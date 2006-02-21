/*
 * Created on Feb 13, 2006
 *
 */
package fr.prima.bipdnssd.interf;

import java.util.Arrays;
import java.util.ResourceBundle;

import fr.prima.bipdnssd.client.DNSSDFactoryBip;

/**
 * 
 * Factory interface used to generate objects to access the dnssd network.
 * The concrete factories implementing this interface are the entry points
 * to each implementation of the dnssd abstracting layout.
 * 
 * All classes in this package describe this abstract layout interfacing
 * the user with dnssd. 
 * @see ServiceBrowser
 * @see ServiceRegistration
 * @see ServiceInformation
 * @see ServiceEventListener
 *
 * @author emonet
 *
 */
public interface DNSSDFactory {
    
    /**
     * Creates a ServiceBrowser that will list all services of the given dnssd registration type.
     * @see ServiceBrowser to see how to use the {@link ServiceBrowser}
     * 
     * @param registrationType
     * @return a new concrete ServiceBrower that will be used for service listing 
     */
    ServiceBrowser createServiceBrowser(String registrationType);
    
    /**
     * Creates a new object representing the registration to the dnssd network.
     * @see ServiceRegistration to see how to register and unregister services using {@link ServiceRegistration}
     * 
     * @param serviceName
     * @param registrationType
     * @return 
     */
    ServiceRegistration createServiceRegistration(String serviceName, String registrationType);

    
    /**
     * 
     * Static methods to lasily instanciate the default factory used
     * (that is the default concrete dnssd implementation used to implement the abstract layout).
     * 
     * The static {@link DNSSDFactory.DefaultFactory#instance()} method returns a factory based on
     * the class name given in the {@link DNSSDFactory.DefaultFactory#dnssdFactoryKey} key value
     * found in the {@link DNSSDFactory.DefaultFactory#propertyBundle}.properties property file.
     * If a problem occurs, the default hard coded factory is returned.
     *
     */
    public static final class DefaultFactory {
        private static final String propertyBundle = "cfg";
        private static final String dnssdFactoryKey = "dnssdFactory";
        private static DNSSDFactory instance = null;
        public static DNSSDFactory instance() {
            return  instance != null ? instance : (instance = makeInstance());
        }
        private static DNSSDFactory makeInstance() {
            String className;
            ResourceBundle bundle;
            try {
                bundle = ResourceBundle.getBundle(propertyBundle);
            } catch (Exception e) {
                System.out.println("Problem while getting resource bundle "+propertyBundle+", using default factory");
                return makeHardCodedDefault();
            }
            try {
                className = bundle.getString(dnssdFactoryKey);
            } catch (Exception e) {
                System.out.println("Problem while getting data in opened bundle "+propertyBundle+", using default factory");
                return makeHardCodedDefault();
            }
            Class factoryClass;
            try {
                 factoryClass = Class.forName(className);
            } catch (Exception e) {
                System.out.println("Problem while retrieving class \""+className+"\", using default factory");
                return makeHardCodedDefault();
            }
            if (!Arrays.asList(factoryClass.getInterfaces()).contains(DNSSDFactory.class)) {
                System.out.println("Specified class \""+className+"\" is not a DNSSDFactory, using default factory");
                return makeHardCodedDefault();
            }
            try {
                return (DNSSDFactory) factoryClass.newInstance();
            } catch (Exception e) {
                System.out.println("Problem while instanciating \""+className+"\", using default factory");
                return makeHardCodedDefault();
            }
        }
        private static DNSSDFactory makeHardCodedDefault() {
            return new DNSSDFactoryBip();
        }
    }
}
