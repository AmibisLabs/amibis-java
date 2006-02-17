/*
 * Created on Feb 13, 2006
 *
 */
package fr.prima.bipdnssd.jmdns;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;

import fr.prima.bipdnssd.interf.DNSSDFactory;
import fr.prima.bipdnssd.interf.ServiceBrowser;
import fr.prima.bipdnssd.interf.ServiceRegistration;

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
        return new fr.prima.bipdnssd.jmdns.ServiceBrowser(jmdns, registrationType + ".local.");
    }

    public ServiceRegistration createServiceRegistration(String serviceName, String registrationType) {
        return new fr.prima.bipdnssd.jmdns.ServiceRegistration(jmdns, serviceName, registrationType + ".local.");
    }

//    public ServiceInformation createServiceInformation() {
//        // TODO Auto-generated method stub
//        return null;
//    }

}
