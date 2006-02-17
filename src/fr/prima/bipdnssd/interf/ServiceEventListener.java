package fr.prima.bipdnssd.interf ;



/**
 * Listener interface for service events.
 * 
 * @see ServiceEvent
 * @see BrowseForService
 * 
 * @author Sebastien Pesnel
 */
public interface ServiceEventListener extends java.util.EventListener {

    /** Call when a service event occured : 
     * a service is found or lost.
     * @param e the data about the event
     */
    public void serviceEventReceived(ServiceEvent e);

}
