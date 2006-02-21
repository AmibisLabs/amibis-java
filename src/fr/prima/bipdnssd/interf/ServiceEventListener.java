package fr.prima.bipdnssd.interf ;

import java.util.EventListener;



/**
 * Listener interface for service events.
 * 
 * @see ServiceEvent
 * 
 * @author Sebastien Pesnel refactoring emonet
 */
public interface ServiceEventListener extends EventListener {

    /** 
     * Listener method called when a service event is send
     * (a new service is found or an existing is lost).
     * 
     * @param e the data about the event
     */
    public void serviceEventReceived(ServiceEvent e);

}
