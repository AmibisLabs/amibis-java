package fr.prima.bipcontrol ;

import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.prima.bipcom.ComTools;
import fr.prima.bipcom.TcpServer;
import fr.prima.bipcontrol.interf.InOutputKind;
import fr.prima.bipcontrol.interf.VariableChangeListener;
import fr.prima.bipdnssd.interf.ServiceRegistration;


/**
 * Create a control port and register the Service as a BIP service on DNS-SD. The service has a variable to give its state, the number of variable, the number of inputs/outputs. The control has the list of variables and the list of in/outputs for the service. The description of variable and input are stored in object called VariableAttribut and InOutputAttribut. These data can be consulted by xml query on the control port. In that purpose the class ControlClient enables to do these queries  and stores the results. <br> The control server has three possibles status : <ul><li>BEGIN : the service is not registered yet </li><li>INIT : the service is registered, and can wait for data or other service to begin processing </li><li>RUNNING : the service is running : the service computes data.  </li></ul> <br> Two methods needs to be reimplemented to manage correctly some queries. That are connect and modifVariable. The first is called when an in/output is demanded to connect a particular port, the second is called when there is a query for the  modification of a variable. Use: <ul><li> Create a new instance of control server with the name of the service. Eventually, you can implemented the methods connect and modifVariable if necessary. </li><li> Register all the variable, and the in/outputs. </li><li> Start the service (startServer). You can process query to the control server in a processing loop (then call  processMessages()), or in another thread (then call  startThreadProcessMessage()). </li><li> Wait for data or other service. When you are ready, set the status value to RUNNING and start processing </li></ul> An example of server is given in the method main. The service is called "essai". It has two own variables : var_1 and var_2. var_1 can be modified by the user. var_2 is regularly incremented. It has also one output that send regularly to all  connected clients the message "hello".
 * @see fr.prima.bipcontrol.ControlClient
 * @see fr.prima.bipcontrol.VariableAttribut
 * @see fr.prima.bipcontrol.InOutputAttribut
 * @author  Sebastien Pesnel  
 * Refactoring by Patrick Reignier and emonet
 * Adding the stop method to unregister the bip service (Patrick Reignier)
 */
public class ControlServer extends XmlMsgManager implements
        VariableChangeListener {
    /** value for the variable status : when the service begins */
    public static final int STATUS_BEGIN = 0;

    /** value for the variable status : when the service is registered */
    public static final int STATUS_INIT = 1;

    /** value for the variable status : when the service is running */
    public static final int STATUS_RUNNING = 2;

    /** the service id used in BIP exchange */
    private final int serviceId = BipService.generateServiceId();

    /** TCP server : the control server */
    private TcpServer tcpServer = null;

    /**
     * Set of variable descriptions (Set of VariableAttribut object)
     */
    private Set<VariableAttribut> variableSet = new java.util.HashSet<VariableAttribut>();

    /**
     * Set of inputs and outputs descriptions (Set of InOutputAttribut object)
     */
    private Set<InOutputAttribut> inoutputSet = new java.util.HashSet<InOutputAttribut>();

    /** the variable for the service status */
    private IntVariableAttribut statusIntVar = null;
    
    /** the variable for the lock attribute */
    private IntVariableAttribut lockIntVar = null;

    /** the variable for the number of variable in the service */
    private IntVariableAttribut nbvarIntVar = null;

    /** the variable for the number of variable in the service */
    private IntVariableAttribut nbinoutputIntVar = null;

    /** Thread where the message are processed */
    private Thread threadProcessMsg = null;

    /** Object to register the service to DNS-SD */
    private ServiceRegistration serviceRegistration = null;

    /**
     * Create a new instance of ControlServer.
     * The status is BEGIN.
     * 
     * @param serviceName name for the service
     */
    public ControlServer(String serviceName) {
        initDefaultVar();
        
        serviceRegistration = BipService.dnssdFactory.createServiceRegistration(serviceName, BipService.REG_TYPE);
    }
    
    /**
     * Create a new instance of ControlServer.
     * The status is BEGIN.
     * The service has a default name.
     * The name need to be changed before a call to the startServer method.
     */
    public ControlServer() {
        initDefaultVar();
        
        serviceRegistration = BipService.dnssdFactory.createServiceRegistration("default_name", BipService.REG_TYPE);
    }
    
    public void stop()
    {
       serviceRegistration.unregister() ;
    }
    
    /** Enable to change the service name. 
     * Must be called before service registration,
     * that is to say before a call to the startServer method. */
    public void setServiceName(String serviceName){
        serviceRegistration.setName(serviceName);
    }
    
    /** create the default variables for a service */
    private void initDefaultVar(){
        VariableAttribut nbvarVar = addVariable("number of variables");
        nbvarIntVar = new IntVariableAttribut(nbvarVar, 1);

        VariableAttribut nbinoutVar = addVariable("number of inoutputs");
        nbinoutputIntVar = new IntVariableAttribut(nbinoutVar, 0);

        VariableAttribut lockVar = addVariable("lock");
        lockIntVar = new IntVariableAttribut(lockVar, 0);
        
        VariableAttribut statusVar = addVariable("status");
        statusIntVar = new IntVariableAttribut(statusVar, STATUS_BEGIN);
    }

    /**
	 * Access to the TCP server 
	 * @return  tcpServer : the control server over TCP
	 * @uml.property  name="tcpServer"
	 */
    public TcpServer getTcpServer() {
        return tcpServer;
    }
    /** Start the server And register the service to DNS-SD.
     * If ok, the status become INIT.
     * @param port the port number where must listen the control server.
     * (with 0 a free port will be automatically used) 
     * @return if the server is correctly launched and 
     * if the service is correctly registered. */
    public boolean startServer(int port) {
        try {
            tcpServer = new TcpServer(getServiceId(), port);
            tcpServer.addBipMessageListener(this);
            tcpServer.start();

            // register the service
            if (registerTheService(tcpServer.getTcpPort())) {
                setStatus(STATUS_INIT);
                return true;
            } else
                return false;
        } catch (java.io.IOException e) {
            tcpServer = null;
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Register the service to DNS-SD. Creation of the text record : owner,
     * peerId, list of input/output.
     * 
     * @param host
     *            the host where the service is launched
     * @param port
     *            the port number where the control port listens
     * @return is the service is correctly registered
     */
    private boolean registerTheService(int port) {
        String inputRecord = "";
        String outputRecord = "";
        String inOutputRecord = "";

        java.util.Iterator<InOutputAttribut> it = inoutputSet.iterator();
        while (it.hasNext()) {
            InOutputAttribut ioa = it.next();

            serviceRegistration.addProperty(ioa.getName(), ioa
                    .generateRecordData());

            if (ioa.isInput()) {
                inputRecord = inputRecord + ioa.getName() + ",";
            } else if (ioa.isOutput()) {
                outputRecord = outputRecord + ioa.getName() + ",";
            } else if (ioa.isInOutput()) {
                inOutputRecord = inOutputRecord + ioa.getName() + ",";
            }
        }

        if (inputRecord.length() > 0)
            inputRecord = inputRecord.substring(0, inputRecord.length() - 1);
        if (outputRecord.length() > 0)
            outputRecord = outputRecord.substring(0,
                    outputRecord.length() - 1);
        if (inOutputRecord.length() > 0)
            inOutputRecord = inOutputRecord.substring(0, inOutputRecord
                    .length() - 1);

        serviceRegistration.addProperty("owner", System.getProperty("user.name"));

        serviceRegistration.addProperty("peerId", fr.prima.bipcom.MsgSocket.intTo8HexString(serviceId));
        serviceRegistration.addProperty("inputs", inputRecord);
        serviceRegistration.addProperty("outputs", outputRecord);
        serviceRegistration.addProperty("inoutputs", inOutputRecord);

        return serviceRegistration.register(port);
    }

    /**
     * Method of the VariableChangeListener.
     * This method enables to generate ControlEvent to answer to subscribe request.
     * 
     * @param var
     *            The VariableAttribut object associated to the modified
     *            variable
     */
    public void changeOccured(VariableAttribut var) {
        Set<Integer> peerSet = var.getPeerInterestedIn();

        Set<Integer> tmpSet = new java.util.HashSet<Integer>();
        String str = "<controlEvent>" +
            var.generateValueMessage() +
            "</controlEvent>";

        java.util.Iterator<Integer> it = peerSet.iterator();
        while (it.hasNext()) {
            Integer peer = it.next();
            if (!tcpServer.sendToOneClient(str.getBytes(), peer.intValue())) {
                tmpSet.add(peer);
            }
        }

        it = tmpSet.iterator();
        while (it.hasNext()) {
            var.removePeer((Integer) it.next());
        }
    }

    /**
     * Launch a thread to process the message, to answer to ControlQuery
     * 
     * @return false if a thread is already launched to process message
     */
    public boolean startThreadProcessMessage() {
        if (threadProcessMsg == null) {
            threadProcessMsg = new Thread() {
                public void run() {
                    while (true) {                        
                        if (waitForMessage())
                            processMessages();
                    }
                }
            };
            threadProcessMsg.start();
            return true;
        } else
            return false;
    }

    /**
	 * @return  the service id
	 * @uml.property  name="serviceId"
	 */
    public int getServiceId() {
        return serviceId;
    }

    /** @return the service name */
    public String getServiceName(){
        return  serviceRegistration.getName();
    }
    /** Access to the name created during registration : 
     * available only after registration.
     * @return the service name kept in registration */
    public String getRegisteredServiceName(){
        return serviceRegistration.getRegisteredName();
    }
    
    /** @return the status value */
    public int getStatus() {
        return statusIntVar.getIntValue();
    }

    /**
     * change the status value
     * 
     * @param s
     *            the new value for the status
     */
    public void setStatus(int s) {
        statusIntVar.setIntValue(s);
    }

    /**
     * Add a variable for this service
     * 
     * @param name
     *            name of the variable
     * @return a new VariableAttribut associated to this variable. This object
     *         can be used to precise the description
     */
    public VariableAttribut addVariable(String name) {
        VariableAttribut v = new VariableAttribut(name);
        v.addListenerChange(this);
        variableSet.add(v);
        if (nbvarIntVar != null) {
            nbvarIntVar.incr();
        }
        return v;
    }

    /**
     * Add an input/output for this service
     * 
     * @param name
     *            name of the input / output
     * @param ct
     *            the ComTools object associated to the input/output
     * @param iok
     *            kind of input/output : input, output, in/output
     * @return a new InOutputAttribut associated to this input/output. This
     *         object can be used to precise the description
     */
    public InOutputAttribut addInOutput(String name, ComTools ct,
            InOutputKind iok) {
        InOutputAttribut ioa = new InOutputAttribut(name, ct);
        ioa.setKind(iok);
        inoutputSet.add(ioa);

        nbinoutputIntVar.incr();

        return ioa;
    }

    /**
     * Generate a short global description for the service. Use to answer to
     * global description query
     * 
     * @return short global description for the service
     */
    protected String generateShortGlobalDescription() {
        String str = "";
        java.util.Iterator<VariableAttribut> it = variableSet.iterator();
        while (it.hasNext()) {
            str += it.next().generateShortDescription();
        }
        java.util.Iterator<InOutputAttribut> itIo = inoutputSet.iterator();
        while (itIo.hasNext()) {
            str += itIo.next().generateShortDescription();
        }
        return str;
    }

    /** Call by connect query */
    protected void connect(String host, int port, boolean tcp,
            InOutputAttribut ioa) {
        System.out.println("in connect : " + ioa.getName() + " on " + host
                + ":" + port);
    }

    /**
     * Call by modification of variable query
     * 
     * @param buffer
     *            the new value for the variable
     * @param status
     *            the current service status
     * @param va
     *            the VaraibleAttribut object associated to the variable
     */
    protected void modifVariable(byte[] buffer, int status, VariableAttribut va) {
        System.out.println("in modifVariable : " + va.getName());
    }

    /**
     * Process a message received by the control server
     * 
     * @param msg
     *            a message received by the control server
     */
    protected void processAMessage(XmlMessage msg) {
        // System.out.println("in ControlServer::processAMessage");
        if (msg.getRootNode() != null) {
            Element root = msg.getRootNode();
            if (root.getNodeName().equals("controlQuery")) {
                Attr attrId = root.getAttributeNode("id");

                String str = "";
                NodeList nodeList = root.getChildNodes();
                if (nodeList.getLength() == 0) {
                    // global description
                    str = generateShortGlobalDescription();
                } else {
                    for(int i=0; i<nodeList.getLength(); i++){
                        Node cur = nodeList.item(i);
                        if(cur.getNodeType() == Node.ELEMENT_NODE){
                            String curName = cur.getNodeName();
                            if (curName.equals(InOutputAttribut.Input.getXMLTag())) {
                                str += processInOutputQuery((Element)cur,
                                        InOutputAttribut.Input);
                            } else if (curName.equals(InOutputAttribut.Output
                                    .getXMLTag())) {
                                str += processInOutputQuery((Element)cur,
                                        InOutputAttribut.Output);
                            } else if (curName.equals(InOutputAttribut.InOutput
                                    .getXMLTag())) {
                                str += processInOutputQuery((Element)cur,
                                        InOutputAttribut.InOutput);
                            } else if (curName.equals("variable")) {
                                str += processVariableQuery((Element)cur, msg.getPid());
                            } else if (curName.equals("connect")) {
                                str += processConnectQuery((Element)cur);
                            } else if (curName.equals("subscribe")) {
                                str += processSubscribeQuery((Element)cur, msg.getPid(),
                                        true);
                            } else if (curName.equals("unsubscribe")) {
                                str += processSubscribeQuery((Element)cur, msg.getPid(),
                                        false);
                            }else if(curName.equals("lock")){
                                str += processLockQuery((Element)cur, msg.getPid());
                            }else if(curName.equals("unlock")){
                                str += processUnlockQuery((Element)cur, msg.getPid());
                            } else
                                System.err.println("unknow tag : " + curName);
                        }
                    }
                }

                str = "<controlAnswer id=\"" + attrId.getValue() + "\">" + str
                        + "</controlAnswer>";

                if (!tcpServer.sendToOneClient(str.getBytes(), msg.getPid())) {
                    System.err.println("ControlServer : Send failed : peer not found : "
                            + msg.getPid());
                }//else System.out.println("send to " + msg.getPid());
            } else
                System.out.println("Unknown Tag : "
                        + msg.getRootNode().getNodeName());
        }
    }

    /**
     * Process the subcribe/unsubscribe queries
     * 
     * @param elt
     *            piece of XML tree for the subcribe/unsubscribe query
     * @param pid
     *            the service id that ask for subscribe
     * @param subscribe
     *            true if subscribe query, false if unsubscribe query
     * @return answer to the query : no answer : ""
     */
    protected String processSubscribeQuery(Element elt, int pid,
            boolean subscribe) {
        Attr attrName = elt.getAttributeNode("name");

        VariableAttribut va = findVariable(attrName.getValue());
        if (va != null) {
            if (subscribe)
                va.addPeer(pid);
            else
                va.removePeer(pid);
        }
        return "";
    }
    /** Process the queries about in/output
     * @param elt part of the XML tree contained between tag about in/output : "input",
     * "output", or "inOutput".
     * @param kind input, output, or inOutput
     * @return the answer to the query
     */
    protected String processInOutputQuery(Element elt, InOutputKind kind) {
        Attr attrName = elt.getAttributeNode("name");
        InOutputAttribut ioa = findInOutput(attrName.getValue(), kind);
        if(ioa != null){
            return ioa.generateLongDescription();
        }else{
            return "";
        }        
    }
    
    /** Process query about variable
     * @param elt part of the xml tree contained between tag "variable" 
     * @param pid id of the peer (origin of the query). Used in case of 
     * modification to enable or not the modification according to the lock attribute
     * @return the answer to the query, "" if the variable concerner is not found */
    protected String processVariableQuery(Element elt, int pid) {
        Attr attrName = elt.getAttributeNode("name");
        if (attrName == null)
            System.err.println("understood query (name requested)");
        else {
            String name = attrName.getValue();
            VariableAttribut va = findVariable(name);
            if (va != null) {
                if (elt.getChildNodes().getLength() == 0)
                    return va.generateLongDescription();
                else {
                    Element eltVal = XmlUtils.firstChild(elt, "value");
                    if (eltVal != null) {
                        if (lockOk(pid) && va.canBeModified(statusIntVar.getIntValue())) {
                            modifVariable(eltVal.getFirstChild().getNodeValue().getBytes(),
                                    statusIntVar.getIntValue(), va);
                        }
                        return va.generateValueMessage();
                    }
                }
            }
        }
        return "";
    }    
    /** Process a query for connection 
     * @param elt the part of xml tree between tag "connect" */
    protected String processConnectQuery(Element elt) {
        Attr attrName = elt.getAttributeNode("name");

        if (attrName == null)
            System.err.println("understood query (name requested)\n");
        else {
            String name = attrName.getValue();
            InOutputAttribut ioa = findInOutput(name, null);
            if (ioa != null) {
                boolean foundHost = false;
                boolean foundPort = false;
                boolean tcp = true;
                int port = 0;
                String host = null;

                NodeList nodeList = elt.getChildNodes();
                for(int i=0; i<nodeList.getLength(); i++){
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
                        System.out.println("in connect query : unused tag : "
                                + curName);
                    }
                }

                if (foundPort && foundHost) {
                    connect(host, port, tcp, ioa);
                    return ioa.generateConnectAnswer();
                }
            }
        }
        return "";
    }
    /** Process a query to lock the control server
     * @param elt part of XML containing the query 
     * @param pid id of peer that asks to lock the control server
     * @return the result of the query : &lt;lock result="res" peer="id" /&gt;
     * where res has the value ok or failed and id the id of the peer thats currently lock the server.
     */
    protected String processLockQuery(Element elt, int pid){
        String res = null;
        if(lockOk(pid)){
            lockIntVar.setIntValue(pid);
            res = "ok";
        }else{
            res = "failed";
        }
        return "<lock result=\""+res+"\" peer=\""+
            fr.prima.bipcom.MsgSocket.intTo8HexString(lockIntVar.getIntValue()) +
            "\"/>";
    }
    /** Process a query to unlock the control server
     * @param elt part of XML containing the query 
     * @param pid id of peer that asks to unlock the control server
     * @return the result of the query : &lt;unlock result="res" peer="id" /&gt;
     * where res has the value ok or failed and id the id of the peer thats currently lock the server.
     * If the query succeeds, the id value is 0.
     */
    protected String processUnlockQuery(Element elt, int pid){
        String res = null;
        if(lockOk(pid)){
            res = "ok";
            lockIntVar.setIntValue(0);
        }else{
            res = "failed";
        }
        return "<unlock result=\""+res+"\" peer=\""+
            fr.prima.bipcom.MsgSocket.intTo8HexString(lockIntVar.getIntValue()) +
            "\"/>";
    }
    
    /** Find a variable of the service 
     * @param name name of the variable to find 
     * @return the VariableAttribut object associated to the variable name
     * or null if not found */
    public VariableAttribut findVariable(String name) {
        java.util.Iterator<VariableAttribut> it = variableSet.iterator();
        while (it.hasNext()) {
            VariableAttribut va = it.next();
            if (va.getName().equals(name))
                return va;
        }
        return null;
    }
    /** Find an in/output of the service 
     * @param name name of the in/output to find
     * @param k kind of the in/output to find : input, output, or inOutput
     * @return the InOutputAttribut object associated to the name
     * or null if not found */
    public InOutputAttribut findInOutput(String name, InOutputKind k) {
        java.util.Iterator<InOutputAttribut> it = inoutputSet.iterator();
        if (k == null) {
            while (it.hasNext()) {
                InOutputAttribut ioa = it.next();
                if (ioa.getName().equals(name))
                    return ioa;
            }
        } else {
            while (it.hasNext()) {
                InOutputAttribut ioa = it.next();
                if (ioa.getKind() == k && ioa.getName().equals(name))
                    return ioa;
            }
        }
        return null;
    }
    
    /** Test when the server is locked if the peer that locked the service is
     * still existing */
    protected void refreshLock(){
        int peer = lockIntVar.getIntValue();
        if(peer != 0){
            if(!tcpServer.isStillConnected(peer)){
                lockIntVar.setIntValue(0);
            }
        }
    }
    /** Test if the lock enables to do modification for a particular peer
     * @param peer the id of the peer to test */
    protected boolean lockOk(int peer){
        refreshLock();
        return (lockIntVar.getIntValue() == 0) || (lockIntVar.getIntValue() == peer);
    }

    /**
     * Service example.
     * A service named essai, with 2 variables (var_1, var_2) and an output (my_output).
     * var_1 can be modified by the user.
     * var_2 is regularly modified by the service.
     */
    public static void main(String[] args) {
        int controlPort = 0;
        String serviceName = "essai";
        System.out.println("ControlServer creation");
        ControlServer ctrl = new ControlServer(serviceName) {
            protected void modifVariable(byte[] buffer, int status,
                    VariableAttribut va) {
                String valueStr = new String(buffer);
                System.out.println("modif variable " + va.getName() + " <- "
                        + valueStr);
                va.setValueStr(valueStr);
            }
        };

        System.out.println("add variable");
        VariableAttribut va = null;
        va = ctrl.addVariable("var_1");
        va.setAccess(VariableAttribut.READ_WRITE);
        va.setType("integer");
        va.setDefaultValue("0");
        va.setDescription("a variable to modify for test");
        va.setFormatDescription("decimal representation");
        va.setValueStr("0");

        va = ctrl.addVariable("var_2");
        va.setDescription("automatically incremented");
        IntVariableAttribut var2 = new IntVariableAttribut(va, 0);
        
        System.out.println("add an output");
        InOutputAttribut ioa = null;
        fr.prima.bipcom.TcpServer tcpServer = null;
        try {
            tcpServer = new fr.prima.bipcom.TcpServer(ctrl.getServiceId(), 0);
            tcpServer.start();
            tcpServer.addBipMessageListener(new fr.prima.bipcom.interf.BipMessageListener(){
                public void receivedBipMessage(fr.prima.bipcom.interf.Message m){
                    System.out.println("received bip message");
                }
            });
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        ioa = ctrl
                .addInOutput("my output", tcpServer, InOutputAttribut.Output);
        ioa.setDescription("output for test");

        System.out.println("Register, creation control port");
        ctrl.startServer(controlPort);
        System.out.println("Thread process msg");
        ctrl.startThreadProcessMessage();

        ctrl.setStatus(ControlServer.STATUS_RUNNING);

        System.out.println("Control Server Launched : "
                + ctrl.getTcpServer().getHost() + ":"
                + ctrl.getTcpServer().getTcpPort());
        System.out.println("Service registered as " + ctrl.getRegisteredServiceName());
        String str = "hello";
        while (true) {
            try {
                tcpServer.sendToClients(str.getBytes());
                var2.incr();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }
}
