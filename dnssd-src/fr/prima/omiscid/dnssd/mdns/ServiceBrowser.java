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

import java.util.List;
import java.util.Vector;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.ResolveListener;
import com.apple.dnssd.TXTRecord;

import fr.prima.omiscid.dnssd.interf.ServiceEvent;
import fr.prima.omiscid.dnssd.interf.ServiceEventListener;
import java.util.HashSet;
import java.util.Set;

/**
 * @author emonet build from BrowseForService by pesnel and reignier
 */
public class ServiceBrowser implements BrowseListener, fr.prima.omiscid.dnssd.interf.ServiceBrowser {

    private List<ServiceEventListener> listeners = new Vector<ServiceEventListener>();
    private final Set<String> serviceNames = new HashSet();
    
    private String registrationType;

    private DNSSDService dnssdService;

    /* package */ServiceBrowser(String registrationType) {
        this.registrationType = registrationType;
    }

    public synchronized void addListener(ServiceEventListener l) {
        listeners.add(l);
    }

    public synchronized void removeListener(ServiceEventListener l) {
        listeners.remove(l);
    }

    public synchronized void start() {
        try {
            synchronized (DNSSD.class) {
                dnssdService = DNSSD.browse(0, 0, registrationType, null, this);
            }
        } catch (DNSSDException e) {
            System.err.println("Error in Start Browse");
            e.printStackTrace();
        }
    }

    public synchronized void stop() {
        dnssdService.stop();
    }

    private synchronized void notifyListeners(ServiceInformation serviceInformation, int type) {
        ServiceEvent event = new ServiceEvent(serviceInformation, type);
        for (ServiceEventListener l : listeners) {
            l.serviceEventReceived(event);
        }
    }

    public synchronized void serviceLost(DNSSDService browser, int flags, int ifIndex, String serviceName, String regType, String domain) {
        ServiceInformation serviceInformation = new ServiceInformation(regType + domain, serviceName);
        String fullName = serviceInformation.getFullName();
        if (serviceNames.remove(fullName)) {
            notifyListeners(serviceInformation, ServiceEvent.LOST);
        }
    }

    public synchronized void operationFailed(DNSSDService service, int errorCode) {
        System.err.println(this.getClass().getName() + ": operation failed (" + errorCode + ")");
    }

    // private Map<DNSSDService, ServiceInformation> serviceInformations = new
    // HashMap<DNSSDService, ServiceInformation>();

    private class MemoryResolveListener implements ResolveListener {
        String registrationType;

        String serviceName;

        public MemoryResolveListener(String registrationType, String serviceName) {
            this.registrationType = registrationType;
            this.serviceName = serviceName;
        }

        public synchronized void serviceResolved(final DNSSDService resolver, int flags, int ifIndex, String fullName, String hostName, int port, TXTRecord txtRecord) {
            ServiceInformation serviceInformation = new ServiceInformation(this.registrationType, fullName, hostName, port, txtRecord);
            String infoFullName = serviceInformation.getFullName();
            if (!serviceNames.contains(infoFullName)) {
                serviceNames.add(infoFullName);
                notifyListeners(serviceInformation, ServiceEvent.FOUND);
                // it seems to be important to be after that notify listener ...
                // probably a dnssd implementation bug
                resolver.stop();
            }
        }

        public synchronized void operationFailed(DNSSDService service, int errorCode) {
            System.err.println(this.getClass().getName() + ": operation failed (" + errorCode + ")");
        }
    }

    public void serviceFound(DNSSDService browser, int flags, int ifIndex, String serviceName, String regType, String domain) {
        try {
            synchronized (DNSSD.class) {
                DNSSD.resolve(0, ifIndex, serviceName, regType, domain, new MemoryResolveListener(regType+domain, serviceName));
            }
        } catch (DNSSDException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
