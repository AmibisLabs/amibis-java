/**
 * Copyright© 2005-2006 INRIA/Université Pierre Mendès-France/Université Joseph Fourier.
 *
 * O3MiSCID (aka OMiSCID) Software written by Sebastien Pesnel, Dominique
 * Vaufreydaz, Patrick Reignier, Remi Emonet and Julien Letessier.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package fr.prima.omiscid.dnssd.interf;

/**
 * Represents the registration under dnssd. It is used to register and
 * unregister a service.
 *
 * @author emonet
 */
public interface ServiceRegistration {
    
    public static interface ServiceNameProducer {
        /**
         * This method being called does not necessarilly means that
         * the returned service name has been tried for registration.
         * For example, implementations can cache a certain number of
         * returned service names and return as soon it can register one.
         * 
         * @return null or the next service name to try to for registration.
         */
        String getServiceName();
    }

    /**
     * Adds a property to the property set under dnssd. The properties must be
     * set before registrating the service (before calling
     * {@link #register(int)}). If the property is already set, its value is replaced by the
     * given value.
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

    /**
     * Tries to register the service with a fixed name. It tries iterativelly the
     * service names returned by the given {@link ServiceNameProducer}.
     *   
     * @param port
     * @param serviceNameProducer
     * @return whether the registration was successful using one of the names given by the {@link ServiceNameProducer}
     */
    boolean register(int port, ServiceNameProducer serviceNameProducer);

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
