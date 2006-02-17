/*
 * Created on Feb 14, 2006
 *
 */
package fr.prima.bipdnssd.mdns;

import java.util.List;
import java.util.Vector;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.ResolveListener;
import com.apple.dnssd.TXTRecord;

import fr.prima.bipdnssd.interf.ServiceEvent;
import fr.prima.bipdnssd.interf.ServiceEventListener;

/**
 * 
 * @author emonet build from BrowseForService by pesnel and reignier
 *
 */
public class ServiceBrowser
implements BrowseListener,
fr.prima.bipdnssd.interf.ServiceBrowser {

    private List<ServiceEventListener> listeners = new Vector<ServiceEventListener>();
    
    private String registrationType;

    private DNSSDService dnssdService;
    
    /*package*/ ServiceBrowser(String registrationType) {
        this.registrationType = registrationType;
    }

    public void addListener(ServiceEventListener l) {
        listeners.add(l);
    }

    public void removeListener(ServiceEventListener l) {
        listeners.remove(l);
    }

    public void start() {
        try {
            dnssdService = DNSSD.browse(0, 0, registrationType, null, this);
        } catch (DNSSDException e) {
            System.err.println("Error in Start Browse");
            e.printStackTrace();
        }
    }

    public void stop() {
        dnssdService.stop();
    }

    private void notifyListeners(ServiceInformation serviceInformation, int type) {
        ServiceEvent event = new ServiceEvent(serviceInformation, type);
        for (ServiceEventListener l : listeners) {
            l.serviceEventReceived(event);
        }
    }

    public void serviceLost(DNSSDService browser, int flags, int ifIndex,
            String serviceName, String regType, String domain) {
        notifyListeners(new ServiceInformation(regType, serviceName), ServiceEvent.LOST);
    }

    public void operationFailed(DNSSDService service, int errorCode) {
        System.err.println(this.getClass().getName()+": operation failed ("+errorCode+")");
    }

    //private Map<DNSSDService, ServiceInformation> serviceInformations = new HashMap<DNSSDService, ServiceInformation>();
    
    private class MemoryResolveListener implements ResolveListener {
        String registrationType;
        String serviceName;
        public MemoryResolveListener(String registrationType, String serviceName) {
            this.registrationType = registrationType;
            this.serviceName = serviceName;
        }
        public void serviceResolved(DNSSDService resolver, int flags, int ifIndex,
                String fullName, String hostName, int port, TXTRecord txtRecord) {
            resolver.stop();
            ServiceInformation serviceInformation = new ServiceInformation( this.registrationType, this.serviceName, hostName, port, txtRecord);
            notifyListeners(serviceInformation, ServiceEvent.FOUND);
        }
        public void operationFailed(DNSSDService service, int errorCode) {
            System.err.println(this.getClass().getName()+": operation failed ("+errorCode+")");
        }
    }
    
    public void serviceFound(DNSSDService browser, int flags, int ifIndex,
            String serviceName, String regType, String domain) {
        try {
            //new Service(serviceName, regType, domain);
            DNSSD.resolve(0, ifIndex, serviceName, regType, domain,
                    new MemoryResolveListener(regType, serviceName));
        } catch (DNSSDException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
