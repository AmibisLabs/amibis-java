/*
 * Created on Feb 13, 2006
 *
 */
package fr.prima.bipdnssd.jmdns;

import java.util.Enumeration;
import java.util.Vector;

import javax.jmdns.ServiceInfo;

public class ServiceInformation
implements fr.prima.bipdnssd.interf.ServiceInformation {
    
    private ServiceInfo serviceInfo;
    
    /*package*/ ServiceInformation(String type, String name) {
        serviceInfo = new ServiceInfo(type,name,0, 0, 0,ServiceInfo.NO_VALUE);
    }

    /*package*/ ServiceInformation(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

//    /*package*/ ServiceInformation(String type, String name, int port, String text) {
//        serviceInfo = new ServiceInfo(type,name,port,text);
//    }
//
//    /*package*/ ServiceInformation(String type, String name, int port, int weight, int priority, byte[] text) {
//        serviceInfo = new ServiceInfo(type,name,port,weight,priority,text);
//    }

    public int getPort() {
        return serviceInfo.getPort();
    }

    public String getFullName() {
        return serviceInfo.getName();
    }

    public String getHostName() {
        return serviceInfo.getServer();
    }

    public String getRegType() {
        return serviceInfo.getType();
    }

    public byte[] getProperty(String key) {
        return serviceInfo.getPropertyBytes(key);
    }

    public String getStringProperty(String key) {
        return serviceInfo.getPropertyString(key);
    }

    public Iterable<String> getPropertyKeys() {
        Vector<String> res = new Vector<String>();
        Enumeration enumeration = serviceInfo.getPropertyNames();
        while (enumeration.hasMoreElements()) {
            res.add((String) enumeration.nextElement());
        }
        return res;
    }

}
