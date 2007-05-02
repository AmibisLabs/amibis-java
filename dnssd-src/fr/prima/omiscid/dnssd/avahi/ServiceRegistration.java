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

package fr.prima.omiscid.dnssd.avahi;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

public class ServiceRegistration implements fr.prima.omiscid.dnssd.interf.ServiceRegistration {

    private AvahiConnection avahiConnection;

    private boolean registered;

    private String registrationType;

    private String serviceName;

    private String registeredServiceName;

    private String hostName;

    private Hashtable<String, byte[]> properties = new Hashtable<String, byte[]>();
    private int estimatedPropertiesSize = 50; // maximum estimated size of the avahi cookie

    private int port;

    /* package */ServiceRegistration(AvahiConnection avahiConnection, String serviceName, String registrationType) {
        this.avahiConnection = avahiConnection;
        this.registrationType = registrationType;
        this.serviceName = serviceName;
    }

    public void addProperty(String name, String value) {
        try {
            byte[] prop = value.getBytes("utf-8");
            byte[] old = properties.put(name, value.getBytes("utf-8"));
            estimatedPropertiesSize += prop.length;
            if (old != null) {
                estimatedPropertiesSize -= old.length;
            } else {
                estimatedPropertiesSize += name.length() + 2; // [size]name=... (one byte for size, one for '=')
            }
            if (estimatedPropertiesSize > 1024) {
                // It seems that avahi can support more but not much
                properties.remove(name);
                estimatedPropertiesSize -= name.length() + 2;
                estimatedPropertiesSize -= prop.length;
                throw new RuntimeException("Maximum overall properties size reached in dnssd access implementation using avahi");
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getName() {
        return serviceName;
    }
    
    public void setHostName(String serviceHostName) {
        this.hostName = serviceHostName;
    }
    
    /*package*/ String getHostName() {
        return this.hostName;
    }

    public boolean register(int port) {
        this.port = port;
        // Do not try to do some renaming
        // TODO if needed, could have some renaming rules
        return avahiConnection.register(this) != null;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void unregister() {
        avahiConnection.unregister(this);
    }

    public String getRegisteredName() {
        // TODO is it ok?
        return registeredServiceName;
    }

    /* package */void setRegisteredServiceName(String registeredServiceName) {
        this.registeredServiceName = registeredServiceName;
    }

    public String getRegistrationType() {
        return registrationType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Hashtable<String, byte[]> getProperties() {
        return properties;
    }

    public int getPort() {
        return port;
    }

    public boolean register(int port, ServiceNameProducer serviceNameProducer) {
        this.port = port;
        registered = false;
        String nextTry = serviceNameProducer.getServiceName();
        while (nextTry != null) {
            setName(nextTry);
            String registeredName = avahiConnection.register(this);
            if (registeredName != null) {
                registered = true;
                setRegisteredServiceName(registeredName);
                return true;
            }
            nextTry = serviceNameProducer.getServiceName();
        }
        return false;
    }

}
