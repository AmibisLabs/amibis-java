package fr.prima.omiscid.control ;

/**
 * Change the OMiSCID message into XML tree. Store the received messages. The
 * message are processed by the method processMessage.
 * 
 * @author Sebastien Pesnel
 * Refactoring by Patrick Reignier
 */
public class XmlMsgManager implements fr.prima.omiscid.com.interf.OmiscidMessageListener {
    /** list of message */
    private java.util.LinkedList<XmlMessage> msgSet = new java.util.LinkedList<XmlMessage>();

    public void receivedOmiscidMessage(fr.prima.omiscid.com.interf.Message msg) {
        // System.out.println("Msg: |" + msg.getBufferAsString() +"|");
        synchronized (msgSet) {
            XmlMessage xmlMsg = XmlMessage.changeMessageToXmlTree(msg);
            if (xmlMsg != null) {
                msgSet.addLast(xmlMsg);
                msgSet.notifyAll();
            }
        }
    }

    /**
     * Exist stored message
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
     * Extract and Return the older message
     * 
     * @return the older message in the list, null if the list is empty
     */
    public XmlMessage getMessage() {
        try {
            synchronized (msgSet) {
                XmlMessage msg = (XmlMessage) msgSet.removeFirst();
                return msg;
            }
        } catch (java.util.NoSuchElementException e) {
            return null;
        }
    }

    /**
     * Process the stored message. Call on each message the method
     * ProcessAMessage
     * 
     * @return the number of message processed
     */
    public int processMessages() {
        // System.out.println("processMessages");
        int nb = 0;
        synchronized (msgSet) {
            while (msgSet.size() != 0) {
                XmlMessage msg = (XmlMessage) msgSet.removeFirst();
                processAMessage(msg);
                nb++;
            }
        }
        return nb;
    }

    /**
     * Wait for message to be stored in the list
     * 
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
    protected void processAMessage(XmlMessage msg) {
        System.out.println(msg);
    }
}
