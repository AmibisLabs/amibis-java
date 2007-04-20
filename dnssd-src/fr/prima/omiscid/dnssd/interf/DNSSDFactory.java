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
import java.util.HashMap;
import java.util.Stack;

import fr.prima.omiscid.dnssd.common.SharedFactory;

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

        private static final String factoryEnvironmentVariable = "OMISCID_DNSSD_FACTORY";
        private static final String sharedFactoryEnvironmentVariable = "OMISCID_DNSSD_SHARED_FACTORY";
        private static final String verboseEnvironmentVariable = "OMISCID_DNSSD_FACTORY_VERBOSE_MODE";
        private static final String sharedTrueValue = "true";
        private static final String sharedDefaultValue = sharedTrueValue;
        private static final PatchedIdentity<String> factoryRewriter = new PatchedIdentity<String>();

        private static boolean verboseMode = false;
        
        static {
            factoryRewriter.put("jmdns", "fr.prima.omiscid.dnssd.jmdns.DNSSDFactoryJmdns");
            factoryRewriter.put("mdns", "fr.prima.omiscid.dnssd.mdns.DNSSDFactoryMdns");
            factoryRewriter.put("avahi", "fr.prima.omiscid.dnssd.avahi.DNSSDFactoryAvahi");
            try {
                if (System.getenv(verboseEnvironmentVariable) != null) {
                    verboseMode = true;
                }
            } catch (SecurityException e) {
                // Access to environment variable is forbidden
            }
        }

        private static DNSSDFactory instance = null;

        public static synchronized DNSSDFactory instance() {
            return instance != null ? instance : (instance = makeInstance());
        }
        
        private static class PatchedIdentity<V> extends HashMap<V,V> {
            @Override public V get(Object key) {
                V res = super.get(key);
                return res == null ? (V)key : res;
            }
        }

        private static DNSSDFactory makeInstance() {
            Stack<String> factories = new Stack<String>();
            factories.push(factoryRewriter.get("jmdns"));
            factories.push(factoryRewriter.get("mdns"));
            factories.push(factoryRewriter.get("avahi"));
            try {
                if (System.getenv(factoryEnvironmentVariable) != null) {
                    factories.push(factoryRewriter.get(System.getenv(factoryEnvironmentVariable)));
                }
            } catch (SecurityException e) {
                // Access to environment variable is forbidden
            }
            
            Class factoryClass = null;
            String className;
            while (!factories.isEmpty() && null != (className = factories.pop())) {
                if (verboseMode) {
                    System.out.println(className);
                }
                try {
                    factoryClass = Class.forName(className);
                } catch (Throwable e) {
                    // Problem while looking for given class, falling back to the next choice
                    continue;
                }
                if (factoryClass != null
                        && !Arrays.asList(factoryClass.getInterfaces()).contains(DNSSDFactory.class)) {
                    System.err.println("Specified class \"" + className + "\" is not a DNSSDFactory ... ignoring");
                    factoryClass = null;
                    continue;
                }
                try {
                    //System.out.println(factoryClass.getCanonicalName());
                    DNSSDFactory factory = (DNSSDFactory) factoryClass.newInstance();
                    try {
                        String shared = sharedDefaultValue;
                        try {
                            if (System.getenv(sharedFactoryEnvironmentVariable) != null) {
                                shared = System.getenv(sharedFactoryEnvironmentVariable);
                            }
                        } catch (SecurityException e) {
                            // Access to environment variable is forbidden
                        }
                        if (sharedTrueValue.equals(shared)) {
                            factory = new SharedFactory(factory);
                        }
                    } catch (Exception e) {
                        System.out.println("Problem while testing for shared factory");
                    } 
                    return factory;
                } catch (Exception e) {
                    System.out.println("Problem while instanciating \"" + className + "\", using default factory");
                }
            }
            System.err.println("Could not get any operational DNSSDFactory ... will badly throw a NPE soon");
            return null;
        }

    }
}
