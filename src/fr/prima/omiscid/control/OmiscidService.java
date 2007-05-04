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

import java.io.IOException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import fr.prima.omiscid.com.BipUtils;
import fr.prima.omiscid.com.TcpClient;
import fr.prima.omiscid.com.XmlMessage;
import fr.prima.omiscid.com.interf.BipMessageListener;
import fr.prima.omiscid.control.interf.GlobalConstants;
import fr.prima.omiscid.control.interf.VariableChangeListener;
import fr.prima.omiscid.control.message.answer.ControlAnswer;
import fr.prima.omiscid.control.message.answer.ControlAnswerItem;
import fr.prima.omiscid.control.message.answer.ControlEvent;
import fr.prima.omiscid.control.message.answer.Variable;
import fr.prima.omiscid.control.message.answer.types.CA_LockResultType;
import fr.prima.omiscid.control.message.query.ControlQuery;
import fr.prima.omiscid.control.message.query.ControlQueryItem;
import fr.prima.omiscid.control.message.query.FullDescription;
import fr.prima.omiscid.control.message.query.Subscribe;
import fr.prima.omiscid.control.message.query.Unsubscribe;
import fr.prima.omiscid.dnssd.interf.DNSSDFactory;
import fr.prima.omiscid.dnssd.interf.ServiceInformation;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.util.Utility;
import fr.prima.omiscid.user.variable.VariableAccessType;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

/**
 * Encapsulates the data about a <b>remote</b> service. Service instantiation is done by
 * instantiating the {@link ControlServer} class. Contains the data extracted
 * from DNS-SD. Provides convenient access to these data. Provides also some
 * utility methods to generate ids for OMiSCID service (used in BIP exchange)
 * and to clean the names read from DNS-SD.
 *
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
public class OmiscidService {
    
    /** Type for the registration */
    public static String REG_TYPE() { return REG_TYPE; }
    private static String REG_TYPE = GlobalConstants.dnssdDefaultWorkingDomain;
    static {
        try {
            // \REVIEWTASK this variable name should be documented somewhere
            if (null != System.getenv(GlobalConstants.dnssdWorkingDomainEnvironmentVariableName)) {
                REG_TYPE = System.getenv(GlobalConstants.dnssdWorkingDomainEnvironmentVariableName);
            }
        } catch (SecurityException e) {
            // Access to environment variable is forbidden
            System.err.println("Warning: access to environment variables is forbidden.");
        }
    };
    
    public static DNSSDFactory dnssdFactory = DNSSDFactory.DefaultFactory.instance();
    
    /** service id used to create the control client */
    private int peerId = 0;
    
    private enum QueryState {UNQUERIED, QUERIED, FAILED, RECEIVED};
    private QueryState queryState = QueryState.UNQUERIED;
    
    private final Map<String, VariableAttribute> variables = new HashMap<String, VariableAttribute>();
    private final Map<String, InOutputAttribute> connectors = new HashMap<String, InOutputAttribute>();
    private final Vector<String> variableSubscriptions = new Vector<String>();
    
    /**
     * Object used to synchronize the access to the controlClient, and the
     * number of user
     */
    private final Object controlClientSync = new Object();
    
    /** A control client to interrogate the service */
    private ControlClient ctrlClient = null;
    
    /** Number of current user for the control client */
    private int nbUserForControl = 0;
    
    /** DNS-SD informations about the service */
    private ServiceInformation serviceInformation;
    
    /**
     * Creates a new instance of OmiscidService with the given service
     * information.
     *
     * @param peerId
     *            the peer id used to represent the local peer in bip
     *            connections with the remote service
     * @param serviceInformation
     *            service information containing the data from DNS-SD
     */
    public OmiscidService(int peerId, ServiceInformation serviceInformation) {
        this.serviceInformation = serviceInformation;
        this.peerId = peerId;
        parseServiceInformation();
    }
    
    /**
     * Creates an instance of OmiscidService with the given service information.
     * No local peer id is specified (unlike in
     * {@link #OmiscidService(int, ServiceInformation)), and it must be
     * specified with {@link #setServiceId(int)} if the OmiscidService is
     * intended to be used to make connections to the remote peer (explicitly or
     * implicitly).
     *
     * @param serviceInformation
     *            service information containing the data from DNS-SD
     */
    public OmiscidService(ServiceInformation serviceInformation) {
        this.serviceInformation = serviceInformation;
        parseServiceInformation();
    }
    
    /**
     * Sets the peer id to use to represent the local peer in BIP exchanges with
     * the remote service.
     *
     * @param peerId
     *            the id to use
     * @see OmiscidService#initControlClient()
     */
    public void setServiceId(int peerId) {
        if (this.peerId != 0) {
            if (this.peerId != peerId) {
                System.err.println("Warning: peer id already set in OmiscidService (was " + Utility.intTo8HexString(this.peerId) + "), setting anyway (to "+Utility.intTo8HexString(peerId)+")");
            } else {
                System.err.println("Warning: useless setting of OmiscidService to the same value (" + Utility.intTo8HexString(this.peerId) + ")");
            }
        }
        this.peerId = peerId;
    }
    
    /**
     * Returns the name of the service (with the bip suffix removed and spaces
     * replaced).
     *
     * @return {@link #getSimplifiedName()}
     */
    public String toString() {
        return getSimplifiedName();
    }
    
    /**
     * Extracts owner name from the text record
     *
     * @return the owner name, or "" if the owner property is not defined
     */
    public String getOwner() {
        return getVariableValue(GlobalConstants.constantNameForOwner);
        /*
        String str = serviceInformation.getStringProperty(GlobalConstants.constantNameForOwner);
        if (str != null) {
            return VariableAccessType.realValueFromDnssdValue(str);
        } else {
            // should do as getRemotePeerId does
            return "";
        }*/
    }
    
    /**
     * Extracts the remote peer id from the text record or by querying the
     * remote control server.
     *
     * @return the BIP peer id of the remote service
     */
    public int getRemotePeerId() {
        String str = OmiscidService.cleanName(serviceInformation.getFullName());
        //        String str = serviceInformation.getStringProperty(GlobalConstants.constantNameForPeerId);
        if (str != null) {
            return Utility.hexStringToInt(str);
            //            return Utility.hexStringToInt(VariableAccessType.realValueFromDnssdValue(str));
        } else {
            ControlClient ctrlClient = initControlClient();
            if (ctrlClient != null) {
                int pid = ctrlClient.getPeerId();
                closeControlClient();
                return pid;
            }
            return 0;
        }
    }
    
    /**
     * Initializes a new Control Client if it is not already existing.
     * <b>Warning:</b> {@link #closeControlClient()} must be called once for
     * each call to this method that have non-null return value. Increments the
     * number of users, this number is used by closeControlClient. When the user
     * do not use the control client any more, the user must call
     * closeControlClient.
     *
     * @return the control client or null if the creation failed
     * @see OmiscidService#closeControlClient()
     */
    public ControlClient initControlClient() {
        synchronized (controlClientSync) {
            if (peerId == 0) {
                System.err.println("Warning: no local peer id (service id) set in OmiscidService to access remote control server ... generating a new one");
                peerId = BipUtils.generateBIPPeerId();
            }
            if (ctrlClient == null) {
                ctrlClient = new ControlClient(peerId);
                ctrlClient.addControlEventListener(new ControlEventListener() {
                    public void receivedControlEvent(Message message) {
                        try {
                            ControlEvent controlEvent = ControlEvent.unmarshal(new InputStreamReader(new ByteArrayInputStream(message.getBuffer())));
                            if (controlEvent.getVariable() != null) {
                                VariableAttribute variableAttribute = findVariable(controlEvent.getVariable().getName());
                                if (variableAttribute != null) {
                                    variableAttribute.setValueStr(controlEvent.getVariable().getValue());
                                }
                            }
                        } catch (MarshalException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (ValidationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
            if (!ctrlClient.isConnected()) {
                if (!ctrlClient.connectToControlServer(serviceInformation.getHostName(), serviceInformation.getPort())) {
                    ctrlClient = null;
                }
            }
            if (ctrlClient != null) {
                nbUserForControl++;
            }
            return ctrlClient;
        }
    }
    
    /**
     * Closes the control client when it is no more used. (Must be called even
     * if the connection has already been lost) Decrements the number of current
     * user of control client. If the number reaches 0, the control client is
     * really closed. The control client can be obtained by calling
     * {@link OmiscidService#initControlClient()}
     */
    public void closeControlClient() {
        synchronized (controlClientSync) {
            nbUserForControl--;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    freeControlClient();
                }
            }, 1000); // One second delay before disconnecting
            if (nbUserForControl < 0) {
                System.err.println("Warning: in OmiscidService, to many calls to closeControlClient ... ignoring");
                nbUserForControl = 0;
            }
        }
    }
    
    protected void freeControlClient() {
        synchronized (controlClientSync) {
            if (nbUserForControl == 0) {
                if (ctrlClient != null) {
                    ctrlClient.close();
                }
            }
        }
    }
    
    /**
     * Creates a TCP connection to a connector on the remote service.
     *
     * @param ioa
     *            description associated to the connector on the remote server
     * @param messageListener
     *            a listener to add on this connection
     * @return a new TcpClient object or null if the connection failed
     */
    public TcpClient connectToConnector(InOutputAttribute ioa, BipMessageListener messageListener) {
        if (ioa != null) {
            try {
                TcpClient tcpClient = new TcpClient(peerId);
                if (messageListener != null) {
                    tcpClient.addBipMessageListener(messageListener);
                }
                tcpClient.connectTo(serviceInformation.getHostName(), ioa.getTcpPort());
                return tcpClient;
            } catch (IOException e) {
            }
        }
        return null;
    }
    
    /**
     * Utility method. Cleans a dnssd service name by removing trailing
     * "._bip._tcp.local." and restoring spaces characters. If you have an
     * {@link OmiscidService} instance, the dedicated method
     * {@link #getSimplifiedName()} can be used instead.
     *
     * @param fullName
     *            the service fullname to process
     * @return the cleaned service name
     */
    public static String cleanName(String fullName) {
        return fullName.replaceAll(("." + OmiscidService.REG_TYPE + ".local.?$").replaceAll("[.]", "\\."), "").replaceAll("\\\\032", " ");
    }
    
    public ServiceInformation getServiceInformation() {
        return serviceInformation;
    }
    
    /**
     * Gets the host name as expressed in the DNS-SD service information.
     */
    public String getHostName() {
        return serviceInformation.getHostName();
    }
    
    public int getPort() {
        return serviceInformation.getPort();
    }
    
    /**
     * Gets the full service name as expressed in the DNS-SD service
     * information.
     */
    public String getFullName() {
        return serviceInformation.getFullName();
    }
    
    public String getSimplifiedName() {
        return getVariableValue(GlobalConstants.constantNameForName);
        /*String str = serviceInformation.getStringProperty(GlobalConstants.constantNameForName);
        if (str != null) {
            return VariableAccessType.realValueFromDnssdValue(str);
        } else {
            ControlClient ctrlClient = initControlClient();
            if (ctrlClient != null) {
                VariableAttribute variable = ctrlClient.findVariable(GlobalConstants.constantNameForName);
                String name = "$$$UNKNOWN$$$";
                if (variable != null) {
                    name = variable.getValueStr();
                }
                closeControlClient();
                return name;
            }
            return "";
        }*/
    }
    
    private boolean isServiceInformationDescriptionFull() {
        return GlobalConstants.keyForFullTextRecordFull.equals(serviceInformation.getStringProperty(GlobalConstants.keyForFullTextRecord));
    }

    public InOutputAttribute findConnector(int peerId) {
        if (queryState == QueryState.UNQUERIED) {
            queryCompleteDescription();
        }
        for (InOutputAttribute inOutputAttribute : connectors.values()) {
            if (inOutputAttribute.getPeerId() == peerId) {
                return inOutputAttribute;
            }
        }
        return null;
    }
    
    public boolean updateDescription() {
        return queryCompleteDescription();
    }
    
    private void parseServiceInformation() {
        if (serviceInformation != null) {
            for (String key : serviceInformation.getPropertyKeys()) {
                String property = serviceInformation.getStringProperty(key);
                if (property != null) {
                    VariableAccessType variableAccessType = VariableAccessType.fromDnssdValue(property);
                    if (variableAccessType != null) {
                        String value = VariableAccessType.constantValueFromDnssdValue(property); // null in case of non constants and big constants
                        VariableAttribute res = new VariableAttribute(key, variableAccessType, value);
                        variables.put(key, res);
                        continue;
                    }
                    ConnectorType connectorType = ConnectorType.fromDnssdValue(property);
                    if (connectorType != null) {
                        int value = Integer.valueOf(ConnectorType.realValueFromDnssdValue(property));
                        InOutputAttribute res = new InOutputAttribute(key, connectorType, value);
                        connectors.put(key, res);
                        continue;
                    }
                }
            }
        }
    }

    public String getVariableValue(String variableName) {
        if (queryState == QueryState.UNQUERIED) {
            // we have no queried description available, just try to answer with txt record
            String property = serviceInformation.getStringProperty(variableName);
            if (property != null) {
                VariableAccessType variableAccessType = VariableAccessType.fromDnssdValue(property);
                if (variableAccessType != null) {
                    // txt record contains such a variable, try to extract its value
                    // this can be done only for constants with reasonable length
                    if (variableAccessType == VariableAccessType.CONSTANT) {
                        String value = VariableAccessType.realValueFromDnssdValue(property);
                        if (value != null) {
                            return value;
                        }
                        // this is a constant but with too lengthy
                    }
                } else {
                    // there is a property but it does not correspond to a variable
                    // safety first so we fallback on the query mode
                    //return null;
                }
            } else {
                // property is null, check whether the service information is full
                if (isServiceInformationDescriptionFull()) {
                    // service does not have such a variable
                    return null;
                }
            }
            // we got no answer using only with txt record, query the value directly
        }
        VariableAttribute variable = variables.get(variableName);
        if (variable != null && variable.getAccess() == VariableAccessType.CONSTANT) {
            // the variable has already been queried (either as part as the whole or individually)
            // it is also constant so we must have its value in cache
            return variable.getValueStr();
        }
        // finally we query the variable and return the obtained value
        queryVariableDescription(variableName);
        variable = variables.get(variableName);
        if (variable != null) {
            return variable.getValueStr();
        } else {
            // we queried it but it is not here ...
            return null;
        }
    }
    
    /**
     * Finds a variable with a particular name.
     *
     * @param name
     *            the name to look for
     * @return the VariableAttribute object if found, null otherwise
     */
    public VariableAttribute findVariable(String name) {
        VariableAttribute variable = variables.get(name);
        if (variable != null) {
            return variable;
        } else {
            if (queryState == QueryState.UNQUERIED && !isServiceInformationDescriptionFull()) {
                queryCompleteDescription();
                return variables.get(name);
            } else {
                return null;
            }
        }
    }
    
    public InOutputAttribute findConnector(String name, ConnectorType targetType) {
        InOutputAttribute res = findConnector(name);
        return res != null && res.getConnectorType() == targetType ? res : null;
    }
    public InOutputAttribute findConnector(String name) {
        InOutputAttribute attribute = connectors.get(name);
        if (attribute != null) {
            return attribute;
        } else {
            if (queryState == QueryState.UNQUERIED && !isServiceInformationDescriptionFull()) {
                queryCompleteDescription();
                return connectors.get(name);
            } else {
                return null;
            }
        }
    }
    
    public InOutputAttribute findInput(String name) {
        return findConnector(name, ConnectorType.INPUT);
    }
    public InOutputAttribute findOutput(String name) {
        return findConnector(name, ConnectorType.OUTPUT);
    }
    public InOutputAttribute findInOutput(String name) {
        return findConnector(name, ConnectorType.INOUTPUT);
    }
    

    
    private ControlAnswer queryToServer(ControlQuery controlQuery, boolean expectAnswer)
    throws MarshalException, ValidationException {
        initControlClient();
        if (ctrlClient != null) {
            ControlAnswer res = ctrlClient.queryToServer(controlQuery, expectAnswer);
            closeControlClient();
            return res;
        }
        return null;
    }
    
    /**
     * Queries a complete description of the remote service. Warning:
     * {@link #queryGlobalDescription()} must have been called before calling
     * this method. This description contains the names and descriptions of all
     * variables and attributes.
     */
    public boolean queryCompleteDescription() {
        System.out.println("query");
        ControlQuery controlQuery = new ControlQuery();
        FullDescription fullDescription = new FullDescription();
        ControlQueryItem controlQueryItem = new ControlQueryItem();
        controlQueryItem.setFullDescription(fullDescription);
        controlQuery.addControlQueryItem(controlQueryItem);
        
        try {
            queryState = QueryState.QUERIED;
            ControlAnswer controlAnswer = queryToServer(controlQuery, true);
            if (controlAnswer != null) {
                for (ControlAnswerItem item : controlAnswer.getControlAnswerItem()) {
                    processControlAnswerItem(item);
                }
                queryState = QueryState.RECEIVED;
                return true;
            } else {
                queryState = QueryState.FAILED;
                return false;
            }
        } catch (MarshalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ValidationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    private void processControlAnswerItem(ControlAnswerItem item) {
        Object choice = item.getChoiceValue();
        if (choice instanceof fr.prima.omiscid.control.message.answer.Variable) {
            processVariableDescription(item.getVariable());
        } else if (choice instanceof fr.prima.omiscid.control.message.answer.Inoutput) {
            processInOutputDescription(item, item.getInoutput().getName());
        } else if (choice instanceof fr.prima.omiscid.control.message.answer.Output) {
            processInOutputDescription(item, item.getOutput().getName());
        } else if (choice instanceof fr.prima.omiscid.control.message.answer.Input) {
            processInOutputDescription(item, item.getInput().getName());
        } else {
            System.err.println("unhandled answer element "+choice);
        }
    }
    
    /**
     * Queries a complete description for a variable.
     *
     * @param name
     *            the variable name
     * @return a VariableAttribute object that contains the description, null if
     *         the request failed
     */
    public VariableAttribute queryVariableDescription(String name) {
        ControlQuery controlQuery = new ControlQuery();
        fr.prima.omiscid.control.message.query.Variable variable = new fr.prima.omiscid.control.message.query.Variable();
        variable.setName(name);
        ControlQueryItem controlQueryItem = new ControlQueryItem();
        controlQueryItem.setVariable(variable);
        controlQuery.addControlQueryItem(controlQueryItem);
        
        try {
            ControlAnswer controlAnswer = queryToServer(controlQuery, true);
            if (oneVariableAnswer(controlAnswer)) {
                processVariableDescription(controlAnswer.getControlAnswerItem(0).getVariable());
                return variables.get(name);
            }
        } catch (MarshalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ValidationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Asks for the modification of the value of a variable.
     *
     * @param name
     *            the varaible name
     * @param value
     *            the new value for the variable
     * @return a VariableAttribute object with the value of the variable after
     *         the request.
     */
    public boolean queryVariableModification(String name, String value) {
        ControlQuery controlQuery = new ControlQuery();
        fr.prima.omiscid.control.message.query.Variable variable = new fr.prima.omiscid.control.message.query.Variable();
        variable.setName(name);
        variable.setValue(value);
        ControlQueryItem controlQueryItem = new ControlQueryItem();
        controlQueryItem.setVariable(variable);
        controlQuery.addControlQueryItem(controlQueryItem);
        try {
            ControlAnswer controlAnswer = queryToServer(controlQuery, true);
            if (oneVariableAnswer(controlAnswer)) {
                processVariableDescription(controlAnswer.getControlAnswerItem(0).getVariable());
                return true;
            }
        } catch (MarshalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ValidationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    
    private static boolean oneVariableAnswer(ControlAnswer controlAnswer) {
        return controlAnswer != null && controlAnswer.getControlAnswerItemCount() == 1 && controlAnswer.getControlAnswerItem(0).getChoiceValue() instanceof Variable;
    }

    public boolean subscribe(String varName, VariableChangeListener variableChangeListener) {
        initControlClient();
        if (ctrlClient != null) {
            VariableAttribute va = findVariable(varName);
            if (va != null) {
                if (variableSubscriptions.contains(varName)) {
                    variableSubscriptions.add(varName);
                    va.addListenerChange(variableChangeListener);
                    return true;
                } else {
                    boolean subscribe = subscribe(varName);
                    if (!subscribe) {
                        closeControlClient();
                    } else {
                        variableSubscriptions.add(varName);
                        va.addListenerChange(variableChangeListener);
                    }
                    return subscribe;
                }
            } else {
                closeControlClient();
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean unsubscribe(String varName, VariableChangeListener varListener) {
        VariableAttribute va = findVariable(varName);
        if (va != null) {
            variableSubscriptions.remove(varName);
            if (variableSubscriptions.contains(varName)) {
                va.removeListenerChange(varListener);
                return true;
            } else {
                boolean unsubscribe = unsubscribe(varName);
                if (unsubscribe) {
                    va.removeListenerChange(varListener);
                    closeControlClient();
                }
                return unsubscribe;
            }
        } else {
            return false;
        }
    }

    /**
     * Subscribes to the modifications of a particular variable. The
     * modification notifications will be received in ControlEvent. Warning:
     * before calling this method, a complete description must have been queried
     * via {@link #queryCompleteDescription()} itself requiring a call to
     * {@link #queryGlobalDescription()}.
     *
     * @param varName
     *            the name of the variable
     * @return false if the variable is not known
     */
    public boolean subscribe(String varName) {
        VariableAttribute va = findVariable(varName);
        if (va != null) {
            ControlQuery controlQuery = new ControlQuery();
            ControlQueryItem controlQueryItem = new ControlQueryItem();
            Subscribe subscribe = new Subscribe();
            subscribe.setName(varName);
            controlQueryItem.setSubscribe(subscribe);
            controlQuery.addControlQueryItem(controlQueryItem);
            //          String request = "<subscribe name=\"" + va.getName() + "\"/>";
            //          queryToServer(request, false);
            try {
                ControlAnswer controlAnswer = queryToServer(controlQuery, true);
                if (oneVariableAnswer(controlAnswer)) {
                    processVariableDescription(controlAnswer.getControlAnswerItem(0).getVariable());
                    return true;
                }
            } catch (MarshalException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ValidationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        } else {
            System.err.println("variable unknown by client\n");
            return false;
        }
    }
    
    /**
     * Unsubscribes to the modification of a particular variable. The
     * modification notifications will not be received in ControlEvent any more.
     *
     * @param varName
     *            the name of the variable
     * @return false if the variable is not known
     */
    public boolean unsubscribe(String varName) {
        VariableAttribute va = findVariable(varName);
        if (va != null) {
            ControlQuery controlQuery = new ControlQuery();
            ControlQueryItem controlQueryItem = new ControlQueryItem();
            Unsubscribe unsubscribe = new Unsubscribe();
            unsubscribe.setName(varName);
            controlQueryItem.setUnsubscribe(unsubscribe );
            controlQuery.addControlQueryItem(controlQueryItem );
            //          String request = "<unsubscribe name=\"" + va.getName() + "\"/>";
            //          queryToServer(request, false);
            try {
                queryToServer(controlQuery, false);
            } catch (MarshalException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ValidationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return true;
        } else {
            System.err.println("variable unknown by client\n");
            return false;
        }
    }
    
    /**
     * Asks to lock the control server
     *
     * @return whether the control server was locked for this service
     */
    public boolean lock() {
        ControlQuery controlQuery = new ControlQuery();
        ControlQueryItem controlQueryItem = new ControlQueryItem();
        controlQueryItem.setLock(new fr.prima.omiscid.control.message.query.Lock());
        controlQuery.addControlQueryItem(controlQueryItem);
        try {
            ControlAnswer controlAnswer = queryToServer(controlQuery, true);
            if (controlAnswer != null) {
                int peer = Utility.hexStringToInt(controlAnswer.getControlAnswerItem(0).getLock().getPeer());
                VariableAttribute vattr = findVariable("lock");
                if (vattr != null) {
                    vattr.setValueStr(Integer.toString(peer));
                }
                if (controlAnswer.getControlAnswerItem(0).getLock().getResult().getType() == CA_LockResultType.OK_TYPE) {
                    if (peer != peerId) {
                        System.err.println("Lock ok, but id different : " + peer + " != " + peerId);
                        return false;
                    }
                    return true;
                }
            }
        } catch (MarshalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ValidationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Asks to unlock the control server.
     *
     * @return whether the control server was unlocked
     */
    public boolean unlock() {
        ControlQuery controlQuery = new ControlQuery();
        ControlQueryItem controlQueryItem = new ControlQueryItem();
        controlQueryItem.setUnlock(new fr.prima.omiscid.control.message.query.Unlock());
        controlQuery.addControlQueryItem(controlQueryItem);
        try {
            ControlAnswer controlAnswer = queryToServer(controlQuery, true);
            if (controlAnswer != null) {
                int peer = Utility.hexStringToInt(controlAnswer.getControlAnswerItem(0).getUnlock().getPeer());
                
                VariableAttribute vattr = findVariable("lock");
                if (vattr != null) {
                    vattr.setValueStr(Integer.toString(peer));
                }
                if (controlAnswer.getControlAnswerItem(0).getUnlock().getResult().getType() == CA_LockResultType.OK_TYPE) {
                    if (peer != 0) {
                        System.err.println("unlock ok, but id no null : " + peer);
                    }
                    return true;
                }
            }
        } catch (MarshalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ValidationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    
    private void processVariableDescription(fr.prima.omiscid.control.message.answer.Variable variable) {
        VariableAttribute variableAttribute = variables.get(variable.getName());
        if (variableAttribute == null) {
            variables.put(variable.getName(), new VariableAttribute(variable));
        } else {
            variableAttribute.init(variable);
        }
    }
    private void processInOutputDescription(ControlAnswerItem item, String itemName) {
        InOutputAttribute attribute = connectors.get(itemName);
        if (attribute == null) {
            connectors.put(itemName, new InOutputAttribute(item));
        } else {
            attribute.init(item);
        }
    }

    private Set<String> filteredCopy(Map<String, InOutputAttribute> things, ConnectorType targetType) {
        Set<String> res = new HashSet<String>();
        for (Map.Entry<String,InOutputAttribute> entry : things.entrySet()) {
            if (entry.getValue().getConnectorType() == targetType) {
                res.add(entry.getKey());
            }
        }
        return res;
    }
    public Set<String> getInOutputNamesSet() {
        if (queryState == QueryState.UNQUERIED && !isServiceInformationDescriptionFull()) {
            queryCompleteDescription();
        }
        return filteredCopy(connectors, ConnectorType.INOUTPUT);
    }
    public Set<String> getInputNamesSet() {
        if (queryState == QueryState.UNQUERIED && !isServiceInformationDescriptionFull()) {
            queryCompleteDescription();
        }
        return filteredCopy(connectors, ConnectorType.INPUT);
    }
    public Set<String> getOutputNamesSet() {
        if (queryState == QueryState.UNQUERIED && !isServiceInformationDescriptionFull()) {
            queryCompleteDescription();
        }
        return filteredCopy(connectors, ConnectorType.OUTPUT);
    }
    public Set<String> getVariableNamesSet() {
        if (queryState == QueryState.UNQUERIED && !isServiceInformationDescriptionFull()) {
            queryCompleteDescription();
        }
        Set<String> res = new HashSet<String>();
        res.addAll(variables.keySet());
        return res;
    }
    
}
