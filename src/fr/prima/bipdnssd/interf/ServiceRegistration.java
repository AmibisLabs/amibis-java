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
public interface ServiceRegistration {
    
    void addProperty(String name, String value);
    void setName(String serviceName);
    String getName();

    boolean register(int port);
    boolean isRegistered();
    void unregister();
    String getRegisteredName();
}
