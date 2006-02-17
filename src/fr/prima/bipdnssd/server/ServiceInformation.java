/*
 * Created on Feb 15, 2006
 *
 */
package fr.prima.bipdnssd.server;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Hashtable;

/**
 * This class is a pure copy of the corresponding class in the BipDnssdServer project.
 * @author emonet
 *
 */
public class ServiceInformation implements Externalizable {

    
    private static final long serialVersionUID = -7947660767854019970L;
    
    private String registrationType = null;
    private String fullName = null;
    private String hostName = null;
    private int port;
    private Hashtable<String, byte[]> properties = null;
    private int status;
    public final static int statusConnecting = 1000;
    public final static int statusDisconnecting = 1001;
    public final static int statusRegistering = 2000; 
    public final static int statusUnregistering = 2001;
    public final static int statusNotifyingRegistered = 1002;
    
    public ServiceInformation() {
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(registrationType);
        out.writeObject(fullName);
        out.writeObject(hostName);
        out.writeInt(port);
        out.writeObject(properties);
        out.writeInt(status);
    }
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        registrationType = (String) in.readObject();
        fullName = (String) in.readObject();
        hostName = (String) in.readObject();
        port = in.readInt();
        properties = (Hashtable<String, byte[]>) in.readObject();
        status = in.readInt();
    }
    public void setNotifying() {
        assert status == statusRegistering || status == statusUnregistering;
        status = statusNotifyingRegistered;
    }
    public String getQualifiedName() {
        return registrationType+fullName;        
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
    public String getRegistrationType() {
        return registrationType;
    }
    public String getRegType() {
        return registrationType;
    }
    public byte[] getProperty(String key) {
        return properties.get(key);
    }
    public String getStringProperty(String key) {
        return new String(getProperty(key));
    }
    public Iterable<String> getPropertyKeys() {
        return properties.keySet();
    }
    public ServiceInformation(String registrationType, String fullName, String hostName, int port, Hashtable<String, byte[]> properties, int status) {
        this.registrationType = registrationType;
        this.fullName = fullName;
        this.hostName = hostName;
        this.port = port;
        this.properties = properties;
        this.status = status;
    }
    public ServiceInformation(String registrationType, String fullName, int status) {
        this.registrationType = registrationType;
        this.fullName = fullName;
        this.hostName = null;
        this.port = 0;
        this.properties = null;
        this.status = status;
    }

    public boolean isRegistering() {
        return status == statusRegistering;
    }

    public boolean isUnregistering() {
        return status == statusUnregistering;
    }

    public boolean isConnecting() {
        return status == statusConnecting;
    }

    public boolean isDisconnecting() {
        return status == statusDisconnecting;
    }

    public boolean isNotifying() {
        return status == statusNotifyingRegistered;
    }

    public int getStatus() {
        return status;
    }

}
