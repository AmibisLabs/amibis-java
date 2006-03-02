/*
 * Created on Feb 13, 2006
 *
 */
package fr.prima.omiscid.dnssd.jmdns;

import java.io.IOException;
import java.util.Hashtable;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class ServiceRegistration
implements fr.prima.omiscid.dnssd.interf.ServiceRegistration {

    private JmDNS jmdns;
    private String serviceName;
    private String registrationType;
    private final Hashtable<String, String> properties = new Hashtable<String, String>();
    private boolean registered;
    private ServiceInfo serviceInfo;

    /*package*/ ServiceRegistration(JmDNS jmdns, String serviceName, String registrationType) {
        this.jmdns = jmdns;
        this.registrationType = registrationType;
        this.serviceName = serviceName;
    }

    public void addProperty(String name, String value) {
        properties.put(name, value);
    }

    public boolean register(int port) {
        registered = false;
        serviceInfo = new ServiceInfo(registrationType, serviceName, port, 0, 0, properties);
        try {
            jmdns.registerService(serviceInfo);
            registered = true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return registered;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void unregister() {
        jmdns.unregisterService(serviceInfo);
        registered = false;
    }

    public String getRegisteredName() {
        return serviceInfo.getQualifiedName();
    }

    public void setName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getName() {
        return serviceName;
    }

}
