/*
 * Created on Feb 15, 2006
 *
 */
package fr.prima.omiscid.dnssd.client;

import java.io.IOException;
import java.net.UnknownHostException;

import fr.prima.omiscid.dnssd.interf.DNSSDFactory;
import fr.prima.omiscid.dnssd.interf.ServiceBrowser;
import fr.prima.omiscid.dnssd.interf.ServiceRegistration;

public class DNSSDFactoryOmiscid implements DNSSDFactory {

    private static int port = 12053;

    private static String host = "localhost";

    private ServiceRegistrator serviceRegistrator;

    public synchronized ServiceBrowser createServiceBrowser(String registrationType) {
        return new fr.prima.omiscid.dnssd.client.ServiceBrowser(registrationType + ".local.", host, port);
    }

    public synchronized ServiceRegistration createServiceRegistration(String serviceName, String registrationType) {
        if (serviceRegistrator == null) {
            try {
                serviceRegistrator = new ServiceRegistrator(host, port);
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return new fr.prima.omiscid.dnssd.client.ServiceRegistration(serviceRegistrator, serviceName, registrationType + ".local.");
    }

}
