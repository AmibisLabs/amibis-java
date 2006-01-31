/*
 * Message.java
 *
 */

package fr.prima.bipcom;

import fr.prima.bipcom.interf.Message;


/**
 * Contains the data about a BIP Message. It contains a buffer of byte that
 * contains the body message, the service id that is the source of the message,
 * an the id message.
 * 
 * @author Sebastien Pesnel
 * Refactoring by Patrick Reignier
 */
public class MessageImpl implements Message {
    /** buffer of byte to store the message body */
    byte[] buffer;

    /** id of the service (source of this message) */
    int pid;

    /** message id */
    int mid;

    /** Create a new instance of Message */
    public MessageImpl() {
    }

    /**
     * Create a new instance with the data in parameter
     * 
     * @param source
     *            a buffer that contains the bytes of the message
     * @param offset
     *            index where begin to read the buffer (source)
     * @param length
     *            number of byte to copy from source
     * @param msgId
     *            the message id
     * @param pId
     *            the id of the service : source of the message
     */
    public MessageImpl(byte[] source, int offset, int length, int msgId, int pid) {
        mid = msgId;
        pid = pid;
        buffer = new byte[length];
        for (int i = 0; i < length; ++i) {
            buffer[i] = source[i + offset];
        }
    }

    /* (non-Javadoc)
	 * @see fr.prima.bipcom.Message#getBufferAsString()
	 */
    public String getBufferAsString() {
        return new String(buffer);
    }

    /* (non-Javadoc)
	 * @see fr.prima.bipcom.Message#getBuffer()
	 */
    public byte[] getBuffer() {
        return buffer;
    }

    /* (non-Javadoc)
	 * @see fr.prima.bipcom.Message#getPeerId()
	 */
    public int getPeerId() {
        return pid;
    }

    /* (non-Javadoc)
	 * @see fr.prima.bipcom.Message#getMsgId()
	 */
    public int getMsgId() {
        return mid;
    }

    /* (non-Javadoc)
	 * @see fr.prima.bipcom.Message#toString()
	 */
    public String toString() {
        return "Msg " + pid + " " + mid + " " + buffer.length;
    }

}
