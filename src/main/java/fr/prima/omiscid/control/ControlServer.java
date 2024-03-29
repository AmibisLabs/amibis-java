/**
 * Copyright© 2005-2006 INRIA/Université Pierre Mendès-France/Université Joseph Fourier.
 *
 * O3MiSCID (aka OMiSCID) Software written by Sebastien Pesnel, Dominique
 * Vaufreydaz, Patrick Reignier, Remi Emonet and Julien Letessier.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package fr.prima.omiscid.control;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import fr.prima.omiscid.com.BipUtils;
import fr.prima.omiscid.com.MessageManager;
import fr.prima.omiscid.com.TcpClientServer;
import fr.prima.omiscid.com.TcpServer;
import fr.prima.omiscid.control.interf.GlobalConstants;
import fr.prima.omiscid.control.interf.VariableChangeListener;
import fr.prima.omiscid.control.interf.VariableChangeQueryListener;
import fr.prima.omiscid.dnssd.interf.DNSSDFactory;
import fr.prima.omiscid.dnssd.interf.ServiceRegistration;
import fr.prima.omiscid.generated.control.ControlAnswer;
import fr.prima.omiscid.generated.control.ControlEvent;
import fr.prima.omiscid.generated.control.ControlQuery;
import fr.prima.omiscid.generated.control.IOType;
import fr.prima.omiscid.generated.control.InOutput;
import fr.prima.omiscid.generated.control.Input;
import fr.prima.omiscid.generated.control.Lock;
import fr.prima.omiscid.generated.control.LockResult;
import fr.prima.omiscid.generated.control.ObjectFactory;
import fr.prima.omiscid.generated.control.Output;
import fr.prima.omiscid.generated.control.Variable;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.util.impl.Constants;
import fr.prima.omiscid.user.util.Utility;
import fr.prima.omiscid.user.variable.VariableAccessType;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a "local" OmiscidService. To access remote service, use the
 * {@link OmiscidService} class. Creates a control connector and register the
 * service as a OMiSCID service on DNS-SD. The service has a variable to give
 * its state, the number of variable, the number of inputs/outputs. The control
 * connector exposes the list of variables and the list of in/outputs of the
 * service. The descriptions of variables and inputs are stored in object called
 * VariableAttribute and InOutputAttribute. These data can be consulted by xml
 * query on the control port (through {@link OmiscidService}). The control
 * server has three possibles status:
 * <ul>
 * <li>BEGIN: the service is not registered yet </li>
 * <li>INIT: the service is registered, and can wait for data or other service
 * to begin processing </li>
 * <li>RUNNING : the service is running : the service computes data.</li>
 * </ul>
 * Two methods needs to be reimplemented to manage correctly some queries. These
 * methods are {@link #connectionQuery(String, int, boolean, InOutputAttribute)}
 * and {@link #variableModificationQuery(String, VariableAttribute)}. The
 * first is called when an in/output is asked to connect a particular port, the
 * second is called when there is a query for the modification of a variable.
 * Example use:
 * <ul>
 * <li>Create a new instance of control server with the name of the service.
 * Eventually, you can implemented the methods connect and modifVariable if
 * necessary.</li>
 * <li> Register all the variable, and the in/outputs.</li>
 * <li> Start the service {@link #startServer(int)}. You can process query to
 * the control server in a processing loop (then call {@link #processMessages()}
 * in your loop), or in another thread (that can be started using
 * {@link #startProcessMessagesThread()}).</li>
 * <li> Wait for data or other service. When you are ready, set the status value
 * to RUNNING and start processing </li>
 * </ul>
 *
 * @see fr.prima.omiscid.control.ControlClient
 * @see fr.prima.omiscid.control.VariableAttribute
 * @see fr.prima.omiscid.control.InOutputAttribute
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
// \REVIEWTASK should externalize some strings to the OmiscidService class
public class ControlServer extends MessageManager implements VariableChangeListener {

    /** the service id used OMiSCID exchange */
    private int peerId = 0;

    /** TCP server : the control server */
    private TcpServer tcpServer = null;

    private VariableAttribute nameVariable;

    /**
     * Set of variable descriptions (Set of VariableAttribute object)
     */
    private Set<VariableAttribute> variablesSet = new HashSet<VariableAttribute>();

    /**
     * Set of inputs and outputs descriptions (Set of InOutputAttribute object)
     */
    private Set<InOutputAttribute> inoutputsSet = new HashSet<InOutputAttribute>();

    /** the variable for the lock attribute */
    private IntVariableAttribute lockIntegerVar = null;

    /** Thread where the message are processed */
    private Thread threadProcessMessages = null;

    /** Object to register the service to DNS-SD */
    protected ServiceRegistration serviceRegistration = null;

    /** Tells the listening thread when to stop */
    private boolean processMessagesThreadRunning;

    private Vector<VariableChangeQueryListener> variableChangeQueryListeners = new Vector<VariableChangeQueryListener>();

    private int inoutputIndexCounter = 1;
    /**
     * Creates a new instance of ControlServer. Its status is BEGIN and its base
     * variables are defined. The service is not registered to DNS-SD. It will
     * only on subsequent call to {@link #startServer(int)}. A BIP peer id is
     * automatically generated and associated to this service. The peer id can
     * be retrieved using {@link #getPeerId()}.
     *
     * @param serviceName
     *            name for the service
     */
    public ControlServer(String serviceName) {
        initDefaultVar();
        setServiceName(serviceName);
    }

    protected void initServiceRegistration() {
        serviceRegistration = DNSSDFactory.DefaultFactory.instance().createServiceRegistration("O3MiSCID_default_name", OmiscidService.REG_TYPE());
    }

    /**
     * Creates a new instance of ControlServer. Its status is BEGIN its service
     * name has a default value. The name should be changed before any call to
     * the {@link #startServer(int)} method. A BIP peer id is automatically
     * generated and associated to this service. The peer id can be retrieved
     * using {@link #getPeerId()}.
     */
    public ControlServer() {
        initDefaultVar();
    }

    /**
     * Unregisters the service from DNS-SD.
     */
    public void stop() {
        serviceRegistration.unregister();
        processMessagesThreadRunning = false;
        if (tcpServer != null) {
            tcpServer.close();
        }
    }

    /**
     * Changes the service name. Must be called before service registration,
     * that is to say before calling {@link #startServer(int)}.
     */
    public void setServiceName(String serviceName) {
//        serviceRegistration.setName(serviceName);
        nameVariable.setValueStr(serviceName);
    }

    /**
     * Creates the default variables for a service
     */
    private void initDefaultVar() {
        VariableAttribute lockVar = addVariable(GlobalConstants.variableNameForLock);
        lockIntegerVar = new IntVariableAttribute(lockVar, 0);
        // override what is defined in IntVariableAttribute
        lockVar.setDescription(GlobalConstants.variableDescriptionForLock);
        lockVar.setType(GlobalConstants.variableTypeForLock);

        // Generate a (at worse) temporary peer id hoping it will be unique
        // At the time of registration #start, it will be tried first and finally will be changed
        // if this peer id is already taken by someone else
        peerId = BipUtils.generateBIPPeerId();
        
//        VariableAttribute peerIdVariable = addVariable(GlobalConstants.constantNameForPeerId);
//        peerIdVariable.setValueStr(Utility.intTo8HexString(peerId));
//        peerIdVariable.setAccessType(VariableAccessType.CONSTANT);

        VariableAttribute ownerVariable = addVariable(GlobalConstants.constantNameForOwner);
        ownerVariable.setDescription(GlobalConstants.constantDescriptionForOwner);
        ownerVariable.setType(GlobalConstants.constantTypeForOwner);
        ownerVariable.setValueStr(System.getProperty("user.name"));
        ownerVariable.setAccessType(VariableAccessType.CONSTANT);

        VariableAttribute classVariable = addVariable(GlobalConstants.constantNameForClass);
        classVariable.setDescription(GlobalConstants.constantDescriptionForClass);
        classVariable.setType(GlobalConstants.constantTypeForClass);
        classVariable.setValueStr(GlobalConstants.defaultServiceClassValue);
        classVariable.setAccessType(VariableAccessType.CONSTANT);

        nameVariable = addVariable(GlobalConstants.constantNameForName);
        nameVariable.setDescription(GlobalConstants.constantDescriptionForName);
        nameVariable.setType(GlobalConstants.constantTypeForName);
        nameVariable.setValueStr("***Unnamed***");
        nameVariable.setAccessType(VariableAccessType.CONSTANT);
    }

    /**
     * Accesses the TCP server.
     *
     * @return tcpServer: the control server over TCP
     */
    public TcpServer getTcpServer() {
        return tcpServer;
    }

    public List<String> getUnsetVariables() {
        List<String> res = new ArrayList<String>();
        for (VariableAttribute var : variablesSet) {
            if (null == var.getValueStr()) {
                res.add(var.getName());
            }
        }
        return res;
    }

    /**
     * Starts the control server and register the service to DNS-SD. If all went
     * fine, the status become INIT.
     *
     * @param port
     *            the port number where the control server should be listening
     *            (0 to choose automatically an available port).
     * @return whether the server was correctly started and was the service
     *         registered.
     */
    public boolean startServer(int port) {
        try {
            tcpServer = new TcpServer(getPeerId(), port);

            // register the service
            if (registerTheService(tcpServer.getTcpPort())) {
                tcpServer.setPeerId(getPeerId());
                for (InOutputAttribute io : inoutputsSet) {
                    int connectorIndex = Utility.PeerId.connectorIndexFromPeerId(io.getPeerId());
                    io.setPeerId(Utility.PeerId.connectorPeerIdFromIndex(getPeerId(), connectorIndex));
                    assert Utility.PeerId.rootPeerIdFromConnectorPeerId(io.getPeerId()) == peerId;
                }
                tcpServer.addBipMessageListener(this);
                tcpServer.start();
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            tcpServer = null;
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Registers the service to DNS-SD. Creates the text containing: owner,
     * peerId, list of input/output.
     *
     * @param host
     *            the host where the service is started
     * @param port
     *            the port number the control port listens to
     * @return whether the service was correctly registered
     */
    private boolean registerTheService(int port) {
        boolean incomplete = false;
        boolean errorOccured = true;
        Vector<String> blackList = new Vector<String>();
        while (errorOccured) {
            errorOccured = false;
            initServiceRegistration();
            serviceRegistration.addProperty(GlobalConstants.keyForFullTextRecord, GlobalConstants.keyForFullTextRecordFull);
            Vector<VariableAttribute> filteredVariableSet = new Vector<VariableAttribute>();
            {
                filteredVariableSet.addAll(variablesSet);
                for (String rem : blackList) {
                    VariableAttribute toRemove = findVariable(rem);
                    filteredVariableSet.remove(toRemove);
                }
                for (String variableName : GlobalConstants.specialVariablesNames) {
                    VariableAttribute variable = findVariable(variableName);
                    if (variable != null) {
                        filteredVariableSet.remove(variable);
                        String prefix = variable.getAccess().getPrefixInDnssd();
                        if (variable.getAccess() != VariableAccessType.CONSTANT) {
                            serviceRegistration.addProperty(variable.getName(), prefix);
                        } else {
                            serviceRegistration.addProperty(variable.getName(), prefix + variable.getValueStr());
                        }
                    }
                }
                Collections.sort(filteredVariableSet, new Comparator<VariableAttribute>() {
                    public int compare(VariableAttribute o1, VariableAttribute o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
            }
            for (VariableAttribute variable: filteredVariableSet) {
                if (variable.getAccess() == VariableAccessType.CONSTANT) {
                    String prefix = variable.getAccess().getPrefixInDnssd();
                    if (blackList.contains(variable.getName())) {
                        try {
                            serviceRegistration.addProperty(variable.getName(), Constants.valueForLongConstantsInDnssd);
                        } catch (Exception e) {
                            incomplete = true;
                        }
                    } else {
                        try {
                            serviceRegistration.addProperty(variable.getName(), prefix + variable.getValueStr());
                        } catch (Exception e) {
                            incomplete = true;
                            errorOccured = true;
                            blackList.add(variable.getName());
                            continue;
                        }
                    }
                }
            }
            for (InOutputAttribute connector : inoutputsSet) {
                String prefix = connector.getConnectorType().getPrefixInDnssd();
                try {
                    serviceRegistration.addProperty(connector.getName(), prefix + connector.getTcpPort());
                } catch (Exception e) {
                    incomplete = true;
                }
            }
            for (VariableAttribute variable: filteredVariableSet) {
                if (variable.getAccess() != VariableAccessType.CONSTANT) {
                    String prefix = variable.getAccess().getPrefixInDnssd();
                    try {
                        serviceRegistration.addProperty(variable.getName(), prefix);
                    } catch (Exception e) {
                        incomplete = true;
                    }
                }
            }
        }
        if (incomplete) {
            serviceRegistration.addProperty(GlobalConstants.keyForFullTextRecord, GlobalConstants.keyForFullTextRecordNonFull);
        }
        ServiceRegistration.ServiceNameProducer nameProducer = new ServiceRegistration.ServiceNameProducer() {
            final int initRemaningTries = 5;
            int remainingTries = initRemaningTries;
            public String getServiceName() {
                if (remainingTries < 1) {
                    remainingTries = initRemaningTries;
                    // This reinitialization allows us to retry in the catch
                    return null;
                } else {
                    remainingTries--;
                    if (remainingTries == initRemaningTries-1) {
                        // First tries the randomly generated peerId 
                        return Utility.intTo8HexString(peerId).toLowerCase();
                    } else {
                        // Fall to a new random if the peerId generated in the constructor is already used
                        return Utility.intTo8HexString(BipUtils.generateBIPPeerId()).toLowerCase();
                    }
                }
            }
        };
        boolean registrationDone = false;
        try {
            registrationDone = serviceRegistration.register(port, nameProducer);
            if (!registrationDone) {
                throw new Exception(); 
            }
        } catch (Exception e) {
            initServiceRegistration();
            serviceRegistration.addProperty(GlobalConstants.keyForFullTextRecord, GlobalConstants.keyForFullTextRecordNonFull);
            for (String variableName : GlobalConstants.specialVariablesNames) {
                VariableAttribute variable = findVariable(variableName);
                if (variable != null) {
                    String prefix = variable.getAccess().getPrefixInDnssd();
                    if (variable.getAccess() != VariableAccessType.CONSTANT) {
                        serviceRegistration.addProperty(variable.getName(), prefix);
                    } else {
                        serviceRegistration.addProperty(variable.getName(), prefix + variable.getValueStr());
                    }
                }
            }
            registrationDone = serviceRegistration.register(port, nameProducer);
        }
        if (registrationDone) {
            this.peerId = Utility.hexStringToInt(serviceRegistration.getRegisteredName());
            VariableAttribute peerIdVariable = addVariable(GlobalConstants.constantNameForPeerId);
            peerIdVariable.setDescription(GlobalConstants.constantDescriptionForPeerId);
            peerIdVariable.setType(GlobalConstants.constantTypeForPeerId);
            peerIdVariable.setValueStr(Utility.intTo8HexString(peerId).toLowerCase());
            peerIdVariable.setAccessType(VariableAccessType.CONSTANT);
        }
        return registrationDone;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.prima.omiscid.control.interf.VariableChangeListener#variableChanged(fr.prima.omiscid.control.VariableAttribute)
     */
    public void variableChanged(VariableAttribute variableAttribute) {
        Set<Integer> peersSet = variableAttribute.getPeerInterestedIn();

        if (peersSet.size() > 0) {
            Set<Integer> unreachablePeers = new java.util.HashSet<Integer>();
            ControlEvent controlEvent = new ControlEvent();
//        controlEvent.setId(BipUtils.intTo8HexString(getPeerId()));
            Variable variable = new Variable();
            variable.setValue(variableAttribute.getValueStr());
            variable.setName(variableAttribute.getName());
            controlEvent.setVariable(variable);
            
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                JAXBTools.marshal(controlEvent, new OutputStreamWriter(byteArrayOutputStream));
                byte[] message = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                
                for (Integer peer : peersSet) {
                    if (!tcpServer.sendToOneClient(message, peer.intValue())) {
                        unreachablePeers.add(peer);
                    }
                }
                variableAttribute.removeAllPeers(unreachablePeers);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        //        String message = "<controlEvent>" + variableAttribute.generateValueMessage() + "</controlEvent>";
//        for (Integer peer : peersSet) {
//            if (!tcpServer.sendToOneClient(message.getBytes(), peer.intValue())) {
//                unreachablePeers.add(peer);
//            }
//        }
//        variableAttribute.removeAllPeers(unreachablePeers);
    }

    /**
     * Launches a thread to process the messages, to answer to ControlQuery
     *
     * @return whether the thread has been launched (false if a thread is
     *         already launched to process messages)
     */
    public boolean startProcessMessagesThread() {
        processMessagesThreadRunning = true;
        if (threadProcessMessages == null || !threadProcessMessages.isAlive()) {
            threadProcessMessages = new Thread("Omiscid Control Server Thread") {
                @Override
                public void run() {
                    while (ControlServer.this.processMessagesThreadRunning) {
                        // set an arbitrary timeout to have the thread closed when control server is stopped but no new events arrive (basic case)
                        if (waitForMessages(500)) {
                            processMessages();
                        }
                    }
                }
            };
            threadProcessMessages.start();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Accesses the BIP peer id of this service. This peer id is automatically
     * generated when the ControlServer object is instantiated.
     *
     * @return the BIP peer id associated to this service
     */
    public int getPeerId() {
        return peerId;
    }

    /**
     * Accesses the desired service name provided when constructing this
     * service. After registration, an unique name is affected to the service
     * and can be accessed using {@link #getRegisteredServiceName()}.
     *
     * @return the service name
     */
    public String getServiceName() {
        return nameVariable.getValueStr();
//        return serviceRegistration.getName();
    }

    /**
     * Accesses the name created during registration: available only after
     * registration.
     * 
     * This method is deprecated as there is no more difference between
     * desired name and registered name. Use {@link #getServiceName()} instead.
     *
     * @return the service name after registration
     */
    @Deprecated public String getRegisteredServiceName() {
        return getServiceName();
//        return serviceRegistration.getRegisteredName();
    }

    /**
     * Adds a variable to this service.
     *
     * @param name
     *            name of the variable
     * @return a new VariableAttribute associated to this variable. This return
     *         value can be manipulated to specify the variable description for
     *         example.
     */
    public VariableAttribute addVariable(String name) {
        VariableAttribute v = new VariableAttribute(name);
        v.addListenerChange(this);
        variablesSet.add(v);
        return v;
    }

    /**
     * Adds an input/output to this service.
     *
     * @param name
     *            name of the input / output
     * @param tcpClientServer
     *            the {@link TcpClientServer} instance associated to the
     *            input/output
     * @param ioKind
     *            kind of input/output: input, output, in/output
     * @return a new InOutputAttribute associated to this input/output. This
     *         object can be manipulate to specify the in/output description for
     *         example.
     */
    public InOutputAttribute addInOutput(String name, TcpClientServer tcpClientServer, ConnectorType ioKind) {
        int connectorPeerId = getPeerId() + inoutputIndexCounter;
        // we can have only 255 connectors (from 01 to FF)
        // inoutputIndexCounter starts at 1
        if (inoutputIndexCounter > 255) {
            inoutputIndexCounter--;
            throw new MaxInoutputCountReached();
        }
        inoutputIndexCounter++;
        tcpClientServer.setPeerId(connectorPeerId);
        InOutputAttribute ioa = new InOutputAttribute(name, tcpClientServer, connectorPeerId);
        ioa.setConnectorType(ioKind);
        inoutputsSet.add(ioa);
        return ioa;
    }
    
    public static class MaxInoutputCountReached extends RuntimeException {}

//    /**
//     * Generates a short global description for the service. Used to answer to
//     * global description query.
//     *
//     * @return short global description for the service
//     */
//    protected String generateShortGlobalDescription() {
//        String str = "";
//        for (VariableAttribute variable : variablesSet) {
//            str += variable.generateShortDescription();
//        }
//        for (InOutputAttribute inoutput : inoutputsSet) {
//            str += inoutput.generateShortDescription();
//        }
//        return str;
//    }

    /**
     * Processes connection query received through the control connector. This
     * implementation just prints some info. You can either overide it or
     * implement it.
     *
     * @param host
     *            the host to connect to
     * @param port
     *            the port to connect to
     * @param tcp
     *            whether to connect using tcp
     * @param ioa
     *            the local input/output connector to connect
     */
    protected void connectionQuery(String host, int port, boolean tcp, InOutputAttribute ioa) {
        System.err.println("in connect : " + ioa.getName() + " on " + host + ":" + port);
        // \REVIEWTASK shouldn't this be implemented (or abstract)
    }

    /**
     * Processes a variable modification query received through the control
     * channnel.
     *
     * @param newValue
     *            the new value for the variable
     * @param va
     *            the VaraibleAttribute object associated to the variable to
     *            modify
     */
    private void variableModificationQuery(String newValue, VariableAttribute va) {
        if (va.canBeModified()) {
            setVariableValueWithValidation(newValue, va);
        }
    }
    
    public void setVariableValueWithValidation(String newValue, VariableAttribute va) {
        boolean doModification = newValue == null || !newValue.equals(va.getValueStr());
        if (doModification) {
            for (VariableChangeQueryListener listener : variableChangeQueryListeners) {
                try {
                    if (! listener.isAccepted(va, newValue)) {
                        doModification = false;
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("Omiscid caught an exception thrown by a listener on variable modification control (modification has been considered accepted by this listener), it is shown here:");
                    e.printStackTrace();
                }
            }
            if (doModification) {
                va.setValueStr(newValue);
            }
        }
    }

//    protected void processMessage(Message message) {
//        try {
//            processXMLMessage(new XmlMessage(message));
//        } catch (BipMessageInterpretationException e) {
////            System.err.println("Warning: wrong xml received on control server, the exception stack follows");
////            e.printStackTrace();
//        }
//    }

    protected void processMessage(Message message) {
        try {
            ControlQuery controlQuery = JAXBTools.unmarshal(new InputStreamReader(new ByteArrayInputStream(message.getBuffer())), ControlQuery.class);
            int remoteId = message.getPeerId();
            ControlAnswer controlAnswer = new ControlAnswer();
            controlAnswer.setId(controlQuery.getId());
            if (controlQuery.getFullDescriptionOrInputOrOutput().size() == 0) {
                generateShortGlobalDescription(controlAnswer);
            } else {
                for (Object item : controlQuery.getFullDescriptionOrInputOrOutput()) {
                    if (item instanceof ControlQuery.Input) {
                        IOType answerItem = generateInoutputAnswer(((ControlQuery.Input)item).getName(), ConnectorType.INPUT);
                        if (answerItem != null) {
                            addInputAnswer(controlAnswer, (Input) answerItem);
                        }
                    } else if (item instanceof ControlQuery.Output) {
                        IOType answerItem = generateInoutputAnswer(((ControlQuery.Output)item).getName(), ConnectorType.OUTPUT);
                        if (answerItem != null) {
                            addOutputAnswer(controlAnswer, (Output) answerItem);
                        }
                    } else if (item instanceof ControlQuery.Inoutput) {
                        IOType answerItem = generateInoutputAnswer(((ControlQuery.Inoutput)item).getName(), ConnectorType.INOUTPUT);
                        if (answerItem != null) {
                            addInOutputAnswer(controlAnswer, (InOutput) answerItem);
                        }
                    } else if (item instanceof ControlQuery.Variable) {
                        Variable answerItem = generateVariableAnswerChoice(((ControlQuery.Variable)item), remoteId);
                        if (answerItem != null) {
                            addVariableAnswer(controlAnswer, answerItem);
                        }
                    } else if (item instanceof ControlQuery.Connect) {
                        IOType answerItem = generateConnectAnswer(((ControlQuery.Connect)item));
                        if (answerItem != null) {
                            addIOTypeAnswer(controlAnswer, answerItem);
                        }
                    } else if (item instanceof ControlQuery.Subscribe) {
                        Variable answerItem = generateSubscribeAnswer(((ControlQuery.Subscribe)item).getName(), remoteId, true);
                        if (answerItem != null) {
                            addVariableAnswer(controlAnswer, answerItem);
                        }
                    } else if (item instanceof ControlQuery.Unsubscribe) {
                        Variable answerItem = generateSubscribeAnswer(((ControlQuery.Unsubscribe)item).getName(), remoteId, false);
                        if (answerItem != null) {
                            addVariableAnswer(controlAnswer, answerItem);
                        }
                    } else if (item instanceof ControlQuery.Lock) {
                        Lock answerItem = generateLockAnswer( remoteId);
                        if (answerItem != null) {
                            controlAnswer.getInputOrOutputOrInoutput().add(new ObjectFactory().createControlAnswerLock(answerItem));
                        }
                    } else if (item instanceof ControlQuery.Unlock) {
                        Lock answerItem = generateUnlockAnswer( remoteId);
                        if (answerItem != null) {
                            controlAnswer.getInputOrOutputOrInoutput().add(new ObjectFactory().createControlAnswerUnlock(answerItem));
                        }
                    } else if (item instanceof ControlQuery.FullDescription) {
                        generateFullGlobalDescription(controlAnswer);
                    }
                }
            }
            if (controlAnswer.getInputOrOutputOrInoutput().size() != 0) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                JAXBTools.marshal(controlAnswer, new OutputStreamWriter(byteArrayOutputStream));
                if (!tcpServer.sendToOneClient(byteArrayOutputStream.toByteArray(), message.getPeerId())) {
                    System.err.println("Warning: ControlServer: Send failed: peer not found : " + Utility.intTo8HexString(message.getPeerId()));
                }
                byteArrayOutputStream.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void addIOTypeAnswer(ControlAnswer controlAnswer, IOType answerItem) {
        if (answerItem instanceof Input) {
            addInputAnswer(controlAnswer, (Input)answerItem);
        } else if (answerItem instanceof Output) {
            addOutputAnswer(controlAnswer, (Output)answerItem);
        } else if (answerItem instanceof InOutput) {            
            addInOutputAnswer(controlAnswer, (InOutput)answerItem);
        }
    }

    private void addInputAnswer(ControlAnswer controlAnswer, Input item) {
        controlAnswer.getInputOrOutputOrInoutput().add(new ObjectFactory().createControlAnswerInput(item));
    }

    private void addOutputAnswer(ControlAnswer controlAnswer, Output item) {
        controlAnswer.getInputOrOutputOrInoutput().add(new ObjectFactory().createControlAnswerOutput(item));
    }

    private void addInOutputAnswer(ControlAnswer controlAnswer, InOutput item) {
        controlAnswer.getInputOrOutputOrInoutput().add(new ObjectFactory().createControlAnswerInoutput(item));
    }

    private void addVariableAnswer(ControlAnswer controlAnswer, Variable variable) {
        controlAnswer.getInputOrOutputOrInoutput().add(new ObjectFactory().createControlAnswerVariable(variable));
    }

    private void generateFullGlobalDescription(ControlAnswer controlAnswer) {
        for (VariableAttribute variable : variablesSet) {
            addVariableAnswer(controlAnswer, variable.generateControlAnswer());
        }
        for (InOutputAttribute inoutput : inoutputsSet) {
            String name = inoutput.getName();
            ConnectorType connectorType = inoutput.getConnectorType();
            addIOTypeAnswer(controlAnswer, generateInoutputAnswer(name, connectorType));
        }
    }

    private void generateShortGlobalDescription(ControlAnswer controlAnswer) {
        for (VariableAttribute variable : variablesSet) {
            addVariableAnswer(controlAnswer, variable.generateShortControlAnswer());
        }
        for (InOutputAttribute inoutput : inoutputsSet) {
            addIOTypeAnswer(controlAnswer, inoutput.generateShortControlAnswer());
        }
    }

    private IOType generateInoutputAnswer(String name, ConnectorType connectorType) {
        InOutputAttribute ioa = findInOutput(name, connectorType);
        if (ioa != null) {
            return ioa.generateControlAnswer();
        }
        return null;
    }
    private Variable generateVariableAnswerChoice(ControlQuery.Variable variable, int peerId) {
        VariableAttribute va = findVariable(variable.getName());
        if (va != null) {
            if (variable.getValue() == null) {
                return va.generateControlAnswer();
            } else {
                if (lockOk(peerId) ) {
//                  && va.canBeModified(statusIntegerVariable.getIntValue())
                    variableModificationQuery(variable.getValue(), va);
                }
                return va.generateControlAnswer();
            }
        }
        return null;
    }

    private IOType generateConnectAnswer(ControlQuery.Connect connect) {
        InOutputAttribute ioa = findInOutput(connect.getName(), null);
        if (ioa != null) {
            if (connect.getTcp() != null) {
                connectionQuery(connect.getHost(), connect.getTcp().intValue(), true, ioa);
                return ioa.generateControlAnswer();
            } else {
                System.err.println("unhandled connection query using udp");
                return null;
            }
        }
        return null;
    }

    private Variable generateSubscribeAnswer(String name, int peerId, boolean subscribe) {
        VariableAttribute va = findVariable(name);
        if (va != null) {
            if (subscribe) {
                va.addPeer(peerId);
                return va.generateControlAnswer();
            } else {
                va.removePeer(peerId);
            }
        }
        return null;
    }

    private Lock generateLockAnswer(int peerId) {
        Lock lock = new Lock();
        if (lockOk(peerId)) {
            lockIntegerVar.setIntValue(peerId);
            lock.setResult(LockResult.OK);
        } else {
            lock.setResult(LockResult.FAILED);
        }
        lock.setPeer(Utility.intTo8HexString(lockIntegerVar.getIntValue()).toLowerCase());
        return lock;
    }

    private Lock generateUnlockAnswer(int peerId) {
        Lock unlock = new Lock();
        if (lockOk(peerId)) {
            lockIntegerVar.setIntValue(0);
            unlock.setResult(LockResult.OK);
        } else {
            unlock.setResult(LockResult.FAILED);
        }
        unlock.setPeer(Utility.intTo8HexString(lockIntegerVar.getIntValue()).toLowerCase());
        return unlock;
    }


//    /**
//     * Processes the subcribe/unsubscribe queries.
//     *
//     * @param elt
//     *            piece of XML tree for the subcribe/unsubscribe query
//     * @param pid
//     *            the service id that ask for subscribe
//     * @param subscribe
//     *            true if subscribe query, false if unsubscribe query
//     * @return answer to the query (empty string for no answer)
//     */
//    protected String processSubscribeQuery(Element elt, int pid, boolean subscribe) {
//        Attr attrName = elt.getAttributeNode("name");
//
//        VariableAttribute va = findVariable(attrName.getValue());
//        if (va != null) {
//            if (subscribe) {
//                va.addPeer(pid);
//            } else {
//                va.removePeer(pid);
//            }
//        }
//        return "";
//    }

//    /**
//     * Processes the queries about in/outputs.
//     *
//     * @param elt
//     *            part of the XML tree contained between tag about in/output :
//     *            "input", "output", or "inOutput".
//     * @param connectorType
//     *            input, output, or inOutput
//     * @return the answer to the query
//     */
//    protected String processInOutputQuery(Element elt, ConnectorType connectorType) {
//        Attr attrName = elt.getAttributeNode("name");
//        InOutputAttribute ioa = findInOutput(attrName.getValue(), connectorType);
//        if (ioa != null) {
//            return ioa.generateLongDescription();
//        } else {
//            return "";
//        }
//    }

//    /**
//     * Processes the queries about variables.
//     *
//     * @param elt
//     *            part of the xml tree contained between tag "variable"
//     * @param pid
//     *            id of the peer (origin of the query). Used in case of
//     *            modification to enable or not the modification according to
//     *            the lock attribute
//     * @return the answer to the query, "" if the variable concerner is not
//     *         found
//     */
//    protected String processVariableQuery(Element elt, int pid) {
//        Attr attrName = elt.getAttributeNode("name");
//        if (attrName == null) {
//            System.err.println("Warning: ununderstood query (name requested)");
//        } else {
//            String name = attrName.getValue();
//            VariableAttribute va = findVariable(name);
//            if (va != null) {
//                if (elt.getChildNodes().getLength() == 0) {
//                    return va.generateLongDescription();
//                } else {
//                    Element eltVal = XmlUtils.firstChild(elt, "value");
//                    if (eltVal != null) {
//                        if (lockOk(pid) ) {
////                            && va.canBeModified(statusIntegerVariable.getIntValue())
//                            variableModificationQuery(eltVal.getFirstChild().getNodeValue().getBytes(), va);
//                        }
//                        return va.generateValueMessage();
//                    }
//                }
//            }
//        }
//        return "";
//    }

//    /**
//     * Processes a query for connection.
//     *
//     * @param elt
//     *            the part of xml tree between tag "connect"
//     */
//    protected String processConnectQuery(Element elt) {
//        Attr attrName = elt.getAttributeNode("name");
//
//        if (attrName == null) {
//            System.err.println("Warning: understood query (name requested)\n");
//        } else {
//            String name = attrName.getValue();
//            InOutputAttribute ioa = findInOutput(name, null);
//            if (ioa != null) {
//                boolean foundHost = false;
//                boolean foundPort = false;
//                boolean tcp = true;
//                int port = 0;
//                String host = null;
//
//                NodeList nodeList = elt.getChildNodes();
//                for (int i = 0; i < nodeList.getLength(); i++) {
//                    Node cur = nodeList.item(i);
//                    String curName = cur.getNodeName();
//                    if (curName.equals("host")) {
//                        host = cur.getFirstChild().getNodeValue();
//                        foundHost = true;
//                    } else if (curName.equals("tcp") || curName.equals("udp")) {
//                        tcp = curName.equals("tcp");
//                        foundPort = true;
//                        port = Integer.parseInt(cur.getFirstChild().getNodeValue());
//                    } else {
//                        System.out.println("Warning: in connect query: ignored tag : " + curName);
//                    }
//                }
//                if (foundPort && foundHost) {
//                    connectionQuery(host, port, tcp, ioa);
//                    return ioa.generateConnectAnswer();
//                }
//            }
//        }
//        return "";
//    }

//    /**
//     * Processes queries to lock the control server.
//     *
//     * @param elt
//     *            part of XML containing the query
//     * @param pid
//     *            id of peer that asks to lock the control server
//     * @return the result of the query : &lt;lock result="res" peer="id" /&gt;
//     *         where res has the value ok or failed and id the id of the peer
//     *         thats currently lock the server.
//     */
//    protected String processLockQuery(Element elt, int pid) {
//        String res = null;
//        if (lockOk(pid)) {
//            lockIntegerVar.setIntValue(pid);
//            res = "ok";
//        } else {
//            res = "failed";
//        }
//        return "<lock result=\"" + res + "\" peer=\"" + BipUtils.intTo8HexString(lockIntegerVar.getIntValue()) + "\"/>";
//    }

//    /**
//     * Processes a query to unlock the control server.
//     *
//     * @param elt
//     *            part of XML containing the query
//     * @param pid
//     *            id of peer that asks to unlock the control server
//     * @return the result of the query : &lt;unlock result="res" peer="id" /&gt;
//     *         where res has the value ok or failed and id the id of the peer
//     *         thats currently lock the server. If the query succeeds, the id
//     *         value is 0.
//     */
//    protected String processUnlockQuery(Element elt, int pid) {
//        String res = null;
//        if (lockOk(pid)) {
//            res = "ok";
//            lockIntegerVar.setIntValue(0);
//        } else {
//            res = "failed";
//        }
//        return "<unlock result=\"" + res + "\" peer=\"" + BipUtils.intTo8HexString(lockIntegerVar.getIntValue()) + "\"/>";
//    }

    /**
     * Finds a variable of the service using its name.
     *
     * @param name
     *            name of the variable to look for
     * @return the VariableAttribute object associated to the variable name or
     *         null if not found
     */
    public VariableAttribute findVariable(String name) {
        for (VariableAttribute variable : variablesSet) {
            if (variable.getName().equalsIgnoreCase(name)) {
                return variable;
            }
        }
        return null;
    }

    /**
     * Finds an in/output of the service using its name.
     *
     * @param name
     *            name of the in/output to find
     * @param k
     *            null or a particular kind of the in/output to look for
     * @return the InOutputAttribute object associated to the name or null if
     *         not found
     */
    public InOutputAttribute findInOutput(String name, ConnectorType k) {
        for (InOutputAttribute ioa : inoutputsSet) {
            if (ioa.getName().equals(name) && (k == null || k == ioa.getConnectorType())) {
                return ioa;
            }
        }
        return null;
    }

    /**
     * Checks the server is not locked by a dead connection. Closes the
     * connection if so.
     */
    protected void refreshLock() {
        int peer = lockIntegerVar.getIntValue();
        if (peer != 0) {
            if (!tcpServer.isStillConnected(peer)) {
                lockIntegerVar.setIntValue(0);
            }
        }
    }

    /**
     * Tests if the lock allows to do modification from a particular peer.
     *
     * @param peer
     *            the BIP peer id of the peer to test
     */
    protected boolean lockOk(int peer) {
        refreshLock();
        return (lockIntegerVar.getIntValue() == 0) || (lockIntegerVar.getIntValue() == peer);
    }

//    /**
//     * Service example. A service named essai, with 2 variables (var_1, var_2)
//     * and an output (my_output). var_1 can be modified by the user. var_2 is
//     * regularly modified by the service.
//     */
//    public static void main(String[] args) {
//        int controlPort = 0;
//        String serviceName = "essai";
//        System.out.println("ControlServer creation");
//        ControlServer ctrl = new ControlServer(serviceName) {
//            protected void variableModificationQuery(String newValue, VariableAttribute va) {
//                System.out.println("modif variable " + va.getName() + " <- " + newValue);
//                va.setValueStr(newValue);
//            }
//        };
//
//        System.out.println("add variable");
//        VariableAttribute va = null;
//        va = ctrl.addVariable("var_1");
//        va.setAccessType(VariableAccessType.READ_WRITE);
//        va.setType("integer");
//        va.setDefaultValue("0");
//        va.setDescription("a variable to modify for test");
//        va.setFormatDescription("decimal representation");
//        va.setValueStr("0");
//
//        va = ctrl.addVariable("var_2");
//        va.setDescription("automatically incremented");
//        IntVariableAttribute var2 = new IntVariableAttribute(va, 0);
//
//        System.out.println("add an output");
//        InOutputAttribute ioa = null;
//        fr.prima.omiscid.com.TcpServer tcpServer = null;
//        try {
//            tcpServer = new fr.prima.omiscid.com.TcpServer(ctrl.getPeerId(), 0);
//            tcpServer.start();
//            tcpServer.addBipMessageListener(new fr.prima.omiscid.com.interf.BipMessageListener() {
//                public void receivedBipMessage(fr.prima.omiscid.com.interf.Message m) {
//                    System.out.println("received message");
//                }
//                public void disconnected(int peerId) {
//                    System.out.println("received disconnection notification");
//                }
//            });
//        } catch (java.io.IOException e) {
//            e.printStackTrace();
//        }
//        ioa = ctrl.addInOutput("my output", tcpServer, ConnectorType.OUTPUT);
//        ioa.setDescription("output for test");
//
//        System.out.println("Register, creation control port");
//        ctrl.startServer(controlPort);
//        System.out.println("Thread process message");
//        ctrl.startProcessMessagesThread();
//
//        System.out.println("Control Server Launched : " + ctrl.getTcpServer().getHost() + ":" + ctrl.getTcpServer().getTcpPort());
//        System.out.println("Service registered as " + ctrl.getRegisteredServiceName());
//        String str = "hello";
//        while (true) {
//            try {
//                tcpServer.sendToAllClientsUnchecked(str);
//                var2.increment();
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//            }
//        }
//    }

    public boolean addVariableChangeQueryListener(VariableChangeQueryListener o) {
        return variableChangeQueryListeners.add(o);
    }

    public boolean removeVariableChangeQueryListener(VariableChangeQueryListener o) {
        return variableChangeQueryListeners.remove(o);
    }

    public Iterable<InOutputAttribute> getConnectors() {
        Vector<InOutputAttribute> res = new Vector<InOutputAttribute>();
        for (InOutputAttribute attribute : inoutputsSet) {
            res.add(attribute);
        }
        return res;
    }

}
