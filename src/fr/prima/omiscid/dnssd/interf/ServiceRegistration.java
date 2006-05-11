/*
 * Created on Feb 13, 2006
 *
 */
package fr.prima.omiscid.dnssd.interf;

/**
 * Represents the registration under dnssd. It is used to register and
 * unregister a service.
 * 
 * @author emonet
 */
public interface ServiceRegistration {

    /**
     * Adds a property to the property set under dnssd. The properties must be
     * set before registrating the service (before calling
     * {@link #register(int)})
     * 
     * @param name
     *            the name of the property
     * @param value
     *            the value for the property
     */
    void addProperty(String name, String value);

    /**
     * Set the desired base name for the service. The properties must be set
     * before registrating the service (before calling {@link #register(int)})
     * This name will be decorated by dnssd (to avoid name clashes and make a
     * unique name) and will be accessible through {@link #getRegisteredName()}.
     * 
     * @param serviceName
     */
    void setName(String serviceName);

    /**
     * Gets the desired base name for the service. After registration, the
     * registered name is accessible through {@link #getRegisteredName()}.
     * 
     * @return
     */
    String getName();

    /**
     * Registers the service under dnssd. If the registration succeeds, the
     * registered service name is accessible through
     * {@link #getRegisteredName()}.
     * 
     * @param port
     * @return whether the registration was successful
     */
    boolean register(int port);

    boolean isRegistered();

    /**
     * Unregisters the registered service from dnssd.
     */
    void unregister();

    /**
     * Gets the name which the service is registered under dnssd with. The
     * service must have been successfully registered beforehand.
     * 
     * @return
     */
    String getRegisteredName();
}
