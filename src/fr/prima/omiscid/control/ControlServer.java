package fr.prima.omiscid.control;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.prima.omiscid.com.BipMessageInterpretationException;
import fr.prima.omiscid.com.BipUtils;
import fr.prima.omiscid.com.CommunicationServer;
import fr.prima.omiscid.com.MessageManager;
import fr.prima.omiscid.com.TcpServer;
import fr.prima.omiscid.com.XmlMessage;
import fr.prima.omiscid.com.interf.Message;
import fr.prima.omiscid.control.interf.ConnectorType;
import fr.prima.omiscid.control.interf.GlobalConstants;
import fr.prima.omiscid.control.interf.VariableAccessType;
import fr.prima.omiscid.control.interf.VariableChangeListener;
import fr.prima.omiscid.control.interf.VariableChangeQueryListener;
import fr.prima.omiscid.dnssd.interf.ServiceRegistration;

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
 * and {@link #variableModificationQuery(byte[], int, VariableAttribute)}. The
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
    private final int peerId = BipUtils.generateBIPPeerId();

    /** TCP server : the control server */
    private TcpServer tcpServer = null;

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
    private ServiceRegistration serviceRegistration = null;

    /** Tells the listening thread when to stop */
    private boolean processMessagesThreadRunning;

    private Vector<VariableChangeQueryListener> variableChangeQueryListeners = new Vector<VariableChangeQueryListener>();

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
        serviceRegistration = OmiscidService.dnssdFactory.createServiceRegistration(serviceName, OmiscidService.REG_TYPE());
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
        serviceRegistration = OmiscidService.dnssdFactory.createServiceRegistration("O3MiSCID_default_name", OmiscidService.REG_TYPE());
    }

    /**
     * Unregisters the service from DNS-SD.
     */
    public void stop() {
        serviceRegistration.unregister();
        processMessagesThreadRunning = false;
    }

    /**
     * Changes the service name. Must be called before service registration,
     * that is to say before calling {@link #startServer(int)}.
     */
    public void setServiceName(String serviceName) {
        serviceRegistration.setName(serviceName);
    }

    /**
     * Creates the default variables for a service
     */
    private void initDefaultVar() {
        VariableAttribute lockVar = addVariable(GlobalConstants.variableNameForLock);
        lockIntegerVar = new IntVariableAttribute(lockVar, 0);

        VariableAttribute peerIdVariable = addVariable(GlobalConstants.constantNameForPeerId);
        peerIdVariable.setValueStr(BipUtils.intTo8HexString(peerId));
        peerIdVariable.setAccess(VariableAccessType.CONSTANT);

        VariableAttribute ownerVariable = addVariable(GlobalConstants.constantNameForOwner);
        ownerVariable.setValueStr(System.getProperty("user.name"));
        ownerVariable.setAccess(VariableAccessType.CONSTANT);

        VariableAttribute classVariable = addVariable(GlobalConstants.constantNameForClass);
        classVariable.setValueStr(GlobalConstants.defaultServiceClassValue);
        classVariable.setAccess(VariableAccessType.CONSTANT);
    }

    /**
     * Accesses the TCP server.
     *
     * @return tcpServer: the control server over TCP
     */
    public TcpServer getTcpServer() {
        return tcpServer;
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
            tcpServer.addBipMessageListener(this);
            tcpServer.start();

            // register the service
            if (registerTheService(tcpServer.getTcpPort())) {
//                setStatus(STATUS_INIT);
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
        serviceRegistration.addProperty(GlobalConstants.keyForFullTextRecord, GlobalConstants.keyForFullTextRecordFull);
        try {
            for (VariableAttribute variable: variablesSet) {
                if (variable.getAccess() == VariableAccessType.CONSTANT) {
                    String prefix = variable.getAccess().getPrefixInDnssd();
                    serviceRegistration.addProperty(variable.getName(), prefix + variable.getValueStr());
                }
            }
            for (InOutputAttribute connector : inoutputsSet) {
                String prefix = connector.getConnectorType().getPrefixInDnssd();
                serviceRegistration.addProperty(connector.getName(), prefix + connector.getTcpPort());
            }
            for (VariableAttribute variable: variablesSet) {
                if (variable.getAccess() != VariableAccessType.CONSTANT) {
                    String prefix = variable.getAccess().getPrefixInDnssd();
                    serviceRegistration.addProperty(variable.getName(), prefix);
                }
            }
        } catch (Exception e) {
            serviceRegistration.addProperty(GlobalConstants.keyForFullTextRecord, GlobalConstants.keyForFullTextRecordNonFull);
        }
        return serviceRegistration.register(port);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.prima.omiscid.control.interf.VariableChangeListener#variableChanged(fr.prima.omiscid.control.VariableAttribute)
     */
    public void variableChanged(VariableAttribute variable) {
        Set<Integer> peersSet = variable.getPeerInterestedIn();

        Set<Integer> unreachablePeers = new java.util.HashSet<Integer>();
        String message = "<controlEvent>" + variable.generateValueMessage() + "</controlEvent>";

        for (Integer peer : peersSet) {
            if (!tcpServer.sendToOneClient(message.getBytes(), peer.intValue())) {
                unreachablePeers.add(peer);
            }
        }
        variable.removeAllPeers(unreachablePeers);
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
            threadProcessMessages = new Thread() {
                public void run() {
                    while (ControlServer.this.processMessagesThreadRunning) {
                        if (waitForMessages()) {
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
        return serviceRegistration.getName();
    }

    /**
     * Accesses the name created during registration: available only after
     * registration.
     *
     * @return the service name after registration
     */
    public String getRegisteredServiceName() {
        return serviceRegistration.getRegisteredName();
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
     * @param communicationServer
     *            the {@link CommunicationServer} instance associated to the
     *            input/output
     * @param ioKind
     *            kind of input/output: input, output, in/output
     * @return a new InOutputAttribute associated to this input/output. This
     *         object can be manipulate to specify the in/output description for
     *         example.
     */
    public InOutputAttribute addInOutput(String name, CommunicationServer communicationServer, ConnectorType ioKind) {
        InOutputAttribute ioa = new InOutputAttribute(name, communicationServer);
        ioa.setConnectorType(ioKind);
        inoutputsSet.add(ioa);
        return ioa;
    }

    /**
     * Generates a short global description for the service. Used to answer to
     * global description query.
     *
     * @return short global description for the service
     */
    protected String generateShortGlobalDescription() {
        String str = "";
        for (VariableAttribute variable : variablesSet) {
            str += variable.generateShortDescription();
        }
        for (InOutputAttribute inoutput : inoutputsSet) {
            str += inoutput.generateShortDescription();
        }
        return str;
    }

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
     * @param buffer
     *            the new value for the variable
     * @param status
     *            the current service status
     * @param va
     *            the VaraibleAttribute object associated to the variable to
     *            modify
     */
    protected void variableModificationQuery(byte[] buffer, VariableAttribute va) {
        boolean doModification = true;
        for (VariableChangeQueryListener listener : variableChangeQueryListeners) {
            if (! listener.isAccepted(va, new String(buffer))) {
                doModification = false;
                break;
            }
        }
        if (doModification) {
            va.setValueStr(new String(buffer));
        }
    }

    protected void processMessage(Message message) {
        try {
            processXMLMessage(new XmlMessage(message));
        } catch (BipMessageInterpretationException e) {
//            System.err.println("Warning: wrong xml received on control server, the exception stack follows");
//            e.printStackTrace();
        }
    }

    /**
     * Processes a message received by the control server.
     *
     * @param message
     *            a message received by the control server
     */
    protected void processXMLMessage(XmlMessage message) {
        // System.out.println("in ControlServer::processAMessage");
        if (message.getRootElement() != null) {
            Element root = message.getRootElement();
            if (root.getNodeName().equals("controlQuery")) {
                Attr attrId = root.getAttributeNode("id");

                String str = "";
                NodeList nodeList = root.getChildNodes();
                if (nodeList.getLength() == 0) {
                    // global description
                    str = generateShortGlobalDescription();
                } else {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node cur = nodeList.item(i);
                        if (cur.getNodeType() == Node.ELEMENT_NODE) {
                            String curName = cur.getNodeName();
                            if (curName.equals(ConnectorType.INPUT.getXMLTag())) {
                                str += processInOutputQuery((Element) cur, ConnectorType.INPUT);
                            } else if (curName.equals(ConnectorType.OUTPUT.getXMLTag())) {
                                str += processInOutputQuery((Element) cur, ConnectorType.OUTPUT);
                            } else if (curName.equals(ConnectorType.INOUTPUT.getXMLTag())) {
                                str += processInOutputQuery((Element) cur, ConnectorType.INOUTPUT);
                            } else if (curName.equals("variable")) {
                                str += processVariableQuery((Element) cur, message.getPeerId());
                            } else if (curName.equals("connect")) {
                                str += processConnectQuery((Element) cur);
                            } else if (curName.equals("subscribe")) {
                                str += processSubscribeQuery((Element) cur, message.getPeerId(), true);
                            } else if (curName.equals("unsubscribe")) {
                                str += processSubscribeQuery((Element) cur, message.getPeerId(), false);
                            } else if (curName.equals("lock")) {
                                str += processLockQuery((Element) cur, message.getPeerId());
                            } else if (curName.equals("unlock")) {
                                str += processUnlockQuery((Element) cur, message.getPeerId());
                            } else
                                System.err.println("unknow tag : " + curName);
                        }
                    }
                }

                str = "<controlAnswer id=\"" + attrId.getValue() + "\">" + str + "</controlAnswer>";

                if (!tcpServer.sendToOneClient(str.getBytes(), message.getPeerId())) {
                    System.err.println("Warning: ControlServer: Send failed: peer not found : " + BipUtils.intTo8HexString(message.getPeerId()));
                }
            } else {
                System.err.println("Warning: in ControlServer#processAMessage, Unknown Tag: " + message.getRootElement().getNodeName());
            }
        }
    }

    /**
     * Processes the subcribe/unsubscribe queries.
     *
     * @param elt
     *            piece of XML tree for the subcribe/unsubscribe query
     * @param pid
     *            the service id that ask for subscribe
     * @param subscribe
     *            true if subscribe query, false if unsubscribe query
     * @return answer to the query (empty string for no answer)
     */
    protected String processSubscribeQuery(Element elt, int pid, boolean subscribe) {
        Attr attrName = elt.getAttributeNode("name");

        VariableAttribute va = findVariable(attrName.getValue());
        if (va != null) {
            if (subscribe) {
                va.addPeer(pid);
            } else {
                va.removePeer(pid);
            }
        }
        return "";
    }

    /**
     * Processes the queries about in/outputs.
     *
     * @param elt
     *            part of the XML tree contained between tag about in/output :
     *            "input", "output", or "inOutput".
     * @param kind
     *            input, output, or inOutput
     * @return the answer to the query
     */
    protected String processInOutputQuery(Element elt, ConnectorType kind) {
        Attr attrName = elt.getAttributeNode("name");
        InOutputAttribute ioa = findInOutput(attrName.getValue(), kind);
        if (ioa != null) {
            return ioa.generateLongDescription();
        } else {
            return "";
        }
    }

    /**
     * Processes the queries about variables.
     *
     * @param elt
     *            part of the xml tree contained between tag "variable"
     * @param pid
     *            id of the peer (origin of the query). Used in case of
     *            modification to enable or not the modification according to
     *            the lock attribute
     * @return the answer to the query, "" if the variable concerner is not
     *         found
     */
    protected String processVariableQuery(Element elt, int pid) {
        Attr attrName = elt.getAttributeNode("name");
        if (attrName == null) {
            System.err.println("Warning: ununderstood query (name requested)");
        } else {
            String name = attrName.getValue();
            VariableAttribute va = findVariable(name);
            if (va != null) {
                if (elt.getChildNodes().getLength() == 0) {
                    return va.generateLongDescription();
                } else {
                    Element eltVal = XmlUtils.firstChild(elt, "value");
                    if (eltVal != null) {
                        if (lockOk(pid) ) {
//                            && va.canBeModified(statusIntegerVariable.getIntValue())
                            variableModificationQuery(eltVal.getFirstChild().getNodeValue().getBytes(), va);
                        }
                        return va.generateValueMessage();
                    }
                }
            }
        }
        return "";
    }

    /**
     * Processes a query for connection.
     *
     * @param elt
     *            the part of xml tree between tag "connect"
     */
    protected String processConnectQuery(Element elt) {
        Attr attrName = elt.getAttributeNode("name");

        if (attrName == null) {
            System.err.println("Warning: understood query (name requested)\n");
        } else {
            String name = attrName.getValue();
            InOutputAttribute ioa = findInOutput(name, null);
            if (ioa != null) {
                boolean foundHost = false;
                boolean foundPort = false;
                boolean tcp = true;
                int port = 0;
                String host = null;

                NodeList nodeList = elt.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node cur = nodeList.item(i);
                    String curName = cur.getNodeName();
                    if (curName.equals("host")) {
                        host = cur.getFirstChild().getNodeValue();
                        foundHost = true;
                    } else if (curName.equals("tcp") || curName.equals("udp")) {
                        tcp = curName.equals("tcp");
                        foundPort = true;
                        port = Integer.parseInt(cur.getFirstChild().getNodeValue());
                    } else {
                        System.out.println("Warning: in connect query: ignored tag : " + curName);
                    }
                }
                if (foundPort && foundHost) {
                    connectionQuery(host, port, tcp, ioa);
                    return ioa.generateConnectAnswer();
                }
            }
        }
        return "";
    }

    /**
     * Processes queries to lock the control server.
     *
     * @param elt
     *            part of XML containing the query
     * @param pid
     *            id of peer that asks to lock the control server
     * @return the result of the query : &lt;lock result="res" peer="id" /&gt;
     *         where res has the value ok or failed and id the id of the peer
     *         thats currently lock the server.
     */
    protected String processLockQuery(Element elt, int pid) {
        String res = null;
        if (lockOk(pid)) {
            lockIntegerVar.setIntValue(pid);
            res = "ok";
        } else {
            res = "failed";
        }
        return "<lock result=\"" + res + "\" peer=\"" + BipUtils.intTo8HexString(lockIntegerVar.getIntValue()) + "\"/>";
    }

    /**
     * Processes a query to unlock the control server.
     *
     * @param elt
     *            part of XML containing the query
     * @param pid
     *            id of peer that asks to unlock the control server
     * @return the result of the query : &lt;unlock result="res" peer="id" /&gt;
     *         where res has the value ok or failed and id the id of the peer
     *         thats currently lock the server. If the query succeeds, the id
     *         value is 0.
     */
    protected String processUnlockQuery(Element elt, int pid) {
        String res = null;
        if (lockOk(pid)) {
            res = "ok";
            lockIntegerVar.setIntValue(0);
        } else {
            res = "failed";
        }
        return "<unlock result=\"" + res + "\" peer=\"" + BipUtils.intTo8HexString(lockIntegerVar.getIntValue()) + "\"/>";
    }

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
            if (variable.getName().equals(name)) {
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

    /**
     * Service example. A service named essai, with 2 variables (var_1, var_2)
     * and an output (my_output). var_1 can be modified by the user. var_2 is
     * regularly modified by the service.
     */
    public static void main(String[] args) {
        int controlPort = 0;
        String serviceName = "essai";
        System.out.println("ControlServer creation");
        ControlServer ctrl = new ControlServer(serviceName) {
            protected void variableModificationQuery(byte[] buffer, VariableAttribute va) {
                String valueStr = new String(buffer);
                System.out.println("modif variable " + va.getName() + " <- " + valueStr);
                va.setValueStr(valueStr);
            }
        };

        System.out.println("add variable");
        VariableAttribute va = null;
        va = ctrl.addVariable("var_1");
        va.setAccess(VariableAccessType.READ_WRITE);
        va.setType("integer");
        va.setDefaultValue("0");
        va.setDescription("a variable to modify for test");
        va.setFormatDescription("decimal representation");
        va.setValueStr("0");

        va = ctrl.addVariable("var_2");
        va.setDescription("automatically incremented");
        IntVariableAttribute var2 = new IntVariableAttribute(va, 0);

        System.out.println("add an output");
        InOutputAttribute ioa = null;
        fr.prima.omiscid.com.TcpServer tcpServer = null;
        try {
            tcpServer = new fr.prima.omiscid.com.TcpServer(ctrl.getPeerId(), 0);
            tcpServer.start();
            tcpServer.addBipMessageListener(new fr.prima.omiscid.com.interf.BipMessageListener() {
                public void receivedBipMessage(fr.prima.omiscid.com.interf.Message m) {
                    System.out.println("received message");
                }
                public void disconnected() {
                    System.out.println("received disconnection notification");
                }
            });
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        ioa = ctrl.addInOutput("my output", tcpServer, ConnectorType.OUTPUT);
        ioa.setDescription("output for test");

        System.out.println("Register, creation control port");
        ctrl.startServer(controlPort);
        System.out.println("Thread process message");
        ctrl.startProcessMessagesThread();

        System.out.println("Control Server Launched : " + ctrl.getTcpServer().getHost() + ":" + ctrl.getTcpServer().getTcpPort());
        System.out.println("Service registered as " + ctrl.getRegisteredServiceName());
        String str = "hello";
        while (true) {
            try {
                tcpServer.sendToAllClientsUnchecked(str);
                var2.increment();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    public boolean addVariableChangeQueryListener(VariableChangeQueryListener o) {
        return variableChangeQueryListeners.add(o);
    }

    public boolean removeVariableChangeQueryListener(VariableChangeQueryListener o) {
        return variableChangeQueryListeners.remove(o);
    }
}
