/*
 * Created on Feb 13, 2006
 *
 */
package fr.prima.omiscid.dnssd.jmdns;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;

import fr.prima.omiscid.dnssd.interf.DNSSDFactory;
import fr.prima.omiscid.dnssd.interf.ServiceBrowser;
import fr.prima.omiscid.dnssd.interf.ServiceRegistration;

public class DNSSDFactoryJmdns implements DNSSDFactory {

    private JmDNS jmdns;

    public DNSSDFactoryJmdns() {
        try {
            this.jmdns = new JmDNS(InetAddress.getLocalHost());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ServiceBrowser createServiceBrowser(String registrationType) {
        return new fr.prima.omiscid.dnssd.jmdns.ServiceBrowser(jmdns, registrationType + ".local.");
    }

    public ServiceRegistration createServiceRegistration(String serviceName, String registrationType) {
        return new fr.prima.omiscid.dnssd.jmdns.ServiceRegistration(jmdns, serviceName, registrationType + ".local.");
    }

}
