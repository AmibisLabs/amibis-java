/*
 * Created on Feb 13, 2006
 *
 */
package fr.prima.bipdnssd.interf;


/**
 * 
 * @author emonet
 *
 */
public interface ServiceBrowser {
    
    /**
     * @param l
     *            listener interested in service event
     */
    void addListener(ServiceEventListener l);

    /**
     * @param l
     *            listener no more interested in service event
     */
    void removeListener(ServiceEventListener l);
    
    /**
     * Start to receive information about the available services, and to
     * generate service events
     */
    void start();

    /**
     * Stop receiving information about the available services, and 
     * generating service events
     */
    void stop();
}
