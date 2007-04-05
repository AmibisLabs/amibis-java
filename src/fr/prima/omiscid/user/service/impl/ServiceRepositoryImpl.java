/**
 *
 * Sofware written by Remi Emonet <remi.emonet@inria.fr>.
 * 
 */

package fr.prima.omiscid.user.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import fr.prima.omiscid.control.OmiscidService;
import fr.prima.omiscid.dnssd.interf.ServiceBrowser;
import fr.prima.omiscid.dnssd.interf.ServiceEvent;
import fr.prima.omiscid.dnssd.interf.ServiceEventListener;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.service.ServiceRepository;
import fr.prima.omiscid.user.service.ServiceRepositoryListener;

public class ServiceRepositoryImpl implements ServiceRepository {
    
    private final Set<ServiceProxy> services = new HashSet<ServiceProxy>();
    private final Vector<ServiceRepositoryListener> serviceRepositoryListeners = new Vector<ServiceRepositoryListener>();
    private ServiceImpl service;
    private ServiceBrowser serviceBrowser;

    public ServiceRepositoryImpl(ServiceImpl service) {
        this.service = service;
        serviceBrowser = OmiscidService.dnssdFactory.createServiceBrowser(OmiscidService.REG_TYPE());
        serviceBrowser.addListener(new ServiceEventListener() {
            public void serviceEventReceived(ServiceEvent e) {
                if (e.isFound()) serviceFound(e); else serviceLost(e);
            }
        });
        serviceBrowser.start();
    }
    
    public void stop() {
        serviceBrowser.stop();
    }
    
    public synchronized List<ServiceProxy> getAllServices() {
        Vector<ServiceProxy> res = new Vector<ServiceProxy>();
        res.addAll(services);
        return res;
    }



    private synchronized void serviceFound(ServiceEvent e) {
        // This should be different in a real integration or layering to omiscid
        ServiceProxy serviceProxy = ServiceProxyImpl.forService(service, new OmiscidService(((ServiceImpl)service).getPeerId(), e.getServiceInformation()));
        services.add(serviceProxy);
        for (ServiceRepositoryListener listener : serviceRepositoryListeners) {
            listener.serviceAdded(serviceProxy);
        }
    }

    private synchronized void serviceLost(ServiceEvent e) {
        int peerId = new OmiscidService(e.getServiceInformation()).getRemotePeerId();
        ServiceProxy matching = null;
        for (ServiceProxy proxy : services) {
            if (proxy.getPeerId() == peerId) {
                matching = proxy;
                break;
            }
        }
        if (matching != null) {
            services.remove(matching);
            for (ServiceRepositoryListener listener : serviceRepositoryListeners) {
                listener.serviceRemoved(matching);
            }
        } else {
            // Should not happen
            System.err.println("PeerId not found in ServiceRepository");
        }
    }
    
    public void addListener(ServiceRepositoryListener listener) {
        addListener(listener, false);
    }

    public void addListener(ServiceRepositoryListener listener, boolean notifyOnlyNewEvents) {
        if (!notifyOnlyNewEvents) {
            for (ServiceProxy proxy : services) {
                listener.serviceAdded(proxy);
            }
        }
        serviceRepositoryListeners.add(listener);
    }

    public void removeListener(ServiceRepositoryListener listener) {
        removeListener(listener, false);
    }

    public void removeListener(ServiceRepositoryListener listener, boolean notifyAsIfExistingServicesDisappear) {
        boolean present = serviceRepositoryListeners.remove(listener);
        if (present && notifyAsIfExistingServicesDisappear) {
            for (ServiceProxy proxy : services) {
                listener.serviceRemoved(proxy);
            }
        }
    }

}
