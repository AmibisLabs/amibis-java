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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Element;

import fr.prima.omiscid.com.TcpClient;
import fr.prima.omiscid.com.XmlMessage;
import fr.prima.omiscid.com.interf.BipMessageListener;
import fr.prima.omiscid.control.interf.GlobalConstants;
import fr.prima.omiscid.control.message.answer.ControlAnswer;
import fr.prima.omiscid.control.message.answer.ControlAnswerItem;
import fr.prima.omiscid.control.message.answer.ControlEvent;
import fr.prima.omiscid.control.message.answer.types.CA_LockResultType;
import fr.prima.omiscid.control.message.query.ControlQuery;
import fr.prima.omiscid.control.message.query.ControlQueryItem;
import fr.prima.omiscid.control.message.query.FullDescription;
import fr.prima.omiscid.control.message.query.Inoutput;
import fr.prima.omiscid.control.message.query.Input;
import fr.prima.omiscid.control.message.query.Lock;
import fr.prima.omiscid.control.message.query.Output;
import fr.prima.omiscid.control.message.query.Subscribe;
import fr.prima.omiscid.control.message.query.Unlock;
import fr.prima.omiscid.control.message.query.Unsubscribe;
import fr.prima.omiscid.control.message.query.Variable;
import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.exception.MessageInterpretationException;
import fr.prima.omiscid.user.util.Utility;

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
//\REVIEWTASK shouldn't this be a monitor?
public class ControlClient implements BipMessageListener {
    /** The max time to wait for the answer to a query */
    private final int maxTimeToWait = 2000; // milliseconds

    // \REVIEWTASK should be configurable in a specific way (env variable?)

    /** The connection to the control port */
    private TcpClient tcpClient = null;

    /** Peer id used in BIP exhange */
    private int peerId = 0;

    /** Query Id: the answer to a query have the same id that the query */
    private int messageId = 0;


    private static class MessageAnswerMonitor {
        private boolean sending = false;
        private Map<Integer, Object> events = new Hashtable<Integer, Object>();
        private Map<Integer, ControlAnswer> answers = new Hashtable<Integer, ControlAnswer>();
        private ReentrantLock lockForEventAdditionAndWaiting = new ReentrantLock();
        
        synchronized private void pushMessageAnswer(ControlAnswer controlAnswer) throws InterruptedException {
            while (sending) wait();
            int msgId = Utility.hexStringToInt(controlAnswer.getId());
            if (answers.containsKey(msgId)) {
                System.err.println("Warning: non-null message answer while receiving another one, should not happen");
            }
            Object event = events.remove(msgId);
            if (event == null) {
                System.err.println("Warning: dropped control answer. May be due to previous timeout");
            } else {
                lockForEventAdditionAndWaiting.lock();
                answers.put(msgId, controlAnswer);
                synchronized (event) {
                    event.notifyAll();
                }
                lockForEventAdditionAndWaiting.unlock();
            }
        }
        synchronized private void willSend() throws InterruptedException {
            while (sending) wait();
            sending = true;
        }
        synchronized private void sent() throws InterruptedException {
            sending = false;
            notifyAll();
        }
        private ControlAnswer willProcess(int msgId, long timeout) throws InterruptedException {
            Object event;
            synchronized (this) {
                lockForEventAdditionAndWaiting.lock();
                sent();
                if (events.containsKey(msgId)) {
                    System.err.println("Warning: key already present while waiting for answer, wrong message iding");
                }
                event = new Object();
                events.put(msgId, event);
            }
            synchronized (event) {
                lockForEventAdditionAndWaiting.unlock();
                event.wait(timeout); // wait event without having the lock on "this"
                synchronized (this) {
                    ControlAnswer answer = answers.remove(msgId);
                    return answer;
                }
            }
        }
    }
    private MessageAnswerMonitor monitor = new MessageAnswerMonitor();

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
            if (root.getNodeName().equals(GlobalConstants.controlAnswerXMLTag)) {
                try {
                    ControlAnswer answer = ControlAnswer.unmarshal(new InputStreamReader(new ByteArrayInputStream(message.getBuffer())));
                    monitor.pushMessageAnswer(answer);
                    return;
                } catch (MarshalException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ValidationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }else {
                try {
                    ControlEvent.unmarshal(new InputStreamReader(new ByteArrayInputStream(message.getBuffer())));
                    synchronized (controlEventListenersSet) {
                        for (ControlEventListener listener : controlEventListenersSet) {
                            try {
                                listener.receivedControlEvent(new XmlMessage(message));
                            } catch (MessageInterpretationException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            //TODO xml message is perhaps not necessary
                        }
                    }
                    return;
                } catch (MarshalException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ValidationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
//      XmlMessage xmlMessage = XmlMessage.newUnchecked(message);
//      if (xmlMessage != null && xmlMessage.getRootElement() != null) {
//      Element root = xmlMessage.getRootElement();
//      if (root.getNodeName().equals("controlAnswer")) {
//      synchronized (answerEvent) {
//      messageAnswer = xmlMessage;
//      answerEvent.notify();
//      }
//      } else if (root.getNodeName().equals("controlEvent")) {
//      synchronized (controlEventListenersSet) {
//      for (ControlEventListener listener : controlEventListenersSet) {
//      listener.receivedControlEvent(xmlMessage);
//      }
//      }
//      } else {
//      System.err.println("Unknown message kind : " + root.getNodeName());
//      }
//      }
    }

    public void disconnected(int remotePeerId) {
        //TODO
    }

    public void connected(int remotePeerId) {
        //TODO
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

    public InOutputAttribute findConnector(int peerId) {
        for (Iterable<InOutputAttribute> list : new Iterable[]{inputAttributesSet,outputAttributesSet,inOutputAttributesSet}) {
            for (InOutputAttribute attribute : list) {
                if (attribute.getPeerId() == peerId) {
                    return attribute;
                }
            }
        }
        return null;
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
        try {
            ControlQuery controlQuery = new ControlQuery();
            ControlAnswer controlAnswer = queryToServer(controlQuery, true);
            if (controlAnswer != null) {
                processGlobalDescription(controlAnswer);
                return true;
            } else {
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

    /**
     * Queries a complete description of the remote service. Warning:
     * {@link #queryGlobalDescription()} must have been called before calling
     * this method. This description contains the names and descriptions of all
     * variables and attributes.
     */
    // \REVIEWTASK should optimize this process (one network exchange only?)
    public void queryCompleteDescription() {
        ControlQuery controlQuery = new ControlQuery();
        FullDescription fullDescription = new FullDescription();
        ControlQueryItem controlQueryItem = new ControlQueryItem();
        controlQueryItem.setFullDescription(fullDescription);
        controlQuery.addControlQueryItem(controlQueryItem);

        try {
            ControlAnswer controlAnswer = queryToServer(controlQuery, true);
            if (controlAnswer != null) {
                for (ControlAnswerItem item : controlAnswer.getControlAnswerItem()) {
                    processControlAnswerItem(item);
                }
            }
        } catch (MarshalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ValidationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
//        for (String input : inputNamesSet) {
//            queryInputDescription(input);
//        }
//        for (String output : outputNamesSet) {
//            queryOutputDescription(output);
//        }
//        for (String inoutput : inOutputNamesSet) {
//            queryInOutputDescription(inoutput);
//        }
//        for (String variable : variableNamesSet) {
//            VariableAttribute variableAttribute = queryVariableDescription(variable);
//            if (variableAttribute == null) {
//                System.err.println("Error queryVariableDescription : " + variable);
//            }
//        }
    }

    private void processControlAnswerItem(ControlAnswerItem item) {
        Object choice = item.getChoiceValue();
        if (choice instanceof fr.prima.omiscid.control.message.answer.Variable) {
            fr.prima.omiscid.control.message.answer.Variable variable = item.getVariable();
            VariableAttribute vattr = findVariable(variable.getName());
            VariableAttribute attr = processVariableDescription(item, vattr);
            if (vattr == null) {
                variableAttributesSet.add(attr);
            }
            if (!variableNamesSet.contains(variable.getName())) {
                variableNamesSet.add(variable.getName());
            }
        } else if (choice instanceof fr.prima.omiscid.control.message.answer.Inoutput) {
            fr.prima.omiscid.control.message.answer.Inoutput inoutput = item.getInoutput();
            InOutputAttribute ioattr = findInOutput(inoutput.getName());
            InOutputAttribute attr = processInOutputDescription(item, ioattr);
            if (ioattr == null) {
                inOutputAttributesSet.add(attr);
            }
            if (!inOutputNamesSet.contains(inoutput.getName())) {
                inOutputNamesSet.add(inoutput.getName());
            }
        } else if (choice instanceof fr.prima.omiscid.control.message.answer.Output) {
            fr.prima.omiscid.control.message.answer.Output inoutput = item.getOutput();
            InOutputAttribute ioattr = findOutput(inoutput.getName());
            InOutputAttribute attr = processInOutputDescription(item, ioattr);
            if (ioattr == null) {
                outputAttributesSet.add(attr);
            }
            if (!outputNamesSet.contains(inoutput.getName())) {
                outputNamesSet.add(inoutput.getName());
            }
        } else if (choice instanceof fr.prima.omiscid.control.message.answer.Input) {
            fr.prima.omiscid.control.message.answer.Input inoutput = item.getInput();
            InOutputAttribute ioattr = findInput(inoutput.getName());
            InOutputAttribute attr = processInOutputDescription(item, ioattr);
            if (ioattr == null) {
                inputAttributesSet.add(attr);
            }
            if (!inputNamesSet.contains(inoutput.getName())) {
                inputNamesSet.add(inoutput.getName());
            }
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
        Variable variable = new Variable();
        variable.setName(name);
        ControlQueryItem controlQueryItem = new ControlQueryItem();
        controlQueryItem.setVariable(variable);
        controlQuery.addControlQueryItem(controlQueryItem);

        try {
            ControlAnswer controlAnswer = queryToServer(controlQuery, true);
            if (controlAnswer != null && controlAnswer.getControlAnswerItemCount() != 0) {
                VariableAttribute vattr = findVariable(name);
                VariableAttribute attr = processVariableDescription(controlAnswer.getControlAnswerItem(0), vattr);
                if (attr == null) {
                    if (vattr != null) {
                        variableAttributesSet.remove(vattr);
                    }
                    if (variableNamesSet.contains(name)) {
                        variableNamesSet.remove(name);
                    }
                } else {
                    if (vattr == null) {
                        variableAttributesSet.add(attr);
                    }
                    if (!variableNamesSet.contains(name)) {
                        variableNamesSet.add(name);
                    }
                }
                return attr;
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
    public VariableAttribute queryVariableModification(String name, String value) {
        VariableAttribute vattr = findVariable(name);
        if (vattr == null) {
            System.err.println("Unknown Variable: Description Not Available: " + name);
            return null;
        } else {
            ControlQuery controlQuery = new ControlQuery();
            Variable variable = new Variable();
            variable.setName(name);
            variable.setValue(value);
            ControlQueryItem controlQueryItem = new ControlQueryItem();
            controlQueryItem.setVariable(variable);
            controlQuery.addControlQueryItem(controlQueryItem);
//          String request = "<variable name=\"" + name + "\">";
//          request += "<value>";
//          request += XmlUtils.generateCDataSection(value);
//          request += "</value></variable>";
//          XmlMessage message = queryToServer(request, true);
//          if (message != null) {
            try {
                ControlAnswer controlAnswer = queryToServer(controlQuery, true);
                if (controlAnswer != null) {
                    VariableAttribute attr = processVariableDescription(controlAnswer.getControlAnswerItem(0), vattr);
                    return attr;
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
        ControlQuery controlQuery = new ControlQuery();
        Input input = new Input();
        input.setName(name);
        ControlQueryItem controlQueryItem = new ControlQueryItem();
        controlQueryItem.setInput(input);
        controlQuery.addControlQueryItem(controlQueryItem);

        try {
            ControlAnswer controlAnswer = queryToServer(controlQuery, true);
            if (controlAnswer != null) {
                InOutputAttribute ioattr = findInput(name);
                InOutputAttribute attr = processInOutputDescription(controlAnswer.getControlAnswerItem(0), ioattr);
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
     * Queries a complete description for an output.
     *
     * @param name
     *            the output name
     * @return a InOutputAttribute object that contains the description, null if
     *         the request failed
     */
    public InOutputAttribute queryOutputDescription(String name) {
        ControlQuery controlQuery = new ControlQuery();
        Output output = new Output();
        output.setName(name);
        ControlQueryItem controlQueryItem = new ControlQueryItem();
        controlQueryItem.setOutput(output);
        controlQuery.addControlQueryItem(controlQueryItem);

        try {
            ControlAnswer controlAnswer = queryToServer(controlQuery, true);
            if (controlAnswer != null) {
                InOutputAttribute ioattr = findOutput(name);
                InOutputAttribute attr = processInOutputDescription(controlAnswer.getControlAnswerItem(0), ioattr);
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
        } catch (MarshalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ValidationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;


//      String request = "<" + ConnectorType.OUTPUT.getXMLTag() + " name=\"" + name + "\"/>";
//      XmlMessage message = queryToServer(request, true);
//      if (message != null) {
//      InOutputAttribute ioattr = findOutput(name);
//      InOutputAttribute attr = processInOutputDescription(XmlUtils.firstChild(message.getRootElement(), ConnectorType.OUTPUT.getXMLTag()), ioattr);
//      if (attr == null) {
//      if (ioattr != null)
//      outputAttributesSet.remove(ioattr);
//      if (outputNamesSet.contains(name))
//      outputNamesSet.remove(name);
//      } else {
//      if (ioattr == null)
//      outputAttributesSet.add(attr);
//      if (!outputNamesSet.contains(name))
//      outputNamesSet.add(name);
//      }
//      return attr;
//      }
//      return null;
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
        ControlQuery controlQuery = new ControlQuery();
        Inoutput inoutput = new Inoutput();
        inoutput.setName(name);
        ControlQueryItem controlQueryItem = new ControlQueryItem();
        controlQueryItem.setInoutput(inoutput);
        controlQuery.addControlQueryItem(controlQueryItem);

        try {
            ControlAnswer controlAnswer = queryToServer(controlQuery, true);
            if (controlAnswer != null) {
                InOutputAttribute ioattr = findInOutput(name);
                InOutputAttribute attr = processInOutputDescription(controlAnswer.getControlAnswerItem(0), ioattr);
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
            controlQueryItem.setSubscribe(subscribe );
            controlQuery.addControlQueryItem(controlQueryItem);
//          String request = "<subscribe name=\"" + va.getName() + "\"/>";
//          queryToServer(request, false);
            try {
                ControlAnswer controlAnswer = queryToServer(controlQuery, true);
                if (controlAnswer != null && controlAnswer.getControlAnswerItemCount() != 0) {
                    VariableAttribute vattr = findVariable(varName);
                    VariableAttribute attr = processVariableDescription(controlAnswer.getControlAnswerItem(0), vattr);
                    if (attr == null) {
                        if (vattr != null) {
                            variableAttributesSet.remove(vattr);
                        }
                        if (variableNamesSet.contains(varName)) {
                            variableNamesSet.remove(varName);
                        }
                    } else {
                        if (vattr == null) {
                            variableAttributesSet.add(attr);
                        }
                        if (!variableNamesSet.contains(varName)) {
                            variableNamesSet.add(varName);
                        }
                    }
                }
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
        controlQueryItem.setLock(new Lock());
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
//      String request = "<lock/>";
//      XmlMessage message = queryToServer(request, true);
//      if (message != null) {
//      Element elt = XmlUtils.firstChild(message.getRootElement(), "lock");
//      String res = elt.getAttribute("result");
//      int peer = BipUtils.hexStringToInt(elt.getAttribute("peer"));

//      VariableAttribute vattr = findVariable("lock");
//      if (vattr != null) {
//      vattr.setValueStr(Integer.toString(peer));
//      }
//      if (res.equals("ok")) {
//      if (peer != peerId) {
//      System.err.println("Lock ok, but id different : " + peer + " != " + peerId);
//      return false;
//      }
//      return true;
//      }
//      }
//      return false;
    }

    /**
     * Asks to unlock the control server.
     *
     * @return whether the control server was unlocked
     */
    public boolean unlock() {
        ControlQuery controlQuery = new ControlQuery();
        ControlQueryItem controlQueryItem = new ControlQueryItem();
        controlQueryItem.setUnlock(new Unlock());
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
//      String request = "<unlock/>";
//      XmlMessage message = queryToServer(request, true);
//      if (message != null) {
//      Element elt = XmlUtils.firstChild(message.getRootElement(), "unlock");
//      String res = elt.getAttribute("result");
//      int peer = BipUtils.hexStringToInt(elt.getAttribute("peer"));

//      VariableAttribute vattr = findVariable("lock");
//      if (vattr != null) {
//      vattr.setValueStr(Integer.toString(peer));
//      }
//      if (res.equals("ok")) {
//      if (peer != 0) {
//      System.err.println("unlock ok, but id no null : " + peer);
//      }
//      return true;
//      }
//      }
//      return false;
    }

//  /**
//  * Processes the query to the control server.
//  *
//  * @param request
//  *            request to send to the server
//  * @param waitAnswer
//  *            indicate whether the method must wait for an answer from the
//  *            control server
//  * @return the control answer or null if we do not wait for the answer or if
//  *         the query failed
//  */
//  private XmlMessage queryToServer(String request, boolean waitAnswer) {
//  synchronized (answerEvent) {
//  if (isConnected()) {
//  int theMsgId = messageId++;
//  String str = BipUtils.intTo8HexString(theMsgId);
//  str = "<controlQuery id=\"" + str + "\">" + request + "</controlQuery>";
//  tcpClient.send(str);
//  if (waitAnswer) {
//  try {
//  answerEvent.wait(MaxTimeToWait);
//  if (messageAnswer != null) {
//  XmlMessage m = null;
//  try {
//  m = new XmlMessage(messageAnswer);
//  } catch (BipMessageInterpretationException e) {
//  // TODO Auto-generated catch block
//  e.printStackTrace();
//  }
//  messageAnswer = null;
//  if (checkMessage(m, theMsgId))
//  return m;
//  } else {
//  System.err.println("answer null to request " + request + " from " + Integer.toHexString(getPeerId()));
//  }
//  } catch (InterruptedException e) {
//  e.printStackTrace();
//  }
//  }
//  }
//  return null;
//  }
//  }

    private ControlAnswer queryToServer(ControlQuery controlQuery, boolean waitAnswer) throws MarshalException, ValidationException {
        if (isConnected()) {
            int theMsgId;
            synchronized (this) {
                theMsgId = messageId++;
            }
            String strMessageId = Utility.intTo8HexString(theMsgId);
            controlQuery.setId(strMessageId);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            controlQuery.marshal(new OutputStreamWriter(byteArrayOutputStream));
            try {
                monitor.willSend();
                tcpClient.send(byteArrayOutputStream.toByteArray());
                if (!waitAnswer) {
                    monitor.sent();
                } else {
                    ControlAnswer controlAnswer = monitor.willProcess(theMsgId,maxTimeToWait);
                    if (controlAnswer == null) {
                        System.err.println("answer null from " + Utility.intTo8HexString(getPeerId()));
                    }
                    return controlAnswer;
                }
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return null;
    }

//  /**
//  * Checks whether the answer id has the awaited value (the same value as the
//  * query id).
//  *
//  * @param message
//  *            answer from the control server
//  * @param messageId
//  *            id of the query
//  * @return whether the answer has the good id, that is to say the value
//  *         'messageId'
//  */
//  private boolean checkMessage(XmlMessage message, int messageId) {
//  if (message != null && message.getRootElement() != null) {
//  Attr attr = message.getRootElement().getAttributeNode("id");
//  if (attr != null && BipUtils.hexStringToInt(attr.getValue()) == messageId) {
//  return true;
//  }
//  }
//  return false;
//  }

    /**
     * Processes the answer to a query for global description.
     *
     * @param message
     *            the answer to a query for global description
     */
    private void processGlobalDescription(ControlAnswer controlAnswer) {
        variableNamesSet.clear();
        inOutputNamesSet.clear();
        inputNamesSet.clear();
        outputNamesSet.clear();
        variableAttributesSet.clear();
        inputAttributesSet.clear();
        outputAttributesSet.clear();
        inOutputAttributesSet.clear();

        for (ControlAnswerItem item : controlAnswer.getControlAnswerItem()) {
            Object choice = item.getChoiceValue();
            if (choice instanceof fr.prima.omiscid.control.message.answer.Variable) {
                variableNamesSet.add(item.getVariable().getName());
            } else if (choice instanceof fr.prima.omiscid.control.message.answer.Input) {
                inputNamesSet.add(item.getInput().getName());
            } else if (choice instanceof fr.prima.omiscid.control.message.answer.Output) {
                outputNamesSet.add(item.getOutput().getName());
            } else if (choice instanceof fr.prima.omiscid.control.message.answer.Inoutput) {
                inOutputNamesSet.add(item.getInoutput().getName());
            } else {
                System.err.println("unhandled element "+choice);
            }
        }
//      NodeList nodeList = message.getRootElement().getChildNodes();
//      for (int i = 0; i < nodeList.getLength(); i++) {
//      Node node = nodeList.item(i);
//      String nodeName = node.getNodeName();
//      if (nodeName.equals("variable")) {
//      variableNamesSet.add(((Element) node).getAttribute("name"));
//      } else if (nodeName.equals(ConnectorType.INPUT.getXMLTag())) {
//      inputNamesSet.add(((Element) node).getAttribute("name"));
//      } else if (nodeName.equals(ConnectorType.OUTPUT.getXMLTag())) {
//      outputNamesSet.add(((Element) node).getAttribute("name"));
//      } else if (nodeName.equals(ConnectorType.INOUTPUT.getXMLTag())) {
//      inOutputNamesSet.add(((Element) node).getAttribute("name"));
//      } else {
//      System.err.println("Unknown kind " + nodeName);
//      }
//      }
    }

    /**
     * Processes the answer to a query for complete variable description.
     *
     * @param item
     *            the answer to a query for complete variable description
     * @param vattr
     *            null or a VariableAttribute object that already exists, then
     *            this description will be update
     * @return a VariableAttribute object (vattr if non null) with the
     *         description, null if the query failed.
     */
    private VariableAttribute processVariableDescription(ControlAnswerItem item, VariableAttribute vattr) {
        if (vattr == null) {
            return new VariableAttribute(item.getVariable());
        } else {
            vattr.init(item.getVariable());
            return vattr;
        }
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
    private InOutputAttribute processInOutputDescription(ControlAnswerItem item, InOutputAttribute ioattr) {
        if (ioattr == null) {
            return new InOutputAttribute(item);
        } else {
            ioattr.init(item);
            return ioattr;
        }

//      String name = null;
//      ConnectorType connectorType;
//      if (item.getChoiceValue() instanceof Input) {
//      name = item.getInput().getName();
//      connectorType = ConnectorType.INPUT;
//      } else if (item.getChoiceValue() instanceof Output) {
//      name = item.getOutput().getName();
//      connectorType = ConnectorType.OUTPUT;
//      } else if (item.getChoiceValue() instanceof InOutputAttribute) {
//      name = item.getInoutput().getName();
//      connectorType = ConnectorType.INOUTPUT;
//      }
//      if (name != null) {
//      if (ioattr == null) {
//      ioattr = new InOutputAttribute(name);
//      }
//      ioattr.setConnectorType(connectorType)
//      return ioattr;
//      }
//      return null;
//      Attr nameAttr = elt.getAttributeNode("name");
//      if (nameAttr != null) {
//      InOutputAttribute attr = ioattr;
//      if (ioattr == null) {
//      attr = new InOutputAttribute(nameAttr.getValue());
//      }
//      attr.setConnectorType(InOutputAttribute.IOKindFromName(elt.getNodeName()));
//      attr.extractInfoFromXML(elt);
//      return attr;
//      }
//      return null;
    }

//    /**
//     * Creates an XML DOM node containing the variables and inputs/outputse of
//     * the remote service. The created node is created using the given
//     * {@link Document} and is returned.
//     *
//     * @param doc
//     *            the {@link Document} used for the creation of the returned
//     *            {@link Element}
//     * @return
//     */
//    public Element createXmlElement(Document doc) {
//        Element eltService = doc.createElement("service");
//        for (VariableAttribute variable : variableAttributesSet) {
//            eltService.appendChild(variable.createXmlElement(doc));
//        }
//        for (InOutputAttribute inoutput : inputAttributesSet) {
//            eltService.appendChild(inoutput.createXmlElement(doc));
//        }
//        for (InOutputAttribute inoutput : inOutputAttributesSet) {
//            eltService.appendChild(inoutput.createXmlElement(doc));
//        }
//        for (InOutputAttribute inoutput : outputAttributesSet) {
//            eltService.appendChild(inoutput.createXmlElement(doc));
//        }
//        return eltService;
//    }

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
