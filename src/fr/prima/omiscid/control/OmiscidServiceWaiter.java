/*
 * Created on Feb 13, 2006
 *
 */
package fr.prima.omiscid.control;

import fr.prima.omiscid.dnssd.interf.ServiceBrowser;
import fr.prima.omiscid.dnssd.interf.ServiceEvent;
import fr.prima.omiscid.dnssd.interf.ServiceEventListener;

/**
 * 
 * @author emonet
 *
 */
public class OmiscidServiceWaiter implements ServiceEventListener {

    private ServiceBrowser serviceBrowser;
    private String prefixForServiceName;
    private OmiscidServiceFilter omiscidServiceFilter;
    private OmiscidService foundService = null;
    
    public OmiscidServiceWaiter(String prefixForServiceName, OmiscidServiceFilter omiscidServiceFilter) {
        this.prefixForServiceName = prefixForServiceName;
        this.omiscidServiceFilter = omiscidServiceFilter;
    }

    public void startSearch() {
        this.serviceBrowser = OmiscidService.dnssdFactory.createServiceBrowser(OmiscidService.REG_TYPE);
        this.serviceBrowser.addListener(this);
        this.serviceBrowser.start();
    }

    public boolean isResolved() {
        return foundService != null;
    }

    public OmiscidService getOmiscidService() {
        return foundService;
    }

    public void serviceEventReceived(ServiceEvent e) {
        if (e.isFound() && !isResolved()) {
            if (e.getServiceInformation().getFullName().startsWith(prefixForServiceName)) {
                OmiscidService omiscidService = new OmiscidService(e.getServiceInformation());
                if (omiscidServiceFilter==null || omiscidServiceFilter.isAGoodService(omiscidService)) {
                    foundService = omiscidService;
                    serviceBrowser.stop();
                }
            }
        }
    }
    


    
    
}
