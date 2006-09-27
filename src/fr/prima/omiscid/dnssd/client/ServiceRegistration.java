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

package fr.prima.omiscid.dnssd.client;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

public class ServiceRegistration implements fr.prima.omiscid.dnssd.interf.ServiceRegistration {

    private ServiceRegistrator serviceRegistrator;

    private boolean registered;

    private String registrationType;

    private String serviceName;

    private String registeredServiceName;

    private Hashtable<String, byte[]> properties = new Hashtable<String, byte[]>();

    private int port;

    /* package */ServiceRegistration(ServiceRegistrator serviceRegistrator, String serviceName, String registrationType) {
        this.serviceRegistrator = serviceRegistrator;
        this.registrationType = registrationType;
        this.serviceName = serviceName;
    }

    public void addProperty(String name, String value) {
        try {
            properties.put(name, value.getBytes("utf-8"));
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

    public boolean register(int port) {
        this.port = port;
        return serviceRegistrator.register(this);
    }

    public boolean isRegistered() {
        return registered;
    }

    public void unregister() {
        serviceRegistrator.unregister(this);
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
        throw new UnsupportedOperationException();
    }

}
