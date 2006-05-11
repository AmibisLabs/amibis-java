/*
 * Message.java
 *
 */

package fr.prima.omiscid.com;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import fr.prima.omiscid.com.interf.Message;

/**
 * Contains the data about a BIP Message.
 * 
 * @see Message The content of the message are basically stored in a byte array.
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
public class MessageImpl implements Message {
    /** buffer of byte to store the message body */
    private byte[] buffer;

    /** BIP peer id of the source peer (source of this message) */
    private int peerId;

    /** message id */
    private int messageId;

    public MessageImpl() {
    }

    /**
     * Create a new instance with the data in parameter.
     * 
     * @param source
     *            a buffer that contains the bytes of the message
     * @param offset
     *            index where begin to read the buffer (source)
     * @param length
     *            number of byte to copy from source
     * @param messageId
     *            the message id
     * @param pId
     *            the BIP peer id of the source peer: source of the message
     */
    public MessageImpl(byte[] source, int offset, int length, int messageId, int peerId) {
        this.messageId = messageId;
        this.peerId = peerId;
        buffer = new byte[length];
        for (int i = 0; i < length; ++i) {
            buffer[i] = source[i + offset];
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.prima.omiscid.com.Message#getBufferAsString()
     */
    public String getBufferAsString() throws BipMessageInterpretationException {
        String res = BipUtils.byteArrayToString(buffer);
        if (res == null) {
            throw new BipMessageInterpretationException(null);
        } else {
            return res;
        }
    }

    public String getBufferAsStringUnchecked() {
        try {
            return getBufferAsString();
        } catch (BipMessageInterpretationException e) {
            return null;
        }
    }

    public Element getBufferAsXML() throws BipMessageInterpretationException {
        try {
            return BipUtils.byteArrayToDomElement(buffer);
        } catch (SAXException e) {
            throw new BipMessageInterpretationException(e);
        }
    }

    public Element getBufferAsXMLUnchecked() {
        try {
            return getBufferAsXML();
        } catch (BipMessageInterpretationException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.prima.omiscid.com.Message#getBuffer()
     */
    /**
     * @return the raw data buffer
     */
    public byte[] getBuffer() {
        return buffer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.prima.omiscid.com.Message#getPeerId()
     */
    public int getPeerId() {
        return peerId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.prima.omiscid.com.Message#getMessageId()
     */
    public int getMessageId() {
        return messageId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.prima.omiscid.com.Message#toString()
     */
    public String toString() {
        return "Message " + peerId + " " + messageId + " " + buffer.length;
    }

}
