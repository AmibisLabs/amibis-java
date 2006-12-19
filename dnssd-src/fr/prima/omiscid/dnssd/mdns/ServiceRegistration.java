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

package fr.prima.omiscid.dnssd.mdns;

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.RegisterListener;
import com.apple.dnssd.TXTRecord;

/**
 * @author emonet initial build from RegisterOmiscidService by pesnel and
 *         reignier
 */
public class ServiceRegistration implements RegisterListener, fr.prima.omiscid.dnssd.interf.ServiceRegistration {

    /** Flag for the registration */
    private static final int FLAG = 0;

    /** IF_INDEX for the registration */
    private static final int IF_INDEX = 0;

    /** Domain for the registration */
    private static final String DOMAIN = null;

    private String serviceName;

    private String registrationType;

    private DNSSDRegistration dnssdRegistration;

    private String registeredServiceName;

    private boolean registered;

    private TXTRecord txtRecord = new TXTRecord();

    private String hostName = null;

    /* package */ServiceRegistration(String serviceName, String registrationType) {
        this.serviceName = serviceName;
        this.registrationType = registrationType;
    }

    public synchronized void operationFailed(DNSSDService service, int errorCode) {
        // synchronized (registerEvent) {
        // // System.err.println("operation failed");
        registered = false;
        this.notify();
        // }
    }

    public synchronized void serviceRegistered(DNSSDRegistration registration, int flags, String serviceName, String regType, String domain) {
        if (registration != dnssdRegistration) {
            // System.out.println(registration);
            // System.out.println(dnssdRegistration);
        }
        registered = true;
        registeredServiceName = serviceName;
        this.notify();
    }

    public void addProperty(String name, String value) {
        txtRecord.set(name, value);
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

    public synchronized boolean register(int port) {
        registered = false;
        try {
            dnssdRegistration = DNSSD.register(FLAG, IF_INDEX, serviceName, registrationType, DOMAIN, hostName, port, txtRecord, this);
            this.wait();
        } catch (com.apple.dnssd.DNSSDException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return registered;
    }

    public synchronized boolean register(int port, ServiceNameProducer serviceNameProducer) {
        registered = false;
        String nextTry = serviceNameProducer.getServiceName();
        while (!registered && nextTry != null) {
            setName(nextTry);
            try {
                dnssdRegistration = DNSSD.register(DNSSD.NO_AUTO_RENAME, IF_INDEX, serviceName, registrationType, DOMAIN, hostName, port, txtRecord, this);
                this.wait();
            } catch (com.apple.dnssd.DNSSDException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            nextTry = serviceNameProducer.getServiceName();
        }
        return registered;
    }

    public synchronized boolean isRegistered() {
        return registered;
    }

    public synchronized void unregister() {
        dnssdRegistration.stop();
    }

    public synchronized String getRegisteredName() {
        return registeredServiceName;
    }

}
