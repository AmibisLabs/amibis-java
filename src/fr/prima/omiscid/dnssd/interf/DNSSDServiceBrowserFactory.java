package fr.prima.omiscid.dnssd.interf;

public interface DNSSDServiceBrowserFactory {

    /**
     * Creates a ServiceBrowser that will list all services of the given dnssd
     * registration type.
     * 
     * @see ServiceBrowser to see how to use the {@link ServiceBrowser}
     * @param registrationType
     * @return a new concrete ServiceBrower that will be used for service
     *         listing
     */
    ServiceBrowser createServiceBrowser(String registrationType);
    
}
