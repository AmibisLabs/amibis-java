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
 * @author emonet
 *
 */
public interface DNSSDFactory {
    
    ServiceBrowser createServiceBrowser(String registrationType);
    
    ServiceRegistration createServiceRegistration(String serviceName, String registrationType);

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
