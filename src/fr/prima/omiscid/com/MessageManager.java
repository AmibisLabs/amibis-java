package fr.prima.omiscid.com;

import java.util.LinkedList;
import java.util.Queue;

import fr.prima.omiscid.com.interf.Message;
import fr.prima.omiscid.com.interf.BipMessageListener;

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
    private Queue<Message> messagesQueue = new LinkedList<Message>();

    /*
     * (non-Javadoc)
     * 
     * @see fr.prima.omiscid.com.interf.OmiscidMessageListener#receivedOmiscidMessage(fr.prima.omiscid.com.interf.Message)
     */
    public void receivedBipMessage(Message message) {
        synchronized (messagesQueue) {
            messagesQueue.add(message);
            messagesQueue.notifyAll();
        }
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
    public boolean waitForMessages() {
        synchronized (messagesQueue) {
            if (hasMessage()) {
                return true;
            }
            try {
                messagesQueue.wait();
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
