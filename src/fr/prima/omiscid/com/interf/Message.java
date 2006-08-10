package fr.prima.omiscid.com.interf;

import org.w3c.dom.Element;

import fr.prima.omiscid.com.BipMessageInterpretationException;

/**
 * Represents a message sent using the Basic Interconnection Protocol (BIP). It
 * is basically composed of some message content, the id of the peer that sent
 * this message and the id of the message itself.
 */
public interface Message {

    /**
     * Accesses the data buffer interpreted as a string.
     *
     * @return a string built on the data buffer
     * @throws BipMessageInterpretationException
     */
    String getBufferAsString() throws BipMessageInterpretationException;

    /**
     * Same as {@link #getBufferAsString()} but silently catches exceptions.
     *
     * @return null or what {@link #getBufferAsString()} would return
     */
    String getBufferAsStringUnchecked();

    /**
     * Accesses the data buffer interpreted as an xml document.
     *
     * @return a DOM element built from the data buffer
     * @throws BipMessageInterpretationException
     */
    Element getBufferAsXML() throws BipMessageInterpretationException;

    /**
     * Same as {@link #getBufferAsXML()} but silently catches exceptions.
     *
     * @return null or what {@link #getBufferAsXML()} would return
     */
    Element getBufferAsXMLUnchecked();

    /**
     * Accesses the raw data buffer. The implementation is free to return an
     * internal buffer to improve performance. The returned buffer is thus not
     * intended to be kept over the lifetime of the Message instance. For
     * example, if you get are in a {@link BipMessageListener} and want to keep
     * the content of the message buffer for further use, you must make a copy
     * of it.
     *
     * @return the data buffer
     */
    byte[] getBuffer();

    /**
     * Accesses the id of the source of this messages.
     *
     * @return an integer to identify the source of this message
     */
    int getPeerId();

    /**
     * Accesses the message id.
     *
     * @return the message number
     */
    int getMessageId();

    /**
     * Builds a string with pid, mid and message length (mainly use for test)
     *
     * @return "Message from "+pid+" "+mid+" "+buffer.length
     */
    String toString();

}
