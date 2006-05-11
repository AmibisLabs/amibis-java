/*
 * Created on Feb 13, 2006
 *
 */
package fr.prima.omiscid.dnssd.interf;

/**
 * ServiceBrowser allows browsing for services registered under dnssd with a
 * given dnssd registration type.
 * 
 * @author emonet
 */
public interface ServiceBrowser {

    /**
     * Adds a listener to this browser. When a service event occurs, all
     * listeners are notified.
     * 
     * @param l
     *            listener interested in service event
     */
    void addListener(ServiceEventListener l);

    /**
     * Removes the given listener.
     * 
     * @param l
     *            listener no more interested in service event
     */
    void removeListener(ServiceEventListener l);

    /**
     * Starts the service browser. All the listeners will first be notified of
     * the declared services and then will start receiving service event
     * associated to registration/unregistration of services.
     */
    void start();

    /**
     * Stops the service browse ; listeners won't be notified any more (but are
     * still listeners).
     */
    void stop();
}
