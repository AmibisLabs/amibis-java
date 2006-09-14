package fr.prima.omiscid.dnssd.avahi;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import com.apple.dnssd.DNSSD;

import fr.prima.omiscid.dnssd.client.ServiceRegistrator;
import fr.prima.omiscid.dnssd.interf.ServiceRegistration.ServiceNameProducer;

public class ServiceRegistration implements fr.prima.omiscid.dnssd.interf.ServiceRegistration {

    private AvahiConnection avahiConnection;

    private boolean registered;

    private String registrationType;

    private String serviceName;

    private String registeredServiceName;

    private Hashtable<String, byte[]> properties = new Hashtable<String, byte[]>();

    private int port;

    /* package */ServiceRegistration(AvahiConnection avahiConnection, String serviceName, String registrationType) {
        this.avahiConnection = avahiConnection;
        this.registrationType = registrationType;
        this.serviceName = serviceName;
    }

    public void addProperty(String name, String value) {
        try {
            properties.put(name, value.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getName() {
        return serviceName;
    }

    public boolean register(int port) {
        this.port = port;
        return avahiConnection.register(this);
    }

    public boolean isRegistered() {
        return registered;
    }

    public void unregister() {
        avahiConnection.unregister(this);
    }

    public String getRegisteredName() {
        // TODO is it ok?
        return registeredServiceName;
    }

    /* package */void setRegisteredServiceName(String registeredServiceName) {
        this.registeredServiceName = registeredServiceName;
    }

    public String getRegistrationType() {
        return registrationType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Hashtable<String, byte[]> getProperties() {
        return properties;
    }

    public int getPort() {
        return port;
    }

    public boolean register(int port, ServiceNameProducer serviceNameProducer) {
        registered = false;
        String nextTry = serviceNameProducer.getServiceName();
        while (nextTry != null) {
            setName(nextTry);
            if (avahiConnection.register(this)) {
                return true;
            }
            nextTry = serviceNameProducer.getServiceName();
        }
        return false;
    }

}
