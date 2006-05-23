
package fr.prima.omiscid.com.interf;

import java.util.EventListener;

/**
 * Defines the listener interface for Basic Interconnection Protocol (BIP)
 * messages. This interface must be implemented in order to receive
 * notifications on BIP messages receptions.
 *
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
public interface BipMessageListener extends EventListener {
    /**
     * Processes a received BIP message. As a given message could be processed
     * by several others listeners, the message must not be modified by its
     * processing.
     *
     * @param message
     *            the BIP message to process
     */
    public void receivedBipMessage(Message message);
}
