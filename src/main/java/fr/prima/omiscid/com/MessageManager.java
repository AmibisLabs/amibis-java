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

import java.util.LinkedList;
import java.util.Queue;

import fr.prima.omiscid.com.interf.BipMessageListener;
import fr.prima.omiscid.user.connector.Message;

/**
 * Stores the received messages in a FIFO list. The messages are processed by
 * the method processMessage who calls a particular method processAMessage on
 * each message. This methods can be overidden to define the appropriate
 * processing.
 *
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
public abstract class MessageManager implements BipMessageListener {
    /** List of the messages received */
    private final Queue<Message> messagesQueue = new LinkedList<Message>();

    /*
     * (non-Javadoc)
     *
     * @see fr.prima.omiscid.com.interf.BipMessageListener#receivedBipMessage(fr.prima.omiscid.com.interf.Message)
     */
    public void receivedBipMessage(Message message) {
        synchronized (messagesQueue) {
            messagesQueue.add(message);
            messagesQueue.notifyAll();
        }
    }

    public void disconnected(int peerId) {

    }

    public void connected(int remotePeerId) {
    }

    /**
     * Tests whether there are available messages to process.
     *
     * @return whether there is at least one available message
     */
    public boolean hasMessage() {
        synchronized (messagesQueue) {
            return !(messagesQueue.isEmpty());
        }
    }

    /**
     * Number of stored messages.
     *
     * @return the number of message
     */
    public int getNbMessage() {
        synchronized (messagesQueue) {
            return messagesQueue.size();
        }
    }

    /**
     * Processes the stored message. Calls on each message the method
     * ProcessAMessage
     *
     * @return the number of message processed
     */
    public final int processMessages() {
        synchronized (messagesQueue) {
            int count = 0;
            while (messagesQueue.size() != 0) {
                count++;
                processMessage(messagesQueue.poll());
            }
            return count;
        }
    }

    /**
     * Waits for the arrival of new message(s). There is no guaranty that
     * hasMessage() will be true after waitForMessage() returns (whatever the
     * return value).
     *
     * @return an indicative boolean, the value return by hasMessage() after the
     *         waiting time
     */
    public boolean waitForMessages(long timeout) {
        synchronized (messagesQueue) {
            if (hasMessage()) {
                return true;
            }
            try {
                messagesQueue.wait(timeout);
                return hasMessage();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Processes a message. This method must be implemented to process the
     * received messages.
     *
     * @param message
     *            the message to process
     */
    protected abstract void processMessage(Message message);
}
