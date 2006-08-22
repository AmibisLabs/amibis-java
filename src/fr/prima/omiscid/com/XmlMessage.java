package fr.prima.omiscid.com;

import org.w3c.dom.Element;

import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.exception.MessageInterpretationException;

/**
 * Encapsulates a BIP message interpreted as XML DOM document. Stores the
 * processed tree to cache it. Emits warning messages on accesses to byte and
 * string representations of the message.
 * 
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
public class XmlMessage implements Message {

    private Element xmlRepresentation = null;

    private Message delegateMessage;

    public XmlMessage(Message message) throws MessageInterpretationException {
        this.delegateMessage = message;
        this.xmlRepresentation = message.getBufferAsXML();
    }

    public byte[] getBuffer() {
        System.err.println("Warning: in XmlMessage, getBuffer shouldn't be used");
        return delegateMessage.getBuffer();
    }

    public String getBufferAsString() throws MessageInterpretationException {
        System.err.println("Warning: in XmlMessage, getBufferAsString shouldn't be used");
        return delegateMessage.getBufferAsString();
    }

    public String getBufferAsStringUnchecked() {
        System.err.println("Warning: in XmlMessage, getBufferAsStringUnchecked shouldn't be used");
        return delegateMessage.getBufferAsStringUnchecked();
    }

    public Element getBufferAsXML() throws MessageInterpretationException {
        return xmlRepresentation;
    }

    public Element getBufferAsXMLUnchecked() {
        return xmlRepresentation;
    }

    public int getMessageId() {
        return delegateMessage.getMessageId();
    }

    public String toString() {
        return delegateMessage.toString();
    }

    public int getPeerId() {
        return delegateMessage.getPeerId();
    }

    public Element getRootElement() {
        return this.getBufferAsXMLUnchecked();
    }

    /**
     * Same as {@link #XmlMessage(Message)} but silently catches the exceptions.
     * 
     * @param message
     *            to use as delegate
     * @return null or a new instance of {@link XmlMessage}
     */
    public static XmlMessage newUnchecked(Message message) {
        try {
            return new XmlMessage(message);
        } catch (MessageInterpretationException e) {
            return null;
        }
    }

}
