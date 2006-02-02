/*
 * MsgSocket.java
 */

package fr.prima.bipcom;


import fr.prima.bipcom.interf.BipMessageListener;
import fr.prima.bipcom.interf.Message;
import java.util.HashSet;
import java.util.Set;


/**
 * Manage the buffer to store bytes and parsed BIP messages The buffer can receive byte. The method 'received' give the number of byte added to the buffer. The method 'process' try to parse BIP message.
 * @author  Sebastien Pesnel  Refactoring by Patrick Reignier
 */
final class ReceiveBuffer {
    /**
     * The MsgSocket that work with this object. New message will be signal to
     * this object
     */
    private MsgSocket msgSocket;

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
    private String[] msgAttribut;

    /** Number of byte stored in the buffer */
    private int nbByte;

    /**
     * Create a new instance of ReceiveBuffer
     * 
     * @param msgSocket
     *            the MsgSocket object working with this ReceiveBuffer object.
     *            The BIP message will be returned to this object.
     */
    public ReceiveBuffer(MsgSocket msgSocket) {
        this.msgSocket = msgSocket;
        buffer = new byte[BUFFER_LENGTH];
        position = 0;
        msgAttribut = new String[3];
        nbByte = 0;
    }

    /**
     * give to the object the number of byte added to its buffer
     * 
     * @param nb
     *            number of byte newly added
     */
    public void received(int nb) {
        nbByte += nb;
    }

    /**
     * @return the offset where we can begin to use the buffer to store more
     *         byte
     */
    public int offset() {
        return position + nbByte;
    }

    /**
     * @return the number of byte again available in the buffer : where we can
     *         store new bytes
     */
    public int available() {
        return BUFFER_LENGTH - (position + nbByte);
    }

    /** try to parse message in the buffer */
    public void process() {
        // System.out.println("MsgSocket::Process");
        try {
            int res = goodBeginning();
            while (res != 0) {
                if (res == 2) { // a message beginning
                    int length = MsgSocket.hexStringToInt(msgAttribut[2]);
                    int dec = position + length;
                    // test the end of the message
                    if (buffer[dec] == '\r' && buffer[dec + 1] == '\n') {
                        // replace the message end by null character
                        // so with have one null character after the message
                        // body
                        buffer[dec] = 0;
                        buffer[dec + 1] = 0;

                        // new message
                        int pid = MsgSocket.hexStringToInt(msgAttribut[0]);

                        if (!msgSocket.getEmptyMsgReceived()) {
                                msgSocket.emptyMsgHasBeenReceived(pid);
                                msgSocket.send(null);
                            }
                        if(length != 0) {
                            // new message received (no empty)
                            int mid = MsgSocket.hexStringToInt(msgAttribut[1]);

                            msgSocket.newMessageReceived(new MessageImpl(buffer,
                                    position, length, mid, pid));
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
            System.out.println("ReceiveBuffer::Process");
            System.out.println(e);
        }
    }

    /**
     * Increment state variable about buffer management to indicate that byte of
     * the buffer have been used (parsed), and so are no more useful
     * 
     * @param nb
     *            number of byte used
     */
    private void readAdvance(int nb) {
        // change the index of the first byte not yet used
        position = position + nb;
        // change the number of byte stored (not used)
        nbByte = nbByte - nb;
    }

    /**
     * Test if the buffer contains a BIP message. If the first byte not yet used
     * (pointed by 'position') is the beggining of a message, the header are
     * extracted and copied in msgAttribut, 'position' is changed and points on
     * the first byte of the message body, and then the methods returns 2.
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
    private int goodBeginning() {
        int MIN_LENGTH_MSG = MsgSocket.MIN_LENGTH_MSG;
        if (nbByte >= MIN_LENGTH_MSG) {
            int i = 0;
            boolean ok = true;
            while (ok && i < MsgSocket.msgBegin.length) {
                ok = (buffer[position + i] == MsgSocket.msgBegin[i]);
                // System.out.println(buffer[position+i] +" -- "+
                // msgSocket.msgBegin[i]);
                i++;
            }
            if (ok) {
                msgAttribut[0] = new String(buffer, position
                        + MsgSocket.msgBegin.length, 8);
                msgAttribut[1] = new String(buffer, position
                        + MsgSocket.msgBegin.length + 9, 8);
                msgAttribut[2] = new String(buffer, position
                        + MsgSocket.msgBegin.length + 18, 8);

                // System.out.println("pid="+msgAttribut[0]+" mid="+
                // msgAttribut[1]+" len="+ msgAttribut[2]);
                int length = MsgSocket.hexStringToInt(msgAttribut[2]);
                if (nbByte >= MIN_LENGTH_MSG + length) {
                    readAdvance(MsgSocket.msgBegin.length + 28);
                    return 2;
                } else if (MIN_LENGTH_MSG + length > BUFFER_LENGTH) {
                    // System.out.println("new buffer");
                    BUFFER_LENGTH = MIN_LENGTH_MSG + length + 10;
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
    private void moveData() {
        for (int i = 0; i < nbByte; i++) {
            buffer[i] = buffer[i + position];
        }
        position = 0;
    }
}

/**
 * Manage the message reception in a thread, when a message arrived the method of the Bip Message listener is called. Bip message can be send by using the method Send. Base of TcpServer and TcpClient The byte reception, and detection of message is managed by a ReceiveBuffer object.
 * @author  Sebastien Pesnel
 */
public abstract class MsgSocket extends Thread implements ComTools {
    /** @ */
    /**
     * the id for this connection used in BIP exhange : used when a message is
     * send to peer
     */
    private int serviceId;

    /** the id of the peer : received in the messages from the peer */
    private int peerId;

    /** state of the connection */
    protected boolean connected;

    /** message id : new for each message sent */
    protected int mid = 0;

    /** characters found at the beginning of the message header */
    public static final byte[] msgBegin = { 'B', 'I', 'P', '/', '1', '.', '0',
            ' ' };

    /** string found at the beginning of the message header */
    private static final String msgBeginStr = new String(msgBegin);

    /** string found at the end of the message */
    public static final byte[] msgEnd = { '\r', '\n' };

    /**
     * the min size of a BIP message (size of header + 2 byte for the message
     * end)
     */
    public static final int MIN_LENGTH_MSG = msgBegin.length + 8 + 1 + 8 + 1
            + 8 + 2 + msgEnd.length /* message end */;

    /**
     * Indicates if an empty message used for 'synchronization' has already been
     * exhanged
     */
    private boolean emptyMsgReceived = false;

    /**
	 * Access to the value of emptyMsgReceived
	 * @return  the value of emptyMsgReceived
	 * @see  fr.prima.bipcom.MsgSocket#emptyMsgReceived
	 * @uml.property  name="emptyMsgReceived"
	 */
    public boolean getEmptyMsgReceived() {
        return emptyMsgReceived;
    }

    /**
     * Signal to this object that an empty message has been received
     * 
     * @param pid
     *            the peer id of the connection who sends the empty messages
     */
    void emptyMsgHasBeenReceived(int pid) {
        //System.out.println("emptyMsgHasBeenReceived " + this + " PID: " + pid);
        emptyMsgReceived = true;
        peerId = pid;
    }

    /**
     * the object who manage the byte reception and the BIP detection
     */
    protected ReceiveBuffer receiveBuffer = null;

    /** Set of listener to call when a BIP message is received */
    private Set<BipMessageListener> ListenerSet;

    /**
     * Creates a new instance of MsgSocket
     * 
     * @param serviceId
     *            identifier for the connection
     */
    public MsgSocket(int serviceId) {
        this.serviceId = serviceId;
        connected = false;
        receiveBuffer = new ReceiveBuffer(this);
        ListenerSet = new HashSet<BipMessageListener>();
    }

    /**
     * Generate a BIP header Build the message with the service id, the current
     * message id and the length given as parameter. Increment the message id
     * 
     * @param len
     *            give the length that will appear in BIP header
     */
    public String GenerateHeader(int len) {
        String str = msgBeginStr + intTo8HexString(serviceId) + " "
                + intTo8HexString(mid) + " " + intTo8HexString(len) + "\r\n";
        mid++;
        return str;
    }

    /**
     * Add a listener to call when a message is received
     * 
     * @param listener
     *            the listener interested in the received message
     */
    public void addBipMessageListener(BipMessageListener listener) {
        synchronized (ListenerSet) {
            ListenerSet.add(listener);
        }
    }

    /**
     * Remove a listener on BIP message
     * 
     * @param listener
     *            the listener no more interested in the received message
     */
    public void removeBipMessageListener(BipMessageListener listener) {
        synchronized (ListenerSet) {
            ListenerSet.remove(listener);
        }
    }

    /**
     * Called when a new message is received. This methods call the listener on
     * the message reception.
     * 
     * @param msg
     *            the received message
     */
    public void newMessageReceived(Message msg) {
        // System.out.println("NewMessageReceived :");
        // System.out.println(msg);
        synchronized (ListenerSet) {
            java.util.Iterator<BipMessageListener> it = ListenerSet.iterator();
            while (it.hasNext()) {
                BipMessageListener listener = (BipMessageListener) it.next();
                listener.receivedBipMessage(msg);
            }
        }
    }

    /**
     * Method executed in a thread While the connection exists, the bytes are
     * received
     */
    public void run() {
        while (connected) {
            receive();
        }
    }

    /**
	 * @return  the value of connected
	 * @uml.property  name="connected"
	 */
    public boolean isConnected() {
        return connected;
    }

    /**
	 * @return  the value of the service id
	 * @uml.property  name="serviceId"
	 */
    public int getServiceId() {
        return serviceId;
    }

    /**
     * Test if the peer id associated to the connection has a particular value.
     * 
     * @param peerId
     *            the peer id value to test
     * @return true if the peer id has a particular value
     */
    public boolean isConnectedToPeer(int peerId) {
        return peerId == peerId;
    }

    /**
	 * @return  the value of the peer id
	 * @uml.property  name="peerId"
	 */
    public int getPeerId() {
        return peerId;
    }

    /**
     * Create a string with an hexadecimal representation of an integer. The
     * representation has 8 characters completed by 0.
     * 
     * @param i
     *            the value to change into string
     */
    public static String intTo8HexString(int i) {
        String str = Integer.toHexString(i);
        while (str.length() < 8) {
            str = "0" + str;
        }
        // System.out.println("IntTo8HexString: "+str);
        return str;
    }

    /**
     * Change a string with an hexadecimal representation of an integer into the
     * integer value
     * 
     * @param str
     *            the string to transform
     * @return the integer value associated to the string
     */
    static public int hexStringToInt(String str) {
        int nb = str.length();
        byte cstr[] = str.getBytes();
        int i = 0;
        int res = 0;

        while (i < nb) {
            res = res * 16;
            // System.out.println(cstr[i]);
            if (cstr[i] <= 57)
                res += (cstr[i] - 48);
            else
                res += (cstr[i] - 97) + 10;
            i++;
        }
        // System.out.println("str:["+str+"] ("+cstr[0]+"):"+res);
        return res;
    }

    /**
     * Send an array of byte in a BIP message
     * 
     * @param buffer
     *            array of byte to send
     */
    public abstract void send(byte[] buffer);

    /** Store the byte received on the connection */
    protected abstract void receive();

    /** Close the connection */
    public abstract void closeConnection();

    /** @return the port number for TCP */
    public abstract int getTcpPort();

    /** @return the port number for UDP */
    public abstract int getUdpPort();
}
