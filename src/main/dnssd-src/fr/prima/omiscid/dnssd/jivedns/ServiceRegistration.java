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

package fr.prima.omiscid.dnssd.jivedns;

import java.io.IOException;
import java.util.Hashtable;
import org.jivedns.JiveDNS;
import org.jivedns.ServiceInfo;

public class ServiceRegistration implements fr.prima.omiscid.dnssd.interf.ServiceRegistration {

    private JiveDNS JiveDNS;

    private String serviceName;

    private String registrationType;

    private String registeredName;

    private final Hashtable<String, String> properties = new Hashtable<String, String>();
    private int estimatedPropertiesSize = 0;

    private boolean registered;

    private ServiceInfo serviceInfo;

    /* package */ServiceRegistration(JiveDNS JiveDNS, String serviceName, String registrationType) {
        this.JiveDNS = JiveDNS;
        this.registrationType = registrationType;
        this.serviceName = serviceName;
    }

    public void addProperty(String name, String value) {
        String old = properties.put(name, value);
        estimatedPropertiesSize += value.length();
        if (old != null) {
            estimatedPropertiesSize -= old.length();
        } else {
            estimatedPropertiesSize += name.length() + 2; // [size]name=... (one byte for size, one for '=')
        }
        if (estimatedPropertiesSize > 1024) {
            // It seems that JiveDNS can support more but not much
            properties.remove(name);
            estimatedPropertiesSize -= name.length() + 2;
            estimatedPropertiesSize -= value.length();
            throw new RuntimeException("Maximum overall properties size reached in dnssd access implementation using jivedns");
        }
    }

    public boolean register(int port) {
        registered = false;
        serviceInfo = new ServiceInfo(registrationType, serviceName, port, 0, 0, properties);
        try {
            JiveDNS.registerService(serviceInfo);
            registered = true;
            registeredName = clean(serviceInfo.getQualifiedName());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return registered;
    }
    
    public boolean register(int port, ServiceNameProducer serviceNameProducer) {
        registered = false;
        while ((serviceName = serviceNameProducer.getServiceName()) != null) {
            serviceInfo = new ServiceInfo(registrationType, serviceName, port, 0, 0, properties);
            if (!serviceInfo.getPropertyNames().hasMoreElements() && !properties.isEmpty()) {
                return registered;
            }
            try {
                JiveDNS.registerService(serviceInfo);
                if (serviceInfo.getName().equals(serviceName)) {
                    registered = true;
                    registeredName = clean(serviceInfo.getQualifiedName());
                    break;
                } else {
                    JiveDNS.unregisterService(serviceInfo);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return registered;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void unregister() {
        JiveDNS.unregisterService(serviceInfo);
        registered = false;
    }

    public String getRegisteredName() {
        return registeredName;
    }

    public void setName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getName() {
        return serviceName;
    }
    
    public void setHostName(String serviceHostName) {
        throw new RuntimeException("OMiSCID DNSSD Server unsupported with jivedns");
    }

    private String clean(String registeredName) {
        String pattern = ("."+registrationType).replaceAll("(.)", "[$1]")+"$";
        return registeredName.replaceFirst(pattern, "");
    }

}
