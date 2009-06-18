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
