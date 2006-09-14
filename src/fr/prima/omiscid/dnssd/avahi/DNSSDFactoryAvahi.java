package fr.prima.omiscid.dnssd.avahi;

import fr.prima.omiscid.dnssd.interf.DNSSDFactory;
import fr.prima.omiscid.dnssd.interf.ServiceBrowser;
import fr.prima.omiscid.dnssd.interf.ServiceRegistration;

public class DNSSDFactoryAvahi implements DNSSDFactory {

//    private static AvahiConnection avahiConnection = null;
//    /*package*/ static synchronized AvahiConnection avahiConnection() {
//        if (avahiConnection == null) {
//            avahiConnection = new AvahiConnection(); 
//        }
//        return avahiConnection;
//    }
    
    public ServiceBrowser createServiceBrowser(String registrationType) {
        return new fr.prima.omiscid.dnssd.avahi.ServiceBrowser(new AvahiConnection(), registrationType);
    }

    public ServiceRegistration createServiceRegistration(String serviceName, String registrationType) {
        return new fr.prima.omiscid.dnssd.avahi.ServiceRegistration(new AvahiConnection(), serviceName, registrationType);
    }

}
