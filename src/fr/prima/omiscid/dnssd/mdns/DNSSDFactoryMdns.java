/*
 * Created on Feb 13, 2006
 *
 */
package fr.prima.omiscid.dnssd.mdns;

import fr.prima.omiscid.dnssd.interf.DNSSDFactory;
import fr.prima.omiscid.dnssd.interf.ServiceBrowser;
import fr.prima.omiscid.dnssd.interf.ServiceRegistration;

/**
 * @author emonet
 */
public class DNSSDFactoryMdns implements DNSSDFactory {

    public ServiceBrowser createServiceBrowser(String registrationType) {
        return new fr.prima.omiscid.dnssd.mdns.ServiceBrowser(registrationType);
    }

    public ServiceRegistration createServiceRegistration(String serviceName, String registrationType) {
        return new fr.prima.omiscid.dnssd.mdns.ServiceRegistration(serviceName, registrationType);
    }

    // public ServiceInformation createServiceInformation() {
    // // TODO Auto-generated method stub
    // return null;
    // }

}
