package fr.prima.omiscid.control;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.prima.omiscid.com.BipUtils;
import fr.prima.omiscid.com.TcpClient;
import fr.prima.omiscid.com.XmlMessage;
import fr.prima.omiscid.com.interf.Message;
import fr.prima.omiscid.com.interf.BipMessageListener;

/**
 * Handles the communication with the control server of a OMiSCID service.
 * Queries data, stores answers. Keeps a local copy of the data. Example use:
 * <ul>
 * <li> Create a ControlClient instance </li>
 * <li> Connect the control client to a control server </li>
 * <li> Query a global description of the service, then you have the names for
 * all variables and in/outputs.</li>
 * <li> Do specific query on variables or in/output .</li>
 * </ul>
 *
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
// \REVIEWTASK shouldn't this be a monitor?
public class ControlClient implements BipMessageListener {
    /** The max time to wait for the answer to a query */
    private final int MaxTimeToWait = 500; // milliseconds

    // \REVIEWTASK should be configurable in a specific way (env variable?)

    /** The connection to the control port */
    private TcpClient tcpClient = null;

    /** Peer id used in BIP exhange */
    private int peerId = 0;

    /** Query Id: the answer to a query have the same id that the query */
    private int messageId = 0;

    /** Object used as condition to signal when an answer is available */
    private Object answerEvent = new Object();

    /** An available answer */
    private XmlMessage messageAnswer = null;

    /**
     * Set of listener interested in the control event (Set of object
     * implementing the ControlEventListener interface)
     */
    private Set<ControlEventListener> controlEventListenersSet = new HashSet<ControlEventListener>();

    /**
     * Set of variable name
     */
    private final Set<String> variableNamesSet = new TreeSet<String>();

    /**
     * Set of input name
     */
    private final Set<String> inputNamesSet = new TreeSet<String>();

    /**
     * Set of output name
     */
    private final Set<String> outputNamesSet = new TreeSet<String>();

    /**
     * Set of in/output name
     */
    private final Set<String> inOutputNamesSet = new TreeSet<String>();

    /**
     * Set of Variables
     */
    private final Set<VariableAttribute> variableAttributesSet = new HashSet<VariableAttribute>();

    /**
     * Set of Inputs (Set of InOutputAttribute objects)
     */
    private final Set<InOutputAttribute> inputAttributesSet = new HashSet<InOutputAttribute>();

    /**
     * Set of outputs (Set of InOutputAttribute objects)
     */
    private final Set<InOutputAttribute> outputAttributesSet = new HashSet<InOutputAttribute>();

    /**
     * Set of in/outputs (Set of InOutputAttribute objects)
     */
    private final Set<InOutputAttribute> inOutputAttributesSet = new HashSet<InOutputAttribute>();

    /**
     * Creates a new instance of ControlClient class.
     *
     * @param peerId
     *            the peer id to use to identify the local peer in BIP exchanges
     */
    public ControlClient(int peerId) {
        this.peerId = peerId;
    }

    /**
     * Creates the connection to a control server. Instanciates a TCP client.
     * Adds this object as listener on message received by the TCP client.
     *
     * @param host
     *            the host name where find the control server
     * @param port
     *            the control port
     * @return if the connection is correctly established
     */
    public boolean connectToControlServer(String host, int port) {
        try {
            tcpClient = new TcpClient(peerId);
            tcpClient.connectTo(host, port);
            tcpClient.addBipMessageListener(this);
            return true;
        } catch (java.io.IOException e) {
            tcpClient = null;
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tests whether this control client connection is running.
     *
     * @return whether the connection is up
     */
    public boolean isConnected() {
        return (tcpClient != null) && tcpClient.isConnected();
    }

    /**
     * Closes the connection.
     */
    public void close() {
        if (tcpClient != null) {
            tcpClient.closeConnection();
        }
    }

    /**
     * Gets the remote peer id from the TCP connection.
     */
    public int getPeerId() {
        if (tcpClient != null) {
            return tcpClient.getRemotePeerId();
        } else {
            return 0;
        }
    }

    /**
     * Implements the BipMessageListerner interface. Tests whether the
     * message is an answer to a query or a control event. In case of answer,
     * the reception of this is signaled by a control event message.
     * {@link ControlEventListener} describes the interface to implement to
     * receive such control event messages.
     *
     * @param message
     *            a new BIP message received
     */
    public void receivedBipMessage(Message message) {
        XmlMessage xmlMessage = XmlMessage.newUnchecked(message);
        if (xmlMessage != null && xmlMessage.getRootElement() != null) {
            Element root = xmlMessage.getRootElement();
            if (root.getNodeName().equals("controlAnswer")) {
                synchronized (answerEvent) {
                    messageAnswer = xmlMessage;
                    answerEvent.notify();
                }
            } else if (root.getNodeName().equals("controlEvent")) {
                synchronized (controlEventListenersSet) {
                    for (ControlEventListener listener : controlEventListenersSet) {
                        listener.receivedControlEvent(xmlMessage);
                    }
                }
            } else {
                System.err.println("Unknown message kind : " + root.getNodeName());
            }
        }
    }

    /**
     * Adds a listener for control event.
     *
     * @param l
     *            listener interested in control event
     */
    public void addControlEventListener(ControlEventListener l) {
        synchronized (controlEventListenersSet) {
            controlEventListenersSet.add(l);
        }
    }

    /**
     * Removes a listener for control event.
     *
     * @param l
     *            listener no more interested in control event
     * @return whether the listener was removed
     */
    public boolean removeControlEventListener(ControlEventListener l) {
        synchronized (controlEventListenersSet) {
            return controlEventListenersSet.remove(l);
        }
    }

    /**
     * Finds an attribute with a particular name.
     *
     * @param name
     *            the name of the attribute
     * @param attributesSet
     *            a set where to look for the name
     * @return the Attribute object if found, null otherwise
     */
    private Attribute findAttribute(String name, Set<? extends Attribute> attributesSet) {
        for (Attribute attribute : attributesSet) {
            if (name.equals(attribute.getName())) {
                return attribute;
            }
        }
        return null;
    }

    /**
     * Finds a variable with a particular name.
     *
     * @param name
     *            the name to look for
     * @return the VariableAttribute object if found, null otherwise
     */
    public VariableAttribute findVariable(String name) {
        return (VariableAttribute) findAttribute(name, variableAttributesSet);
    }

    /**
     * Finds an input with a particular name.
     *
     * @param name
     *            the name to look for
     * @return the InOutputAttribute object if found, null otherwise
     */
    public InOutputAttribute findInput(String name) {
        return (InOutputAttribute) findAttribute(name, inputAttributesSet);
    }

    /**
     * Finds an output with a particular name.
     *
     * @param name
     *            the name to look for
     * @return the InOutputAttribute object if found, null otherwise
     */
    public InOutputAttribute findOutput(String name) {
        return (InOutputAttribute) findAttribute(name, outputAttributesSet);
    }

    /**
     * Finds an input/output with a particular name
     *
     * @param name
     *            the name to look for
     * @return the InOutputAttribute object if found, null otherwise
     */
    public InOutputAttribute findInOutput(String name) {
        return (InOutputAttribute) findAttribute(name, inOutputAttributesSet);
    }

    /**
     * Queries a global description of the remote service. This description
     * contains the name of all the variables, inputs and outputs.
     * {@link #queryCompleteDescription()} can then be called to get more
     * information about the variables and inputs/outputs.
     *
     * @return whether the query has received an answer
     */
    public boolean queryGlobalDescription() {
        String request = "";
        XmlMessage message = queryToServer(request, true);
        if (message != null) {
            processGlobalDescription(message);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Queries a complete description of the remote service. Warning:
     * {@link #queryGlobalDescription()} must have been called before calling
     * this method. This description contains the names and descriptions of all
     * variables and attributes.
     */
    // \REVIEWTASK should optimize this process (one network exchange only?)
    public void queryCompleteDescription() {
        for (String input : inputNamesSet) {
            queryInputDescription(input);
        }
        for (String output : outputNamesSet) {
            queryOutputDescription(output);
        }
        for (String inoutput : inOutputNamesSet) {
            queryInOutputDescription(inoutput);
        }
        for (String variable : variableNamesSet) {
            VariableAttribute variableAttribute = queryVariableDescription(variable);
            if (variableAttribute == null) {
                System.out.println("Error queryVariableDescription : " + variable);
            }
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
        String request = "<variable name=\"" + name + "\"/>";
        XmlMessage message = queryToServer(request, true);
        if (message != null) {
            VariableAttribute vattr = findVariable(name);
            Element elt = XmlUtils.firstChild(message.getRootElement(), "variable");

            VariableAttribute attribute = null;
            if (elt != null) {
                attribute = processVariableDescription(elt, vattr);
            }
            if (attribute == null) {
                if (vattr != null) {
                    variableAttributesSet.remove(vattr);
                }
                if (variableNamesSet.contains(name)) {
                    variableNamesSet.remove(name);
                }
            } else {
                if (vattr == null) {
                    variableAttributesSet.add(attribute);
                }
                if (!variableNamesSet.contains(name)) {
                    variableNamesSet.add(name);
                }
            }
            return attribute;
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
    public VariableAttribute queryVariableModification(String name, String value) {
        VariableAttribute vattr = findVariable(name);
        if (vattr == null) {
            System.out.println("Unknown Variable: Description Not Available: " + name);
            return null;
        } else {
            String request = "<variable name=\"" + name + "\">";
            request += "<value>";
            request += XmlUtils.generateCDataSection(value);
            request += "</value></variable>";
            XmlMessage message = queryToServer(request, true);
            if (message != null) {
                VariableAttribute attr = processVariableDescription(XmlUtils.firstChild(message.getRootElement(), "variable"), vattr);
                return attr;
            }
            return null;
        }
    }

    /**
     * Queries a complete description for an input.
     *
     * @param name
     *            the input name
     * @return a InOutputAttribute object that contains the description, null if
     *         the request failed
     */
    public InOutputAttribute queryInputDescription(String name) {
        String request = "<" + InOutputAttribute.Input.getXMLTag() + " name=\"" + name + "\"/>";
        XmlMessage message = queryToServer(request, true);
        if (message != null) {
            InOutputAttribute ioattr = findInput(name);
            InOutputAttribute attr = processInOutputDescription(XmlUtils.firstChild(message.getRootElement(), InOutputAttribute.Input.getXMLTag()), ioattr);
            if (attr == null) {
                if (ioattr != null)
                    inputAttributesSet.remove(ioattr);
                if (inputNamesSet.contains(name))
                    inputNamesSet.remove(name);
            } else {
                if (ioattr == null)
                    inputAttributesSet.add(attr);
                if (!inputNamesSet.contains(name))
                    inputNamesSet.add(name);
            }
            return attr;
        }
        return null;
    }

    /**
     * Queries a complete description for an output.
     *
     * @param name
     *            the output name
     * @return a InOutputAttribute object that contains the description, null if
     *         the request failed
     */
    public InOutputAttribute queryOutputDescription(String name) {
        String request = "<" + InOutputAttribute.Output.getXMLTag() + " name=\"" + name + "\"/>";
        XmlMessage message = queryToServer(request, true);
        if (message != null) {
            InOutputAttribute ioattr = findOutput(name);
            InOutputAttribute attr = processInOutputDescription(XmlUtils.firstChild(message.getRootElement(), InOutputAttribute.Output.getXMLTag()), ioattr);
            if (attr == null) {
                if (ioattr != null)
                    outputAttributesSet.remove(ioattr);
                if (outputNamesSet.contains(name))
                    outputNamesSet.remove(name);
            } else {
                if (ioattr == null)
                    outputAttributesSet.add(attr);
                if (!outputNamesSet.contains(name))
                    outputNamesSet.add(name);
            }
            return attr;
        }
        return null;
    }

    /**
     * Queries a complete description for an input/output.
     *
     * @param name
     *            the input/output name
     * @return a InOutputAttribute object that contains the description, null if
     *         the request failed
     */
    public InOutputAttribute queryInOutputDescription(String name) {
        String request = "<" + InOutputAttribute.InOutput.getXMLTag() + " name=\"" + name + "\"/>";
        XmlMessage message = queryToServer(request, true);
        if (message != null) {
            InOutputAttribute ioattr = findInOutput(name);
            InOutputAttribute attr = processInOutputDescription(XmlUtils.firstChild(message.getRootElement(), InOutputAttribute.InOutput.getXMLTag()), ioattr);
            if (attr == null) {
                if (ioattr != null)
                    inOutputAttributesSet.remove(ioattr);
                if (inOutputNamesSet.contains(name))
                    inOutputNamesSet.remove(name);
            } else {
                if (ioattr == null)
                    inOutputAttributesSet.add(attr);
                if (inOutputNamesSet.contains(name))
                    inOutputNamesSet.add(name);
            }
            return attr;
        }
        return null;
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
            String request = "<subscribe name=\"" + va.getName() + "\"/>";
            queryToServer(request, false);
            return true;
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
            String request = "<unsubscribe name=\"" + va.getName() + "\"/>";
            queryToServer(request, false);
            return true;
        } else {
            System.out.println("variable unknown by client\n");
            return false;
        }
    }

    /**
     * Asks to lock the control server
     *
     * @return whether the control server was locked for this service
     */
    public boolean lock() {
        String request = "<lock/>";
        XmlMessage message = queryToServer(request, true);
        if (message != null) {
            Element elt = XmlUtils.firstChild(message.getRootElement(), "lock");
            String res = elt.getAttribute("result");
            int peer = BipUtils.hexStringToInt(elt.getAttribute("peer"));

            VariableAttribute vattr = findVariable("lock");
            if (vattr != null) {
                vattr.setValueStr(Integer.toString(peer));
            }
            if (res.equals("ok")) {
                if (peer != peerId) {
                    System.err.println("Lock ok, but id different : " + peer + " != " + peerId);
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Asks to unlock the control server.
     *
     * @return whether the control server was unlocked
     */
    public boolean unlock() {
        String request = "<unlock/>";
        XmlMessage message = queryToServer(request, true);
        if (message != null) {
            Element elt = XmlUtils.firstChild(message.getRootElement(), "unlock");
            String res = elt.getAttribute("result");
            int peer = BipUtils.hexStringToInt(elt.getAttribute("peer"));

            VariableAttribute vattr = findVariable("lock");
            if (vattr != null) {
                vattr.setValueStr(Integer.toString(peer));
            }
            if (res.equals("ok")) {
                if (peer != 0) {
                    System.err.println("unlock ok, but id no null : " + peer);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Processes the query to the control server.
     *
     * @param request
     *            request to send to the server
     * @param waitAnswer
     *            indicate whether the method must wait for an answer from the
     *            control server
     * @return the control answer or null if we do not wait for the answer or if
     *         the query failed
     */
    private XmlMessage queryToServer(String request, boolean waitAnswer) {
        synchronized (answerEvent) {
            if (isConnected()) {
                int theMsgId = messageId++;
                String str = BipUtils.intTo8HexString(theMsgId);
                str = "<controlQuery id=\"" + str + "\">" + request + "</controlQuery>";
                tcpClient.send(str);
                if (waitAnswer) {
                    try {
                        answerEvent.wait(MaxTimeToWait);
                        if (messageAnswer != null) {
                            XmlMessage m = messageAnswer;
                            messageAnswer = null;
                            if (checkMessage(m, theMsgId))
                                return m;
                        } else {
                            System.err.println("answer null to request " + request + " from " + Integer.toHexString(getPeerId()));
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
     * Checks whether the answer id has the awaited value (the same value as the
     * query id).
     *
     * @param message
     *            answer from the control server
     * @param messageId
     *            id of the query
     * @return whether the answer has the good id, that is to say the value
     *         'messageId'
     */
    private boolean checkMessage(XmlMessage message, int messageId) {
        if (message != null && message.getRootElement() != null) {
            Attr attr = message.getRootElement().getAttributeNode("id");
            if (attr != null && BipUtils.hexStringToInt(attr.getValue()) == messageId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Processes the answer to a query for global description.
     *
     * @param message
     *            the answer to a query for global description
     */
    private void processGlobalDescription(XmlMessage message) {
        variableNamesSet.clear();
        inOutputNamesSet.clear();
        inputNamesSet.clear();
        outputNamesSet.clear();
        variableAttributesSet.clear();
        inputAttributesSet.clear();
        outputAttributesSet.clear();
        inOutputAttributesSet.clear();

        NodeList nodeList = message.getRootElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String nodeName = node.getNodeName();
            if (nodeName.equals("variable")) {
                variableNamesSet.add(((Element) node).getAttribute("name"));
            } else if (nodeName.equals(InOutputAttribute.Input.getXMLTag())) {
                inputNamesSet.add(((Element) node).getAttribute("name"));
            } else if (nodeName.equals(InOutputAttribute.Output.getXMLTag())) {
                outputNamesSet.add(((Element) node).getAttribute("name"));
            } else if (nodeName.equals(InOutputAttribute.InOutput.getXMLTag())) {
                inOutputNamesSet.add(((Element) node).getAttribute("name"));
            } else {
                System.out.println("Unknown kind " + nodeName);
            }
        }
    }

    /**
     * Processes the answer to a query for complete variable description.
     *
     * @param elt
     *            the answer to a query for complete variable description
     * @param vattr
     *            null or a VariableAttribute object that already exists, then
     *            this description will be update
     * @return a VariableAttribute object (vattr if non null) with the
     *         description, null if the query failed.
     */
    private VariableAttribute processVariableDescription(Element elt, VariableAttribute vattr) {
        Attr nameAttr = elt.getAttributeNode("name");
        if (nameAttr != null) {
            VariableAttribute attr = vattr;
            if (vattr == null) {
                attr = new VariableAttribute(nameAttr.getValue());
            }
            attr.extractInfoFromXML(elt);
            return attr;
        }
        return null;
    }

    /**
     * Processes the answer to a query for complete in/output description.
     *
     * @param elt
     *            the answer to a query for complete in/output description
     * @param ioattr
     *            null or a VariableAttribute object that already exists, then
     *            this description will be update.
     * @return a VariableAttribute object (ioattr if non null) with the
     *         description
     */
    private InOutputAttribute processInOutputDescription(Element elt, InOutputAttribute ioattr) {
        Attr nameAttr = elt.getAttributeNode("name");
        if (nameAttr != null) {
            InOutputAttribute attr = ioattr;
            if (ioattr == null) {
                attr = new InOutputAttribute(nameAttr.getValue());
            }
            attr.setKind(InOutputAttribute.IOKindFromName(elt.getNodeName()));
            attr.extractInfoFromXML(elt);
            return attr;
        }
        return null;
    }

    /**
     * Creates an XML DOM node containing the variables and inputs/outputse of
     * the remote service. The created node is created using the given
     * {@link Document} and is returned.
     *
     * @param doc
     *            the {@link Document} used for the creation of the returned
     *            {@link Element}
     * @return
     */
    public Element createXmlElement(Document doc) {
        Element eltService = doc.createElement("service");
        for (VariableAttribute variable : variableAttributesSet) {
            eltService.appendChild(variable.createXmlElement(doc));
        }
        for (InOutputAttribute inoutput : inputAttributesSet) {
            eltService.appendChild(inoutput.createXmlElement(doc));
        }
        for (InOutputAttribute inoutput : inOutputAttributesSet) {
            eltService.appendChild(inoutput.createXmlElement(doc));
        }
        for (InOutputAttribute inoutput : outputAttributesSet) {
            eltService.appendChild(inoutput.createXmlElement(doc));
        }
        return eltService;
    }

    /**
     * @return a read only version of the desired field
     */
    public Set<InOutputAttribute> getInOutputAttributesSet() {
        return Collections.unmodifiableSet(inOutputAttributesSet);
    }

    /**
     * @return a read only version of the desired field
     */
    public Set<String> getInOutputNamesSet() {
        return Collections.unmodifiableSet(inOutputNamesSet);
    }

    /**
     * @return a read only version of the desired field
     */
    public Set<InOutputAttribute> getInputAttributesSet() {
        return Collections.unmodifiableSet(inputAttributesSet);
    }

    /**
     * @return a read only version of the desired field
     */
    public Set<String> getInputNamesSet() {
        return Collections.unmodifiableSet(inputNamesSet);
    }

    /**
     * @return a read only version of the desired field
     */
    public Set<InOutputAttribute> getOutputAttributesSet() {
        return Collections.unmodifiableSet(outputAttributesSet);
    }

    /**
     * @return a read only version of the desired field
     */
    public Set<String> getOutputNamesSet() {
        return Collections.unmodifiableSet(outputNamesSet);
    }

    /**
     * @return a read only version of the desired field
     */
    public Set<VariableAttribute> getVariableAttributesSet() {
        return Collections.unmodifiableSet(variableAttributesSet);
    }

    /**
     * @return a read only version of the desired field
     */
    public Set<String> getVariableNamesSet() {
        return Collections.unmodifiableSet(variableNamesSet);
    }

}
