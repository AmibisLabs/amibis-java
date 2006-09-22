package fr.prima.omiscid.dnssd.common;

import fr.prima.omiscid.dnssd.interf.DNSSDFactory;
import fr.prima.omiscid.dnssd.interf.DNSSDServiceBrowserFactory;
import fr.prima.omiscid.dnssd.interf.ServiceBrowser;
import fr.prima.omiscid.dnssd.interf.ServiceRegistration;

public class SharedFactory implements DNSSDFactory {
    
    private DNSSDFactory baseFactory;

    public SharedFactory(DNSSDFactory baseFactory) {
        this.baseFactory = baseFactory;
    }

    public ServiceBrowser createServiceBrowser(String registrationType) {
        return SharedBrowser.forType(registrationType, new DNSSDServiceBrowserFactory() {
            public ServiceBrowser createServiceBrowser(String registrationType) {
                return baseFactory.createServiceBrowser(registrationType);
            }
        });
    }
    public ServiceRegistration createServiceRegistration(String serviceName, String registrationType) {
        return baseFactory.createServiceRegistration(serviceName, registrationType);
    }
    

}
