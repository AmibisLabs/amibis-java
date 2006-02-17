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
public interface ServiceInformation {

    //String getDomain();

    String getFullName();

    String getHostName();

    int getPort();

    String getRegType();

    byte[] getProperty(String key);

    String getStringProperty(String key);

    Iterable<String> getPropertyKeys();

}
