package fr.prima.omiscid.control;

import java.util.EventListener;

import fr.prima.omiscid.com.XmlMessage;

/**
 * Listener for control events. Interface to implement to receive control
 * events. The control events are sent by the control server to some connected
 * control client. The object that implements this interface can be given to
 * {@link ControlClient} instance in order to receive and process messages.
 * 
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
public interface ControlEventListener extends EventListener {
    /**
     * Processes the given control event message.
     * 
     * @param xmlMessage
     *            an BIP message converted in an XML tree containing the control
     *            event
     */
    public void receivedControlEvent(XmlMessage xmlMessage);
}
