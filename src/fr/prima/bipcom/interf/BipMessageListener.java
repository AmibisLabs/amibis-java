/*
 * BipMessageListener.java
 *
 * Created on 3 avril 2005, 14:33
 */

package fr.prima.bipcom.interf;



/**
 * Listener for BIP message
 * 
 * Interface to implement to receive BIP message. The object that implements
 * this interface can be given to object as TcpClient, TcpServer to receive and
 * process message.
 * 
 *
 * @author Sebastien Pesnel
 * Refactoring by Patrick Reignier
 */
public interface BipMessageListener extends java.util.EventListener {
    /**
     * Call on receive message when the object is declared as listener to the
     * source of message
     * 
     * @param msg
     *            the BIP message
     */
    public void receivedBipMessage(Message msg);
}
