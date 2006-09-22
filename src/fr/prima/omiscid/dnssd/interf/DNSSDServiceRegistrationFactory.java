package fr.prima.omiscid.dnssd.interf;

public interface DNSSDServiceRegistrationFactory {

    /**
     * Creates a new object representing the registration to the dnssd network.
     * 
     * @see ServiceRegistration to see how to register and unregister services
     *      using {@link ServiceRegistration}
     * @param serviceName
     * @param registrationType
     * @return
     */
    ServiceRegistration createServiceRegistration(String serviceName, String registrationType);
}
