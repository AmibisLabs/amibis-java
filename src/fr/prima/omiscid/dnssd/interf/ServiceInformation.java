/*
 * Created on Feb 13, 2006
 *
 */
package fr.prima.omiscid.dnssd.interf;

/**
 * 
 * Informations describing a service under dnssd.
 * 
 * @author emonet
 *
 */
public interface ServiceInformation {

    String getFullName();

    String getHostName();

    int getPort();

    String getRegType();

    byte[] getProperty(String key);

    String getStringProperty(String key);

    Iterable<String> getPropertyKeys();

}
