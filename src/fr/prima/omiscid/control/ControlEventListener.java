package fr.prima.omiscid.control ;

/**
 * Listener for control event
 * 
 * Interface to implement to receive control event. The control event are sent
 * by the control server to some connected control client. The object that
 * implements this interface can be given to ControlClient object in order to
 * receive and process message.
 * 
 * @author Sebastien Pesnel
 * Refactoring by Patrick Reignier
 */
public interface ControlEventListener extends java.util.EventListener {
    /**
     * call on control event
     * 
     * @param xmlMsg
     *            a BIP message change in XML tree that contains the control
     *            event
     */
    public void receivedControlEvent(XmlMessage xmlMsg);
}
