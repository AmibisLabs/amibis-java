package fr.prima.bipcontrol ;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.ResolveListener;
import com.apple.dnssd.TXTRecord;

/**
 * Browse for service of a given type Use :
 * <ul>
 * <li>Create a BrowseForService object by given the type of registration for
 * which we want the registered service
 * <li>Add one or more listener called when a service is discovererd or lost
 * <li>Start browsing by calling the method StartBrowse
 * </ul>
 * 
 * @author Sebastien Pesnel
 * Refactoring by Patrick Reignier
 */
public class BrowseForService implements ResolveListener, BrowseListener {

    /**
	 * Structure to put together service data and a DNSSDService object. The DNSSDService object is associated to the query to resolve the service in order to obtain more data.
	 */
    static final class ServiceBrowse {
        /** Object associate to a demand to resolve the service */
        DNSSDService dnssdService;

        /** Contains the service data */
        Service service;
    }
    
    /** Object associated to the query for browsing */
    private DNSSDService dnssdService;

    /** Type of registration looked for */
    private String regType;

    /**
     * Set of listener interested on Service event : lost, discovery.
     * Set of object implementing {@link ServiceEventListener} interface
     */
    private java.util.Set<ServiceEventListener> listenerSet = new java.util.HashSet<ServiceEventListener>();

    /**
     * Set of discovered services.
     * Set of {@link BrowseForService.ServiceBrowse} object
     */
    private java.util.Set<ServiceBrowse> serviceSet = new java.util.HashSet<ServiceBrowse>();

    /**
     * Create a new instance of BrowseForService. It will be used to browse
     * registered on a particular type.
     * 
     * @param regType
     *            type of registration
     */
    public BrowseForService(String regType) {
        this.regType = regType;
    }

    /**
     * @param l
     *            listener interested in service event
     */
    public void addListener(ServiceEventListener l) {
        listenerSet.add(l);
    }

    /**
     * @param l
     *            listener no more interested in service event
     */
    public void removeListener(ServiceEventListener l) {
        listenerSet.remove(l);
    }

    /**
     * Call the listeners with a new service event. The new service is built on
     * the parameter value.
     * 
     * @param s
     *            service object containing the data about the service more or
     *            less information according to the kind of event.
     * @param event
     *            identify the kind of event : lost, found (used
     *            ServiceEvent.LOST and ServiceEvent.FOUND)
     * @see ServiceEvent
     */
    protected void eventOccured(Service s, int event) {
        ServiceEvent e = new ServiceEvent(s, event);
        java.util.Iterator<ServiceEventListener> it = listenerSet.iterator();
        while (it.hasNext()) {            
            it.next().serviceEventReceived(e);
        }
    }

    /**
     * Start to receive information about the available services, and to
     * generate service events
     */
    public void startBrowse() {
        try {
            dnssdService = DNSSD.browse(0, 0, regType, null, this);
        } catch (DNSSDException e) {
            System.err.println("Error in StartBrowse");
            e.printStackTrace();
        }
    }

    /**
     * Search among the existing services known a service associated to a
     * specific DNSSDService objet.
     * 
     * @param dnssdService
     *            the connection to DNS-SD
     * @return a ServiceBrowse object if a service is found, or null otherwise
     */
    private ServiceBrowse findSearchService(DNSSDService dnssdService) {
        synchronized (serviceSet) {
            java.util.Iterator<ServiceBrowse> it = serviceSet.iterator();
            while (it.hasNext()) {
                ServiceBrowse sb = it.next();
                if (sb.dnssdService == dnssdService)
                    return sb;
            }
            return null;
        }
    }

    /**
     * Implementation for BrowseListener method A new service is present, the
     * service is resolved
     * 
     * @see BrowseListener
     */
    public void serviceFound(DNSSDService browser, int flags, int ifIndex,
            String serviceName, String regType, String domain) {
        synchronized (serviceSet) {
            try {
                ServiceBrowse sb = new ServiceBrowse();
                sb.service = new Service(serviceName, regType, domain);
                sb.dnssdService = DNSSD.resolve(0, ifIndex, serviceName,
                        regType, domain, this);
                serviceSet.add(sb);
            } catch (DNSSDException e) {
                System.err.println("Error in serviceFound");
                e.printStackTrace();
            }
        }
    }

    /**
     * Implementation for BrowseListener method Signal that a service was lost
     * 
     * @see BrowseListener
     */
    public void serviceLost(DNSSDService browser, int flags, int ifIndex,
            String serviceName, String regType, String domain) {
        //System.out.println("Service lost :" + serviceName);
        eventOccured(new Service(serviceName, regType, domain),
                ServiceEvent.LOST);
    }

    /**
     * Implementation for BrowseListener, ResolveListener method
     * 
     * @see BrowseListener
     * @see ResolveListener
     */
    public void operationFailed(DNSSDService service, int errorCode) {
        System.out.println("BrowseForService : operation failed (" + errorCode
                + ")");
    }

    /**
     * Implementation for ResolveListener method
     * 
     * Complete service information. Signal that a service was found. Remove the
     * ServiceBrowse object
     * 
     * @see ResolveListener
     */
    public void serviceResolved(DNSSDService resolver, int flags, int ifIndex,
            String fullName, String hostName, int port, TXTRecord txtRecord) {
        synchronized (serviceSet) {
            ServiceBrowse sb = findSearchService(resolver);
            if (sb != null) {
                Service service = sb.service;
                service.hostName = hostName;
                service.port = port;
                service.txtRecord = txtRecord;

                eventOccured(service, ServiceEvent.FOUND);

                sb.dnssdService.stop();
                sb.dnssdService = null;
                serviceSet.remove(sb);
            }
        }
    }

    /**
     * Main for test Display the service that are discovered, and lost. Browse
     * the service registered on _bip._tcp
     */
    public static void main(String[] arg) {

        BrowseForService browse = new BrowseForService("_bip._tcp");

        // the listener who displays the service lost and discovered
        browse.addListener(new ServiceEventListener() {
            public void serviceEventReceived(ServiceEvent e) {
                if (e.isLost())
                    System.out.println(e.getService().fullName + " is lost");
                else
                    System.out.println(e.getService().fullName + " is found");
            }
        });

        // begin to browse
        browse.startBrowse();

        // browse execute itself
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }
}
