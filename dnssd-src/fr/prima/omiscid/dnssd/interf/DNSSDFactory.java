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

import java.util.Arrays;
import java.util.MissingResourceException;
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
                System.out.println("Problem while getting data ("+dnssdFactoryKey+") in opened bundle " + propertyBundle + ", using default factory");
                return makeHardCodedDefault();
            }
            try {
                if (System.getenv("OMISCID_DNSSD_FACTORY") != null) {
                    className = System.getenv("OMISCID_DNSSD_FACTORY");
                }
                // \REVIEWTASK this variable name should be documented somewhere
            } catch (SecurityException e) {
                // Access to environment variable is forbidden
            }
            if (!className.contains(".")) {
                try {
                    className = dnssdFactoryKey+"."+className;
                    className = bundle.getString(className);
                } catch (Exception e) {
                    System.out.println("Problem while getting data ("+className+") in opened bundle " + propertyBundle + ", using default factory");
                    return makeHardCodedDefault();
                }
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
                } catch (MissingResourceException e) {
                    System.out.println("Problem while testing for shared factory: key '"+sharedKey+"' not found, using non-shared by default");
                } catch (Exception e) {
                    System.out.println("Problem while testing for shared factory");
                } 
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
