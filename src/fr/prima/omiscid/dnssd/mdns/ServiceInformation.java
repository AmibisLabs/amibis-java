/*
 * Created on Feb 14, 2006
 *
 */
package fr.prima.omiscid.dnssd.mdns;

import java.util.Vector;

import com.apple.dnssd.TXTRecord;

/**
 * 
 * @author emonet initial build from Service by pesnel
 *
 */
public class ServiceInformation
implements fr.prima.omiscid.dnssd.interf.ServiceInformation {

    /** name of the service */
    public String fullName = null;

    /** host name where the service is launched */
    public String hostName = null;

    /** port where the control server listens */
    public int port;

    /** records registers with the service */
    public TXTRecord txtRecord = null;

    /** register type */
    public String registrationType = null;

    /** domain name */
    public String domain = null;

    
    /*package*/ ServiceInformation(String registrationType, String serviceName) {
        this.fullName = serviceName;
        this.registrationType = registrationType;
    }

    /*package*/ ServiceInformation(String registrationType, String fullName, String hostName, int port, TXTRecord txtRecord) {
        this.fullName = fullName;
        this.registrationType = registrationType;
        this.hostName = hostName;
        this.port = port;
        this.txtRecord = txtRecord;
    }

    public String getFullName() {
        return fullName;
    }

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

    public String getRegType() {
        return registrationType;
    }

    public byte[] getProperty(String key) {
        return txtRecord.getValue(key);
    }

    public String getStringProperty(String key) {
        return new String(getProperty(key));
    }

    public Iterable<String> getPropertyKeys() {
        Vector<String> res = new Vector<String>();
        for (int i = 0; i < txtRecord.size(); i++) {
            res.add(txtRecord.getKey(i));
        }
        return res;
    }

}
