package fr.prima.omiscid.dnssd.interf ;



/**
 * Event generated by service browser. Signal the service found, and lost.
 * @author  Sebastien Pesnel refactoring emonet
 */
public class ServiceEvent {
    /** the value for event when a service is lost */
    static public final int LOST = 0;
    /** the value for event when a service is found */
    static public final int FOUND = 1;
    
    /** the service with more or less data available 
     * according that the service is found or lost.
     * if lost, only the service name is available, 
     * else all the data are available : host, port, text records 
     */
    private ServiceInformation serviceInformation;
    
    /** the event : service lost or found.*/
    private int event;
    
    /** Contructs a new intance of ServiceEvent 
     * @param s the service
     * @param e the kind of event : LOST or FOUND
     */
    public ServiceEvent(ServiceInformation s, int e) {
        serviceInformation = s;
        event = e;
    }
    
    /**
	 * Access to the service information associated with this service event
	 * @return  the Service object
	 * @uml.property  name="service"
	 */
    public ServiceInformation getServiceInformation() {
        return serviceInformation;
    }
    
    /** Access to the kind of event 
     * @return whether this service event notifies that a service is lost */
    public boolean isLost() {
        return event == LOST;
    }
    
    /** Access to the kind of event 
     * @return whether this service event notifies that a service is found */
    public boolean isFound() {
        return event == FOUND;
    }
}