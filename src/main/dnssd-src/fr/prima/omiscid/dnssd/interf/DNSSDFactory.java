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
import java.io.PrintWriter;
import java.io.StringWriter;

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
        private static final String factoryEnvironmentVariable = "OMISCID_DNSSD_FACTORY";
        private static final String factoryEnvironmentVariableDebugValue = "DEBUG";
        private static final String sharedFactoryEnvironmentVariable = "OMISCID_DNSSD_SHARED_FACTORY";
        private static final String verboseEnvironmentVariable = "OMISCID_DNSSD_FACTORY_VERBOSE_MODE";
        private static final String sharedTrueValue = "true";
        private static final String preventIPV4Preference = "OMISCID_DNSSD_FACTORY_NO_PREFERIPV4STACK";
        private static final PatchedIdentity<String> factoryRewriter = new PatchedIdentity<String>();

        public static boolean verboseMode = false;
        public static boolean verboseModeMore = false;
        public static String factoryToTryFirst = null;
        public static boolean shared = true;
        /**
         * This can be used (set to true) when the jvm does not support full introspection.
         * This only disables a additional check on the provided class name.
         */
        public static boolean skipIntrospectionBasedVerification = false;
        
        static {
            //factoryRewriter.put("jmdns",  "fr.prima.omiscid.dnssd.jmdns.DNSSDFactoryJmdns");
            factoryRewriter.put("jmdns",  "fr.prima.omiscid.dnssd.jmdns.DNSSDFactoryJmdns");
            factoryRewriter.put("jivedns", "fr.prima.omiscid.dnssd.jivedns.DNSSDFactoryJivedns");
            factoryRewriter.put("mdns", "fr.prima.omiscid.dnssd.mdns.DNSSDFactoryMdns");
            factoryRewriter.put("avahi", "fr.prima.omiscid.dnssd.avahi.DNSSDFactoryAvahi");
            factoryRewriter.put("proxy", "fr.prima.omiscid.dnssd.client.DNSSDFactoryOmiscid");
            try {
                if (System.getenv(verboseEnvironmentVariable) != null) {
                    verboseMode = true;
                    if (System.getenv(verboseEnvironmentVariable).equalsIgnoreCase(factoryEnvironmentVariableDebugValue)) {
                        verboseModeMore = true;
                    }
                }
                if (System.getenv(preventIPV4Preference) == null) {
                    System.setProperty("java.net.preferIPv4Stack", "true");
                }
                if (System.getenv(sharedFactoryEnvironmentVariable) != null) {
                    String sharedString = System.getenv(sharedFactoryEnvironmentVariable);
                    shared = sharedTrueValue.equals(sharedString);
                }
            } catch (SecurityException e) {
                // Access to environment variable is forbidden
            }
        }

        private static DNSSDFactory instance = null;

        public static synchronized DNSSDFactory instance() {
            return instance != null ? instance : (instance = makeInstance());
        }

        private static void verboseMessage(String message) {
            if (verboseMode) {
                System.out.println("OmiscidDnssd: "+message);
            }
        }
        private static void verboseMessage(Throwable e) {
            if (verboseMode) {
                StringWriter w = new StringWriter();
                PrintWriter wr = new PrintWriter(w);
                e.printStackTrace(wr);
                for (String line : w.toString().split("\n")) {
                    verboseMessage(line);
                }
            }
        }
        
        private static class PatchedIdentity<V> extends HashMap<V,V> {
            @Override public V get(Object key) {
                V res = super.get(key);
                return res == null ? (V)key : res;
            }
        }

        private static DNSSDFactory makeInstance() {
            Stack<String> factories = new Stack<String>();
            factories.push(factoryRewriter.get("jivedns")); // tried really last
            factories.push(factoryRewriter.get("jmdns")); // tried last
            factories.push(factoryRewriter.get("mdns"));
            factories.push(factoryRewriter.get("avahi"));
            try {
                if (System.getenv(factoryEnvironmentVariable) != null) {
                    factories.push(factoryRewriter.get(System.getenv(factoryEnvironmentVariable)));
                }
            } catch (SecurityException e) {
                // Access to environment variable is forbidden
            }
            if (factoryToTryFirst != null) {
                factories.push(factoryRewriter.get(factoryToTryFirst));
            }
            Class factoryClass = null;
            String className;
            while (!factories.isEmpty() && null != (className = factories.pop())) {
                verboseMessage("trying factory " + className);
                try {
                    factoryClass = Class.forName(className);
                } catch (Throwable e) {
                    // Problem while looking for given class, falling back to the next choice
                    if (verboseModeMore) {
                        verboseMessage(e);
                    }
                    continue;
                }
                if (factoryClass != null
                        && !skipIntrospectionBasedVerification
                        && !Arrays.asList(factoryClass.getInterfaces()).contains(DNSSDFactory.class)) {
                    System.err.println("Specified class \"" + className + "\" is not a DNSSDFactory ... ignoring");
                    factoryClass = null;
                    continue;
                }
                try {
                    //System.out.println(factoryClass.getCanonicalName());
                    verboseMessage("using factory " + className);
                    DNSSDFactory factory = (DNSSDFactory) factoryClass.newInstance();
                    if (shared) {
                        verboseMessage("using shared factory");
                        factory = new SharedFactory(factory);
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
