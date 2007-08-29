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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Element;

import fr.prima.omiscid.com.TcpClient;
import fr.prima.omiscid.com.XmlMessage;
import fr.prima.omiscid.com.interf.BipMessageListener;
import fr.prima.omiscid.control.interf.GlobalConstants;
import fr.prima.omiscid.control.message.answer.ControlAnswer;
import fr.prima.omiscid.control.message.query.ControlQuery;
import fr.prima.omiscid.user.connector.Message;
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
//\REVIEWTASK shouldn't contain the sets it contains (should be in an higher level class (this one has two functionnalities))
public class ControlClient implements BipMessageListener {
    /** The max time to wait for the answer to a query */
    private final int maxTimeToWait = 5000; // milliseconds

    // \REVIEWTASK should be configurable in a specific way (env variable?)

    /** The connection to the control port */
    private TcpClient tcpClient = null;

    /** Peer id used in BIP exhange */
    private int peerId = 0;

    /** Query Id: the answer to a query have the same id that the query */
    private int messageId = 0;


    private class MessageAnswerMonitor {
        private boolean sending = false;
        private Map<Integer, Object> events = new Hashtable<Integer, Object>();
        private Map<Integer, ControlAnswer> answers = new Hashtable<Integer, ControlAnswer>();
        private ReentrantLock lockForEventAdditionAndWaiting = new ReentrantLock();
        
        synchronized private void pushMessageAnswer(ControlAnswer controlAnswer) throws InterruptedException {
            while (sending) wait();
            int msgId = Utility.hexStringToInt(controlAnswer.getId());
            if (answers.containsKey(msgId)) {
                System.err.println("Warning: "+Utility.intTo8HexString(ControlClient.this.peerId)+" received a non-null message answer from "+Utility.intTo8HexString(ControlClient.this.getPeerId())+" while receiving another one, should not happen");
            }
            Object event = events.remove(msgId);
            if (event == null) {
                System.err.println("Warning: "+Utility.intTo8HexString(ControlClient.this.peerId)+" dropped a control answer from "+Utility.intTo8HexString(ControlClient.this.getPeerId())+". May be due to previous timeout");
                // This could happen: when there was a timeout just before, or when we break the wait (is it really possible)? 
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
        } catch (IOException e) {
            tcpClient = null;
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
            tcpClient.removeBipMessageListener(this);
            tcpClient = null;
            controlEventListenersSet.clear();
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
                synchronized (controlEventListenersSet) {
                    for (ControlEventListener listener : controlEventListenersSet) {
                        listener.receivedControlEvent(message);
                    }
                }
                return;
            }
        }
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

    public ControlAnswer queryToServer(ControlQuery controlQuery, boolean waitAnswer) throws MarshalException, ValidationException {
        if (isConnected()) {
            int theMsgId;
            synchronized (this) {
                theMsgId = messageId++;
            }
            String strMessageId = Utility.intTo8HexString(theMsgId).toLowerCase();
            controlQuery.setId(strMessageId);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            controlQuery.marshal(new OutputStreamWriter(byteArrayOutputStream));
            try {
                monitor.willSend();
                tcpClient.send(byteArrayOutputStream.toByteArray());
                byteArrayOutputStream.close();
                if (!waitAnswer) {
                    monitor.sent();
                } else {
                    ControlAnswer controlAnswer = monitor.willProcess(theMsgId,maxTimeToWait);
                    if (controlAnswer == null) {
                        System.err.println(Utility.intTo8HexString(this.peerId)+" got a null answer from " + Utility.intTo8HexString(getPeerId()));
                    }
                    return controlAnswer;
                }
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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



}
