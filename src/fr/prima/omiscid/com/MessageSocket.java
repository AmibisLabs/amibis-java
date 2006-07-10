package fr.prima.omiscid.com;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.prima.omiscid.com.interf.BipMessageListener;
import fr.prima.omiscid.com.interf.Message;

/**
 * Manages the buffer to store bytes and parsed BIP messages. The buffer can
 * receive bytes. The method {@link #received(int)} gives the number of byte
 * added to the buffer. The method {@link #process()} tries to parse BIP
 * message.
 *
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
final class ReceiveBuffer {
    /**
     * The MessageSocket that work with this object. New messages will be
     * notified to this object
     */
    private MessageSocket messageSocket;

    /**
     * The max length of the buffer used to receive messages
     */
    private int BUFFER_LENGTH = 64000;

    /** The buffer used to receive byte */
    public byte[] buffer;

    /**
     * The index of the first byte in the buffer that can be used to build
     * message
     */
    private int position;

    /**
     * Array used to store temporarly data extracted from header of BIP message :
     * peer id, message id, message length
     */
    private String[] messageAttribute;

    /** Number of byte stored in the buffer */
    private int nbByte;

    /**
     * Creates a new instance of ReceiveBuffer.
     *
     * @param messageSocket
     *            the MessageSocket object working with this ReceiveBuffer
     *            object. The OMiSCID message will be returned to this object.
     */
    public ReceiveBuffer(MessageSocket messageSocket) {
        this.messageSocket = messageSocket;
        buffer = new byte[BUFFER_LENGTH];
        position = 0;
        messageAttribute = new String[3];
        nbByte = 0;
    }

    /**
     * Gives to the object the number of byte added to its buffer.
     *
     * @param nb
     *            number of byte newly added
     */
    public synchronized void received(int nb) {
        nbByte += nb;
    }

    /**
     * @return the offset at which we can begin to use the buffer to store more
     *         bytes
     */
    public int offset() {
        return position + nbByte;
    }

    /**
     * @return the number of bytes available in the buffer (where new bytes can
     *         be stored)
     */
    public int available() {
        return BUFFER_LENGTH - (position + nbByte);
    }

    /**
     * Tries to parse message in the buffer.
     */
    public synchronized void process() {
        // System.out.println("MessageSocket::Process");
        try {
            int res = goodBeginning();
            while (res != 0) {
                if (res == 2) { // a message beginning
                    int length = BipUtils.hexStringToInt(messageAttribute[2]);
                    int dec = position + length;
                    // test the end of the message
                    if (buffer[dec] == '\r' && buffer[dec + 1] == '\n') {
                        // replace the message end by null character
                        // so with have one null character after the message
                        // body
                        buffer[dec] = 0;
                        buffer[dec + 1] = 0;

                        // new message
                        int pid = BipUtils.hexStringToInt(messageAttribute[0]);
                        int mid = BipUtils.hexStringToInt(messageAttribute[1]);

                        if (!messageSocket.isInitMessageReceived()) {
                            messageSocket.initMessageReceived(pid);
                            if (!messageSocket.isInitMessageSent()) {
                                messageSocket.initializeConnection();
                            }
                        } else {
                            messageSocket.newMessageReceived(new MessageImpl(buffer, position, length, mid, pid));
                        }

                        readAdvance(length);
                        readAdvance(2); // "\r\n" the two byte at the message
                        // end
                    } else {
                        System.out.println("Not good end");
                    }
                } else if (res == 1) { // bad beginning
                    readAdvance(1);
                }
                if (res != 0)
                    res = goodBeginning();
            }
            moveData();
        } catch (NumberFormatException e) {
            System.err.println("ReceiveBuffer::process");
            e.printStackTrace();
        }
    }

    /**
     * Increments state variable about buffer management to indicate that byte
     * of the buffer have been used (parsed), and so are no more useful
     *
     * @param nb
     *            number of byte used
     */
    private synchronized void readAdvance(int nb) {
        // change the index of the first byte not yet used
        position = position + nb;
        // change the number of byte stored (not used)
        nbByte = nbByte - nb;
    }

    /**
     * Tests whether the buffer contains a BIP message. If the first byte not
     * yet used (pointed by 'position') is the beggining of a message, the
     * header are extracted and copied in messageAttribute, 'position' is
     * changed and points on the first byte of the message body, and then the
     * methods returns 2.
     *
     * @return an integer to give the result of the methods
     *         <ul>
     *         <li> 0 : no message (need more byte) </li>
     *         <li> 1 : the header is not good, 'position' is not pointing on a
     *         message header </li>
     *         <li> 2 : a message has been found, 'position' is now pointing on
     *         the message body </li>
     *         </ul>
     */
    private synchronized int goodBeginning() {
        final int MIN_LENGTH_MESSAGE = MessageSocket.MIN_LENGTH_MESSAGE;
        if (nbByte >= MIN_LENGTH_MESSAGE) {
            int i = 0;
            boolean ok = true;
            while (ok && i < BipUtils.messageBegin.length) {
                ok = (buffer[position + i] == BipUtils.messageBegin[i]);
                i++;
            }
            if (ok) {
                messageAttribute[0] = new String(buffer, position + BipUtils.messageBegin.length, 8);
                messageAttribute[1] = new String(buffer, position + BipUtils.messageBegin.length + 9, 8);
                messageAttribute[2] = new String(buffer, position + BipUtils.messageBegin.length + 18, 8);

                int length = BipUtils.hexStringToInt(messageAttribute[2]);
                if (nbByte >= MIN_LENGTH_MESSAGE + length) {
                    readAdvance(BipUtils.messageBegin.length + 28);
                    return 2;
                } else if (MIN_LENGTH_MESSAGE + length > BUFFER_LENGTH) {
                    // System.out.println("new buffer");
                    BUFFER_LENGTH = MIN_LENGTH_MESSAGE + length + 10;
                    byte tmpBuffer[] = new byte[BUFFER_LENGTH];
                    for (int c = 0; c < nbByte; c++)
                        tmpBuffer[c] = buffer[position + c];
                    position = 0;
                    buffer = tmpBuffer;
                    return 0;
                } else {
                    // System.out.println("wait for more byte");
                    return 0;
                }
            } else
                return 1;
        } else {
            return 0;
        }
    }

    /**
     * Move the data in the buffer. The byte are moved from 'position' to 0
     */
    private synchronized void moveData() {
        for (int i = 0; i < nbByte; i++) {
            buffer[i] = buffer[i + position];
        }
        position = 0;
    }
}

/**
 * Manages the message emission and reception. A receiving thread is strarted
 * when this {@link MessageSocket} is started. When a complete message arrives,
 * the {@link BipMessageListener#receivedBipMessage(Message)} methods of the BIP
 * Message listeners are called. BIP message can be sent by using the method
 * Send. Base of TcpServer and TcpClient The byte reception, and detection of
 * message is managed by a ReceiveBuffer object.
 *
 * @author Sebastien Pesnel
 */
public abstract class MessageSocket {

    /**
     * The BIP peer id used in BIP exhanges: used to represent the local peer
     * when a message is send to the remote peer.
     */
    private int localPeerId;

    /** The id of the remote peer: received in the messages from the peer */
    private int remotePeerId;

    /**
     * The thread started to listen to incoming messages.
     */
    private Thread listeningThread;

    /** State of the connection */
    protected boolean connected;

    /** Message id: new for each message sent */
    protected int mid = 0;

    /**
     * The minimum size of a OMiSCID message (size of header + 2 byte for the
     * message end)
     */
    public static final int MIN_LENGTH_MESSAGE = BipUtils.messageBegin.length + 8 + 1 + 8 + 1 + 8 + 2 + BipUtils.messageEnd.length /*
                                                                                                                                     * message
                                                                                                                                     * end
                                                                                                                                     */;

    protected static final String messageBeginStr = BipUtils.byteArrayToString(BipUtils.messageBegin);

    protected static final String headerEndStr = BipUtils.byteArrayToString(BipUtils.headerEnd);

    /**
     * Indicates whether an empty message used for 'synchronization' has already
     * been exhanged
     */
    private boolean initMessageReceived = false;

    private boolean initMessageSent = false;

    synchronized boolean isInitMessageReceived() {
        return initMessageReceived;
    }

    /**
     * Signals to this object that an initialisation message has been received
     *
     * @param peerId
     *            the remote peer id of the connection who sends the empty
     *            messages
     */
    synchronized void initMessageReceived(int peerId) {
        initMessageReceived = true;
        remotePeerId = peerId;
        if (notifyListenersOnConnection) {
            synchronized (listenersSet) {
                for (BipMessageListener listener : listenersSet) {
                    listener.connected(remotePeerId);
                }
            }
        }
    }

    synchronized boolean isInitMessageSent() {
        return initMessageSent;
    }

    synchronized void setInitMessageSent() {
        this.initMessageSent = true;
    }

    /**
     * the object who manage the byte reception and the OMiSCID detection
     */
    protected ReceiveBuffer receiveBuffer = null;

    /** Set of listener to call when a BIP message is received */
    private Set<BipMessageListener> listenersSet;

    private boolean notifyListenersOnConnection;

    /**
     * Creates a new instance of MessageSocket using the specified BIP peer id
     * to represent the local peer.
     *
     * @param peerId
     *            identifier for the connection
     */
    public MessageSocket(int peerId) {
        this.localPeerId = peerId;
        connected = false;
        receiveBuffer = new ReceiveBuffer(this);
        listenersSet = new HashSet<BipMessageListener>();
    }

    /**
     * Generates an OMiSCID header Build the message with the BIP peer id, the
     * current message id and the length given as parameter. Increment the
     * message id
     *
     * @param len
     *            give the length that will appear in OMiSCID header
     */
    protected String generateHeader(int len) {
        String str = messageBeginStr + BipUtils.intTo8HexString(localPeerId) + " " + BipUtils.intTo8HexString(mid) + " " + BipUtils.intTo8HexString(len)
                + headerEndStr;
        mid++;
        return str;
    }

    /**
     * Same as {@link #generateHeader(int)} but returning a byte array
     */
    protected byte[] generateHeaderByte(int len) {
        return BipUtils.stringToByteArray(generateHeader(len));
    }

    /**
     * Adds a listener to call when a message is received
     *
     * @param listener
     *            the listener interested in the received message
     */
    public void addBipMessageListener(BipMessageListener listener) {
        synchronized (listenersSet) {
            listenersSet.add(listener);
        }
    }

    /**
     * Removes a listener for BIP messages
     *
     * @param listener
     *            the listener no more interested in the received message
     */
    public void removeBipMessageListener(BipMessageListener listener) {
        synchronized (listenersSet) {
            listenersSet.remove(listener);
        }
    }

    public void newMessageReceived(Message message) {
        synchronized (listenersSet) {
            for (BipMessageListener listener : listenersSet) {
                listener.receivedBipMessage(message);
            }
        }
    }

    private void run() {
        while (connected) {
            receive();
        }
        synchronized (listenersSet) {
            for (BipMessageListener listener : listenersSet) {
                listener.disconnected(remotePeerId);
            }
        }
    }

    /**
     * @return the value of connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Tests whether the peer id associated to the connection has a particular
     * value.
     *
     * @param peerId
     *            the peer id value to test
     * @return true if the peer id has a particular value
     */
    public boolean isConnectedToPeer(int peerId) {
        return this.remotePeerId == peerId;
    }

    /**
     * @return the BIP peer id of the remote peer
     */
    public int getRemotePeerId() {
        return remotePeerId;
    }

    /**
     * @return the BIP peer id of the local peer
     */
    int getLocalPeerId() {
        return localPeerId;
    }

    /**
     * Starts the thread dedicated to the processing of incoming data on the
     * socket. The started thread is automatically stopped on
     * {@link #closeConnection()} call.
     */
    public void start() {
        if (listeningThread == null) {
            listeningThread = new Thread(new Runnable() {
                public void run() {
                    MessageSocket.this.run();
                }
            });
            listeningThread.start();
        } else {
            System.err.println("Warning: MessageSocket start() called more than once");
        }
    }

    /**
     * Initializes the connection (protocol initialisation).
     */
    public synchronized void initializeConnection(boolean shouldNotifyListenersOnConnection) {
        if (!initMessageSent) {
            send((byte[]) null);
            initMessageSent = true;
            notifyListenersOnConnection = shouldNotifyListenersOnConnection;
        } else {
            System.err.println("Warning: in MessageSocket, multiple calls to initializeConnection");
        }
    }

    /**
     * Initializes the connection (protocol initialisation).
     */
    public synchronized void initializeConnection() {
        initializeConnection(false);
    }

    /**
     * Sends a String message with a BIP header. The string is
     * encoded using the BIP encoding. To check that the encoding process went
     * right, you must do it yourself using
     * {@link BipUtils#stringToByteArray(String)}.
     *
     * @param messageBody the message to send in a BIP message
     */
    public void send(String messageBody) {
        send(BipUtils.stringToByteArray(messageBody));
    }

    /**
     * Sends an XML DOM message to all still connected clients.
     *
     * @param message the XML message to send
     */
    public void send(Element message) {
        send(BipUtils.elementToByteArray(message));
    }

    /**
     * Sends an XML DOM message to all still connected clients.
     *
     * @param message the XML message to send
     */
    public void send(Document message) {
        send(message.getDocumentElement());
    }

    /**
     * Sends an array of byte in a BIP message.
     * Communication exceptions are caught by this method.
     *
     * You can use {@link #isConnected()} to check the status
     * of the connection or use {@link #sendExplicit(byte[])} to
     * send a message and be notified of errors.
     *
     * @param buffer array of byte to send
     */
    public void send(byte[] buffer) {
        try {
            sendExplicit(buffer);
        } catch (IOException e) {}
    }

    /**
     * Sends an array of byte in a BIP message possibly
     * throwing exceptions in case of communication error.
     *
     * @param buffer array of byte to send
     */
    public abstract void sendExplicit(byte[] buffer) throws IOException;

    /** Stores the bytes received on the connection */
    protected abstract void receive();

    /**
     * Closes the connection and stops the thread listening for incoming data.
     * Any concrete implementation of this method must set the connected
     * attribute to false.
     */
    public abstract void closeConnection();

    /** @return the port number for TCP */
    public abstract int getTcpPort();

    /** @return the port number for UDP */
    public abstract int getUdpPort();

}
