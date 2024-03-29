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

package fr.prima.omiscid.user.connector;

import org.w3c.dom.Element;

import fr.prima.omiscid.user.exception.MessageInterpretationException;

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
     * @throws MessageInterpretationException
     */
    String getBufferAsString() throws MessageInterpretationException;

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
     * @throws MessageInterpretationException
     */
    Element getBufferAsXML() throws MessageInterpretationException;

    /**
     * Same as {@link #getBufferAsXML()} but silently catches exceptions.
     *
     * @return null or what {@link #getBufferAsXML()} would return
     */
    Element getBufferAsXMLUnchecked();

    /**
     * Accesses the raw data buffer. The implementation is free to return an
     * internal buffer to improve performance. The returned buffer is thus not
     * intended to be kept over the lifetime of the Message instance nor modified.
     * For example, if you get are in a {@link ConnectorListener} and want to keep
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
