package fr.prima.omiscid.control ;

import java.util.Calendar;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.prima.omiscid.com.MsgSocket;
import fr.prima.omiscid.com.TcpClient;
import fr.prima.omiscid.com.interf.Message;
import fr.prima.omiscid.com.interf.OmiscidMessageListener;

/**
 * Communication with a control server of a OMiSCID service. Query data, store answer. Has a local copy of the data. <br> Use: <ul><li> Creates a ControlClient instance </li><li> Connect the control client to a control server </li><li> query a global description of the service : then you have the  names for all variables and in/outputs. </li><li> you can do specific query on variable, or in/output  </li></ul>
 * @author  Sebastien Pesnel  Refactoring by Patrick Reignier
 */
public class ControlClient implements OmiscidMessageListener {
    /** The max time to wait for the answer to a query */
    private final int MaxTimeToWait = 500; // milliseconds

    /** The connection to the control port */
    private TcpClient tcpClient = null;

    /** Id used in OMiSCID exhange */
    private int serviceId = 0;

    /** Query Id : the answer to a query have the same id that the query */
    private int msgId = 0;

    /** Object used as condition to signal when an answer is available */
    private Object answerEvent = new Object();

    /** An available answer */
    private XmlMessage msgAnswer = null;

    /**
     * Set of listener interested in the control event (Set of object
     * implementing the ControlEventListener interface)
     */
    private Set<ControlEventListener> ctrlEventListenerSet = new java.util.HashSet<ControlEventListener>();

    /**
     * Set of variable name (Set of String object)
     */
    public final Set<String> variableNameSet = new java.util.TreeSet<String>();

    /**
     * Set of input name (Set of String object)
     */
    public final Set<String> inputNameSet = new java.util.TreeSet<String>();

    /**
     * Set of output name (Set of String object)
     */
    public final Set<String> outputNameSet = new java.util.TreeSet<String>();

    /**
     * Set of in/output name (Set of string object)
     */
    public final Set<String> inOutputNameSet = new java.util.TreeSet<String>();

    /**
     * Set of Variables (Set of VariableAttribut objects)
     */
    public final Set<VariableAttribut> variableAttrSet = new java.util.HashSet<VariableAttribut>();

    /**
     * Set of Inputs (Set of InOutputAttribut objects)
     */
    public final Set<InOutputAttribut> inputAttrSet = new java.util.HashSet<InOutputAttribut>();

    /**
     * Set of outputs (Set of InOutputAttribut objects)
     */
    public final Set<InOutputAttribut> outputAttrSet = new java.util.HashSet<InOutputAttribut>();

    /**
     * Set of in/outputs (Set of InOutputAttribut objects)
     */
    public final Set<InOutputAttribut> inOutputAttrSet = new java.util.HashSet<InOutputAttribut>();

    /**
     * Create a new instance of ControlClient class
     * 
     * @param serviceId
     *            the id to use to identify peer in OMiSCID exchange
     */
    public ControlClient(int serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * Create the connection to a control server. Instanciate a TCP client. Add
     * this object as listener on message received by the TCP client
     * 
     * @param host
     *            the host name where find the control server
     * @param port
     *            the control port
     * @return if the connection is correctly established
     */
    public boolean connectToControlServer(String host, int port) {
        try {
            //System.out.println("control client : " +host +" "+port);
            
            tcpClient = new TcpClient(serviceId);
            tcpClient.connectTo(host, port);
            tcpClient.addOmiscidMessageListener(this);

            return true;
        } catch (java.io.IOException e) {
            tcpClient = null;
            e.printStackTrace();
            return false;
        }
    }

    /** @return if the connection worked */
    public boolean isConnected() {
        return (tcpClient != null) && tcpClient.isConnected();
    }

    /** Close the connection */
    public void close() {
        if (tcpClient != null)
            tcpClient.closeConnection();
    }
    
    /** Get Peer id From the TCP connection */
    public int getPeerId(){
        if(tcpClient != null)
            return tcpClient.getPeerId();
        else return 0;
    }

    /**
     * Implement the OmiscidMessageListerner interface Test if the message is an
     * answer to a query or a control event. In case of answer, the reception of
     * this is signaled, for the control event message, they are given to the
     * ControlEventListener
     * 
     * @param msg a new OMiSCID message received
     */
    public void receivedOmiscidMessage(Message msg) {
//         System.out.println("ControlClient:MsgReceived: " +
//         msg.getBufferAsString());
        XmlMessage xmlMsg = XmlMessage.changeMessageToXmlTree(msg);
        if (xmlMsg != null && xmlMsg.getRootNode() != null) {
            org.w3c.dom.Element root = xmlMsg.getRootNode();
            if (root.getNodeName().equals("controlAnswer")) {
                synchronized (answerEvent) {
                    msgAnswer = xmlMsg;
                    answerEvent.notify();
                }
            } else if (root.getNodeName().equals("controlEvent")) {
                synchronized (ctrlEventListenerSet) {
                    java.util.Iterator<ControlEventListener> it = ctrlEventListenerSet.iterator();
                    while (it.hasNext()) {
                        it.next().receivedControlEvent(xmlMsg);
                    }
                }
            } else{
                System.err.println("Unknown message kind : " + root.getNodeName());
            }
        }
    }

    /**
     * Add a listener on control event
     * 
     * @param l
     *            listener interested in control event
     */
    public void addControlEventListener(ControlEventListener l) {
        synchronized (ctrlEventListenerSet) {
            ctrlEventListenerSet.add(l);
        }
    }

    /**
     * Remove a listener on control event
     * 
     * @param l
     *            listener no more interested in control event
     */
    public void removeControlEventListener(ControlEventListener l) {
        synchronized (ctrlEventListenerSet) {
            ctrlEventListenerSet.remove(l);
        }
    }

    /**
     * Find an attribute with a particular name
     * 
     * @param name
     *            name of the attribute
     * @param attributSet
     *            a set of attribute where looked for the name
     * @return the Attribut object if found, null otherwise
     */
    private Attribut findAttribute(String name, Set<? extends Attribut> attributSet) {
        java.util.Iterator<? extends Attribut> it = attributSet.iterator();
        while (it.hasNext()) {
            Attribut attr = (Attribut) it.next();
            if (name.equals(attr.getName()))
                return attr;
        }
        return null;
    }

    /**
     * Find a variable with a particular name
     * 
     * @param name
     *            the name to look for
     * @return the VariableAttribut object if found, null otherwise
     */
    public VariableAttribut findVariable(String name) {
        Attribut attr = findAttribute(name, variableAttrSet);
        if (attr == null)
            return null;
        else
            return (VariableAttribut) attr;
    }

    /**
     * Find an input with a particular name
     * 
     * @param name
     *            the name to look for
     * @return the InOutputAttribut object if found, null otherwise
     */
    public InOutputAttribut findInput(String name) {
        Attribut attr = findAttribute(name, inputAttrSet);
        if (attr == null)
            return null;
        else
            return (InOutputAttribut) attr;
    }

    /**
     * Find an output with a particular name
     * 
     * @param name
     *            the name to look for
     * @return the InOutputAttribut object if found, null otherwise
     */
    public InOutputAttribut findOutput(String name) {
        Attribut attr = findAttribute(name, outputAttrSet);
        if (attr == null)
            return null;
        else
            return (InOutputAttribut) attr;
    }

    /**
     * Find an input/output with a particular name
     * 
     * @param name
     *            the name to look for
     * @return the InOutputAttribut object if found, null otherwise
     */
    public InOutputAttribut findInOutput(String name) {
        Attribut attr = findAttribute(name, inOutputAttrSet);
        if (attr == null)
            return null;
        else
            return (InOutputAttribut) attr;
    }

    /**
     * Query a global description of a service. This description return the name
     * of all the variables, inputs and outputs
     * 
     * @return true is the query has received an answer
     */
    public boolean queryGlobalDescription() {
        String request = "";
        XmlMessage msg = queryToServer(request, true);
        if (msg != null) {
            processGlobalDescription(msg);
            return true;
        }
        return false;
    }
    public void queryCompleteDescription() {
        java.util.Iterator<String> it;
        it = inputNameSet.iterator();
        while (it.hasNext()) {
            queryInputDescription((String) it.next());
        }
        it = outputNameSet.iterator();
        while (it.hasNext()) {
            queryOutputDescription((String) it.next());
        }
        it = inOutputNameSet.iterator();
        while (it.hasNext()) {
            queryInOutputDescription((String) it.next());
        }
        
        it = variableNameSet.iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            VariableAttribut vattr = queryVariableDescription(name);
            if (vattr == null)
                System.out.println("Error queryVariableDescription : "
                        + name);
        }  
    }

    /**
     * Query a complete description for a variable
     * 
     * @param name
     *            the variable name
     * @return a VariableAttribut object that contains the description, null if
     *         the request failed
     */
    public VariableAttribut queryVariableDescription(String name) {
        String request = "<variable name=\"" + name + "\"/>";
        XmlMessage msg = queryToServer(request, true);
        System.err.println("queryVariableDescription : sent " + request + " and had " + msg) ; //- trace
        if (msg != null) {
            VariableAttribut vattr = findVariable(name);
            Element elt = XmlUtils.firstChild(msg.getRootNode(), "variable");
            
            VariableAttribut attr = null;
            if(elt != null)
                attr = processVariableDescription(elt, vattr);
            if (attr == null) {
                // System.out.println("attr = null");
                if (vattr != null)
                    variableAttrSet.remove(vattr);
                if (variableNameSet.contains(name))
                    variableNameSet.remove(name);
            } else {
                // System.out.println("attr != null");
                if (vattr == null)
                    variableAttrSet.add(attr);
                if (!variableNameSet.contains(name))
                    variableNameSet.add(name);
            }
            return attr;
        }
        return null;
    }

    /**
     * Ask for the modification of the value of a variable
     * 
     * @param name
     *            the varaible name
     * @param value
     *            the new value for the variable
     * @return a VariableAttribut object with the value of the variable after
     *         the request.
     */
    public VariableAttribut queryVariableModification(String name, String value) {
        VariableAttribut vattr = findVariable(name);
        if (vattr == null) {
            System.out
                    .println("Unknown Variable : Not Available Description : "
                            + name);
            return null;
        }
        String request = "<variable name=\"" + name + "\">";
        request += "<value>";
        request += XmlUtils.generateCDataSection(value);
        request += "</value></variable>";
        XmlMessage msg = queryToServer(request, true);
        if (msg != null) {
            VariableAttribut attr = processVariableDescription(XmlUtils.firstChild(msg.getRootNode(), "variable"), vattr);
            return attr;
        }
        return null;
    }

    /**
     * Query a complete description for an input
     * 
     * @param name
     *            the input name
     * @return a InOutputAttribut object that contains the description, null if
     *         the request failed
     */
    public InOutputAttribut queryInputDescription(String name) {
        String request = "<" + InOutputAttribut.Input.getXMLTag() + " name=\""
                + name + "\"/>";
        XmlMessage msg = queryToServer(request, true);
        if (msg != null) {
            InOutputAttribut ioattr = findInput(name);
            InOutputAttribut attr = processInOutputDescription(
                    XmlUtils.firstChild(msg.getRootNode(), InOutputAttribut.Input.getXMLTag()),
                    ioattr);
            if (attr == null) {
                if (ioattr != null)
                    inputAttrSet.remove(ioattr);
                if (inputNameSet.contains(name))
                    inputNameSet.remove(name);
            } else {
                if (ioattr == null)
                    inputAttrSet.add(attr);
                if (!inputNameSet.contains(name))
                    inputNameSet.add(name);
            }
            return attr;
        }
        return null;
    }

    /**
     * Query a complete description for an output
     * 
     * @param name
     *            the output name
     * @return a InOutputAttribut object that contains the description, null if
     *         the request failed
     */
    public InOutputAttribut queryOutputDescription(String name) {
        String request = "<" + InOutputAttribut.Output.getXMLTag() + " name=\""
                + name + "\"/>";
        XmlMessage msg = queryToServer(request, true);
        if (msg != null) {
            InOutputAttribut ioattr = findOutput(name);
            InOutputAttribut attr = processInOutputDescription(
                    XmlUtils.firstChild(msg.getRootNode(),
                    InOutputAttribut.Output.getXMLTag()), ioattr);
            if (attr == null) {
                if (ioattr != null)
                    outputAttrSet.remove(ioattr);
                if (outputNameSet.contains(name))
                    outputNameSet.remove(name);
            } else {
                if (ioattr == null)
                    outputAttrSet.add(attr);
                if (!outputNameSet.contains(name))
                    outputNameSet.add(name);
            }
            return attr;
        }
        return null;
    }

    /**
     * Query a complete description for an input/output
     * 
     * @param name
     *            the input/output name
     * @return a InOutputAttribut object that contains the description, null if
     *         the request failed
     */
    public InOutputAttribut queryInOutputDescription(String name) {
        String request = "<" + InOutputAttribut.InOutput.getXMLTag()
                + " name=\"" + name + "\"/>";
        XmlMessage msg = queryToServer(request, true);
        if (msg != null) {
            InOutputAttribut ioattr = findInOutput(name);
            InOutputAttribut attr = processInOutputDescription(
                    XmlUtils.firstChild(msg.getRootNode(),
                            InOutputAttribut.InOutput.getXMLTag()), ioattr);
            if (attr == null) {
                if (ioattr != null)
                    inOutputAttrSet.remove(ioattr);
                if (inOutputNameSet.contains(name))
                    inOutputNameSet.remove(name);
            } else {
                if (ioattr == null)
                    inOutputAttrSet.add(attr);
                if (inOutputNameSet.contains(name))
                    inOutputNameSet.add(name);
            }
            return attr;
        }
        return null;
    }

    /**
     * Subscribe to the modification of a particular variable. The modification
     * wiil be received in ControlEvent. A complete description must have been
     * queried before calling this method
     * 
     * @param varName
     *            the name of the variable
     * @return false if the variable is not known
     */
    public boolean subscribe(String varName) {
        VariableAttribut va = findVariable(varName);
        if (va != null) {
            String request = "<subscribe name=\"" + va.getName() + "\"/>";
            queryToServer(request, false);
            return true;
        } else {
            System.err.println("variable unknown by client\n");
            return false;
        }
    }

    /**
     * Unsubscribe to the modification of a particular variable. The
     * modification will not be received in ControlEvent any more. A complete
     * description must have been queried before calling this method.
     * 
     * @param varName
     *            the name of the variable
     * @return false if the variable is not known
     */
    public boolean unsubscribe(String varName) {
        VariableAttribut va = findVariable(varName);
        if (va != null) {
            String request = "<unsubscribe name=\"" + va.getName() + "\"/>";
            queryToServer(request, false);
            return true;
        } else {
            System.out.println("variable unknown by client\n");
            return false;
        }
    }

    /** Ask to lock the control server 
     * @return if the control server is locked for this service */
    public boolean lock() {
        String request = "<lock/>";
        XmlMessage msg = queryToServer(request, true);
        if (msg != null) {
            Element elt = XmlUtils.firstChild(msg.getRootNode(), "lock");
            String res = elt.getAttribute("result");
            int peer = MsgSocket.hexStringToInt(elt.getAttribute("peer"));
            
            VariableAttribut vattr = findVariable("lock");
            if(vattr != null){
                vattr.setValueStr(Integer.toString(peer));
            }
            if(res.equals("ok")){
                if(peer != serviceId){
                    System.err.println("lock ok, but id different : " + peer +" != "+ serviceId);
                }
                return true;
            }
        }
        return false;
    }
    
    /** Ask to unlock the control server 
     * @return if the control server is unlocked */
    public boolean unlock() {
        String request = "<unlock/>";
        XmlMessage msg = queryToServer(request, true);
        if (msg != null) {
            Element elt = XmlUtils.firstChild(msg.getRootNode(), "unlock");
            String res = elt.getAttribute("result");
            int peer = MsgSocket.hexStringToInt(elt.getAttribute("peer"));
            
            VariableAttribut vattr = findVariable("lock");
            if(vattr != null){
                vattr.setValueStr(Integer.toString(peer));
            }
            if(res.equals("ok")){
                if(peer != 0){
                    System.err.println("unlock ok, but id no null : " + peer );
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Process the query to the control server
     * 
     * @param request
     *            request to send to the server
     * @param waitAnswer
     *            indicate if the methods must wait for an answer from the
     *            control server
     * @return the control answer or null if we do not wait for the answer or if
     *         the query failed
     */
    private XmlMessage queryToServer(String request, boolean waitAnswer) {

        synchronized (answerEvent) {
            if (isConnected()) {
                int theMsgId = msgId++;
                String str = fr.prima.omiscid.com.MsgSocket.intTo8HexString(theMsgId);
                str = "<controlQuery id=\"" + str + "\">" + request
                        + "</controlQuery>";

                // System.out.println("queryToServer ["+str+"]");
                tcpClient.send(str.getBytes());
                if (waitAnswer) {
                    try {
                        System.out.println("queryToServer : before wait " + Calendar.getInstance().getTime()); //- trace
                        answerEvent.wait(MaxTimeToWait);
                        System.out.println("queryToServer : after wait " +  Calendar.getInstance().getTime()); //- trace
                        
                        if (msgAnswer != null){
                            XmlMessage m = msgAnswer;
                            msgAnswer = null;
                            if (checkMessage(m, theMsgId)) return m;
                        }else{
                            System.out.println("answer null to request " + request + " from "+Integer.toHexString(getPeerId()));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

    /**
     * Check if the answer id has the waited value (the same value as the query
     * id)
     * 
     * @param msg
     *            answer from the control server
     * @param msgId
     *            id of the query
     * @return if the answer has the good id, that is to say the value 'msgId'
     */
    private boolean checkMessage(XmlMessage msg, int msgId) {
        //System.out.println("in check message");
        if (msg != null && msg.getRootNode() != null) {
            Attr attr = msg.getRootNode().getAttributeNode("id");
            if (attr != null
                    && MsgSocket.hexStringToInt(attr.getValue()) == msgId)
                return true;
        }
        return false;
    }

    /**
     * Process the answer to a query for global description
     * 
     * @param msg
     *            the answer to a query for global description
     */
    private void processGlobalDescription(XmlMessage msg) {
        variableNameSet.clear();
        inOutputNameSet.clear();
        inputNameSet.clear();
        outputNameSet.clear();
        variableAttrSet.clear();
        inputAttrSet.clear();
        outputAttrSet.clear();
        inOutputAttrSet.clear();

        NodeList nodeList = msg.getRootNode().getChildNodes();
        for(int i = 0; i<nodeList.getLength(); i++){            
            Node node = nodeList.item(i);
            String nodeName = node.getNodeName();
            if (nodeName.equals("variable")) {
                variableNameSet.add(((Element)node).getAttribute("name"));
            } else if (nodeName.equals(InOutputAttribut.Input.getXMLTag())) {
                inputNameSet.add(((Element)node).getAttribute("name"));
            } else if (nodeName
                    .equals(InOutputAttribut.Output.getXMLTag())) {
                outputNameSet.add(((Element)node).getAttribute("name"));
            } else if (nodeName.equals(
                    InOutputAttribut.InOutput.getXMLTag())) {
                inOutputNameSet.add(((Element)node).getAttribute("name"));
            } else
                System.out.println("Unknown kind " + nodeName);
        }
    }

    /**
     * Process the answer to a query for complete variable description
     * 
     * @param msg
     *            the answer to a query for complete variable description
     * @param vattr
     *            a VariableAttribut object that already exists, then this
     *            description will be update. can be null.
     * @return a VariableAttribut object with the description, null if the query
     *         failed. If 'vattr' is non null, it is this object that is
     *         returned
     */
    private VariableAttribut processVariableDescription(
            Element elt,
            VariableAttribut vattr) {
        Attr nameAttr = elt.getAttributeNode("name");
        if (nameAttr != null) {
            VariableAttribut attr = vattr;
            if (vattr == null)
                attr = new VariableAttribut(nameAttr.getValue());

            attr.extractInfoFromXML(elt);
            
            return attr;
        }
        return null;
    }

    /**
     * Process the answer to a query for complete in/output description
     * 
     * @param msg
     *            the answer to a query for complete in/output description
     * @param ioattr
     *            a VariableAttribut object that already exists, then this
     *            description will be update. can be null.
     * @return a VariableAttribut object with the description, null if the query
     *         failed. If 'ioattr' is non null, it is this object that is
     *         returned
     */
    private InOutputAttribut processInOutputDescription(
            Element elt,
            InOutputAttribut ioattr) {

        Attr nameAttr = elt.getAttributeNode("name");
        if (nameAttr != null) {
            InOutputAttribut attr = ioattr;
            if (ioattr == null)
                attr = new InOutputAttribut(nameAttr.getValue());

            attr.setKind(InOutputAttribut.IOKindFromName(elt.getNodeName()));            
            attr.extractInfoFromXML(elt);
            
            return attr;
        }
        return null;
    }
    
    public Element createXmlElement(Document doc){
        Element eltService = doc.createElement("service");
        
        Element elt = null;
        java.util.Iterator<VariableAttribut> itVa = null;
        itVa = variableAttrSet.iterator();
        while(itVa.hasNext()){
            elt = itVa.next().createXmlElement(doc);
            eltService.appendChild(elt);
        }
        java.util.Iterator<InOutputAttribut> it = null;
        it = inputAttrSet.iterator();
        while(it.hasNext()){
            elt = ((InOutputAttribut)it.next()).createXmlElement(doc);
            eltService.appendChild(elt);
        }
        it = inOutputAttrSet.iterator();
        while(it.hasNext()){
            elt = ((InOutputAttribut)it.next()).createXmlElement(doc);
            eltService.appendChild(elt);
        }
        it = outputAttrSet.iterator();
        while(it.hasNext()){
            elt = ((InOutputAttribut)it.next()).createXmlElement(doc);
            eltService.appendChild(elt);
        }
        
        return eltService;
    }

//    public static void main(String arg[]) {
//
//        WaitForOmiscidServices wfbs = new WaitForOmiscidServices();
//        int index = wfbs.needService("essai");
//        wfbs.waitResolve();
//        OmiscidService service = wfbs.getService(index);
//
//        int serviceId = OmiscidService.generateServiceId();
//        ControlClient client = new ControlClient(serviceId);
//        if (!client.connectToControlServer(service.getHostName(), service.getPort())) {
//            System.err.println("error connection");
//            System.exit(1);
//        }
//        System.out.println("query global description");
//        if (!client.queryGlobalDescription()) {
//            System.err.println("error global description");
//            System.exit(1);
//        } else {
//            java.util.Iterator<String> it = null;
//
//            System.out.println("Variables : ");
//            it = client.variableNameSet.iterator();
//            while (it.hasNext()) {
//                System.out.println(" - " + (String) it.next());
//            }
//            System.out.println("Input : ");
//            it = client.inputNameSet.iterator();
//            while (it.hasNext()) {
//                System.out.println(" - " + (String) it.next());
//            }
//            System.out.println("Output : ");
//            it = client.outputNameSet.iterator();
//            while (it.hasNext()) {
//                System.out.println(" - " + (String) it.next());
//            }
//            System.out.println("In/Output : ");
//            it = client.inOutputNameSet.iterator();
//            while (it.hasNext()) {
//                System.out.println(" - " + (String) it.next());
//            }
//        }
//    }
}
