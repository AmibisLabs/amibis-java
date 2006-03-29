/*
 * Created on Mar 29, 2006
 *
 */
package fr.prima.omiscid.control;

import java.util.List;
import java.util.Vector;

import fr.prima.omiscid.dnssd.interf.ServiceBrowser;
import fr.prima.omiscid.dnssd.interf.ServiceEvent;
import fr.prima.omiscid.dnssd.interf.ServiceEventListener;

public class OmiscidServicesRepository implements ServiceEventListener {

    private final Vector<OmiscidService> servicesList = new Vector<OmiscidService>();
    private final ServiceBrowser serviceBrowser = OmiscidService.dnssdFactory.createServiceBrowser(OmiscidService.REG_TYPE);
    private int serviceId = OmiscidService.generateServiceId();
    
    /**
     * creates and start a new OMiSCID services repository.
     *
     */
    public OmiscidServicesRepository() {
        serviceBrowser.addListener(this);
        serviceBrowser.start();
    }

    /**
     * Creates and start a new OMiSCID services repository. Also wait for the given delay before returning.
     * The delay could be useful to let the repository be filled by the dnssd service browser before use.
     *
     */
    public OmiscidServicesRepository(long sleepTime) {
        this();
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        }
    }
    
    public synchronized void serviceEventReceived(ServiceEvent e) {
        System.out.println(e.getServiceInformation().getFullName());
        if (e.isFound()) {
            servicesList.add(new OmiscidService(serviceId, e.getServiceInformation()));
        } else if (e.isLost()) {
            OmiscidService toRemove = null;
            for (OmiscidService service : servicesList) {
                if (e.getServiceInformation().getFullName().equals(service.getFullName())) {
                    toRemove = service;
                    break;
                }
            }
            if (toRemove!=null) {
                servicesList.remove(toRemove);
            }
        }
    }
    
    /**
     * 
     * @param filter
     * @param into a list to add the matching services into.
     * @return the number of matching services
     */
    public synchronized int getMatchingServices(OmiscidServiceFilter filter, List<OmiscidService> into) {
        int res = 0;
        for (OmiscidService service : servicesList) {
            if (filter.isAGoodService(service)) {
                into.add(service);
                res++;
            }
        }
        return res;
    }
    
    /**
     * 
     * @param filter
     * @return the number of matching services
     */
    public synchronized Vector<OmiscidService> getMatchingServices(OmiscidServiceFilter filter) {
        Vector<OmiscidService> res = new Vector<OmiscidService>();
        getMatchingServices(filter, res);
        return res;
    }

    /**
     * 
     * @return a copy of the current vector containing all the services.
     */
    public synchronized Vector<OmiscidService> getAllServices() {
        Vector<OmiscidService> res = new Vector<OmiscidService>(servicesList);
        return res;
    }

    /**
     * Gets the omiscid service id used by this repository when creating omiscid services.
     * This id is for example used by filters when accessing control port of other services.
     */
    public int getServiceId() {
        return serviceId;
    }

    /**
     * Sets the omiscid service id used by this repository when creating omiscid services.
     * This id is for example used by filters when accessing control port of other services.
     * 
     * @param serviceId
     */
    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }
    
}
