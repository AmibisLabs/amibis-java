/*
 * Created on Feb 15, 2006
 *
 */
package fr.prima.omiscid.dnssd.client;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

public class ServiceRegistration implements fr.prima.omiscid.dnssd.interf.ServiceRegistration {

    private ServiceRegistrator serviceRegistrator;

    private boolean registered;

    private String registrationType;

    private String serviceName;

    private String registeredServiceName;

    private Hashtable<String, byte[]> properties = new Hashtable<String, byte[]>();

    private int port;

    /* package */ServiceRegistration(ServiceRegistrator serviceRegistrator, String serviceName, String registrationType) {
        this.serviceRegistrator = serviceRegistrator;
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
        return serviceRegistrator.register(this);
    }

    public boolean isRegistered() {
        return registered;
    }

    public void unregister() {
        serviceRegistrator.unregister(this);
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
        throw new UnsupportedOperationException();
    }

}
