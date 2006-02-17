/*
 * Created on Feb 13, 2006
 *
 */
package fr.prima.bipdnssd.mdns;

import fr.prima.bipdnssd.interf.DNSSDFactory;
import fr.prima.bipdnssd.interf.ServiceBrowser;
import fr.prima.bipdnssd.interf.ServiceRegistration;

/**
 * 
 * @author emonet
 *
 */
public class DNSSDFactoryMdns implements DNSSDFactory {

    public ServiceBrowser createServiceBrowser(String registrationType) {
        return new fr.prima.bipdnssd.mdns.ServiceBrowser(registrationType);
    }

    public ServiceRegistration createServiceRegistration(String serviceName, String registrationType) {
        return new fr.prima.bipdnssd.mdns.ServiceRegistration(serviceName, registrationType);
    }

//    public ServiceInformation createServiceInformation() {
//        // TODO Auto-generated method stub
//        return null;
//    }

}
