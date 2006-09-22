/*
 * Created on Feb 13, 2006
 *
 */
package fr.prima.omiscid.dnssd.interf;

import java.util.Arrays;
import java.util.ResourceBundle;

import fr.prima.omiscid.dnssd.common.SharedFactory;
import fr.prima.omiscid.dnssd.mdns.DNSSDFactoryMdns;

/**
 * Factory interface used to generate objects to access the dnssd network. The
 * concrete factories implementing this interface are the entry points to each
 * implementation of the dnssd abstracting layout. All classes in this package
 * describe this abstract layout interfacing the user with dnssd.
 * 
 * @see ServiceBrowser
 * @see ServiceRegistration
 * @see ServiceInformation
 * @see ServiceEventListener
 * @author emonet
 */
public interface DNSSDFactory
extends DNSSDServiceBrowserFactory, DNSSDServiceRegistrationFactory {

    /**
     * Static methods to lasily instanciate the default factory used (that is
     * the default concrete dnssd implementation used to implement the abstract
     * layout). The static {@link DNSSDFactory.DefaultFactory#instance()} method
     * returns a factory based on the class name given in the
     * {@link DNSSDFactory.DefaultFactory#dnssdFactoryKey} key value found in
     * the {@link DNSSDFactory.DefaultFactory#propertyBundle}.properties
     * property file. If a problem occurs, the default hard coded factory is
     * returned.
     */
    public static final class DefaultFactory {
        private static final String propertyBundle = "cfg";
        private static final String dnssdFactoryKey = "dnssdFactory";
        private static final String sharedKey = "sharedFactory";
        private static final String sharedTrueValue = "true";

        private static DNSSDFactory instance = null;

        public static DNSSDFactory instance() {
            return instance != null ? instance : (instance = makeInstance());
        }

        private static DNSSDFactory makeInstance() {
            String className;
            ResourceBundle bundle;
            try {
                bundle = ResourceBundle.getBundle(propertyBundle);
            } catch (Exception e) {
                System.out.println("Problem while getting resource bundle " + propertyBundle + ", using default factory");
                return makeHardCodedDefault();
            }
            try {
                className = bundle.getString(dnssdFactoryKey);
            } catch (Exception e) {
                System.out.println("Problem while getting data in opened bundle " + propertyBundle + ", using default factory");
                return makeHardCodedDefault();
            }
            Class factoryClass;
            try {
                factoryClass = Class.forName(className);
            } catch (Exception e) {
                System.out.println("Problem while retrieving class \"" + className + "\", using default factory");
                return makeHardCodedDefault();
            }
            if (!Arrays.asList(factoryClass.getInterfaces()).contains(DNSSDFactory.class)) {
                System.out.println("Specified class \"" + className + "\" is not a DNSSDFactory, using default factory");
                return makeHardCodedDefault();
            }
            try {
                DNSSDFactory factory = (DNSSDFactory) factoryClass.newInstance();
                try {
                    if (sharedTrueValue.equals(bundle.getString(sharedKey))) {
                        factory = new SharedFactory(factory);
                    }
                    System.out.println(bundle.getString(sharedKey));
                } catch (Exception e) {
                    System.out.println("Problem while testing for shared factory");
                } 
                System.out.println(factory);
                return factory;
            } catch (Exception e) {
                System.out.println("Problem while instanciating \"" + className + "\", using default factory");
                return makeHardCodedDefault();
            }
        }

        private static DNSSDFactory makeHardCodedDefault() {
            return new DNSSDFactoryMdns();
        }
    }
}
