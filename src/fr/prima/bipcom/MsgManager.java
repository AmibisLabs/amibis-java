package fr.prima.bipcom;


import java.util.LinkedList;
import java.util.NoSuchElementException;

import fr.prima.bipcom.interf.BipMessageListener;
import fr.prima.bipcom.interf.Message;


/**
 * Store the received messages. (FIFO list) The message are processed by the
 * method processMessage who calls a particular method processAMessage on each
 * message. This methods can be rewritten to define the appropriate processing.
 * 
 * @author Sebastien Pesnel
 * Refactoring by Patrick Reignier
 */
public class MsgManager implements BipMessageListener {
    /** List the message received */
    private LinkedList<Message> msgSet = new LinkedList<Message>();

    /**
     * Method calls on the received messages
     * 
     * @param msg
     *            a BIP message newly received
     */
    public void receivedBipMessage(Message msg) {
        synchronized (msgSet) {
            msgSet.addLast(msg);
            msgSet.notifyAll();
        }
    }

    /**
     * Test if stored messages exist
     * 
     * @return if there is stored message
     */
    public boolean hasMessage() {
        synchronized (msgSet) {
            return !(msgSet.isEmpty());
        }
    }

    /**
     * Number of stored message
     * 
     * @return the number of message
     */
    public int getNbMessage() {
        synchronized (msgSet) {
            return msgSet.size();
        }
    }

    /**
     * @return the older message in the list, null if the list is empty
     */
    public Message getMessage() {
        try {
            synchronized (msgSet) {
                Message msg = (Message) msgSet.removeFirst();
                return msg;
            }
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /**
     * Process the stored message. Call on each message the methods
     * ProcessAMessage
     * 
     * @return the number of message processed
     */
    public int processMessages() {
        synchronized (msgSet) {
            int nb = 0;
            while (msgSet.size() != 0) {
                Message msg = (Message) msgSet.removeFirst();
                processAMessage(msg);
                nb++;
            }
            return nb;
        }
    }

    /**
     * Wait for message to be stored in the list
     */
    public boolean waitForMessage() {
        synchronized (msgSet) {
            if (hasMessage())
                return true;
            try {
                msgSet.wait();
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Process a message
     * 
     * @param msg
     *            the message to process
     */
    protected void processAMessage(Message msg) {
        System.out.println(msg);
    }
}
