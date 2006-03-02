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
public class BipServiceWaiter implements ServiceEventListener {

    private ServiceBrowser serviceBrowser;
    private String prefixForServiceName;
    private BipServiceFilter bipServiceFilter;
    private BipService foundService = null;
    
    public BipServiceWaiter(String prefixForServiceName, BipServiceFilter bipServiceFilter) {
        this.prefixForServiceName = prefixForServiceName;
        this.bipServiceFilter = bipServiceFilter;
    }

    public void startSearch() {
        this.serviceBrowser = BipService.dnssdFactory.createServiceBrowser(BipService.REG_TYPE);
        this.serviceBrowser.addListener(this);
        this.serviceBrowser.start();
    }

    public boolean isResolved() {
        return foundService != null;
    }

    public BipService getBipService() {
        return foundService;
    }

    public void serviceEventReceived(ServiceEvent e) {
        if (e.isFound() && !isResolved()) {
            if (e.getServiceInformation().getFullName().startsWith(prefixForServiceName)) {
                BipService bipService = new BipService(e.getServiceInformation());
                if (bipServiceFilter==null || bipServiceFilter.isAGoodService(bipService)) {
                    foundService = bipService;
                    serviceBrowser.stop();
                }
            }
        }
    }
    


    
    
}
