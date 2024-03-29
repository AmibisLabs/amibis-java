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

package fr.prima.omiscid.user.service.impl;

import fr.prima.omiscid.user.service.ServiceFilter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import fr.prima.omiscid.control.OmiscidService;
import fr.prima.omiscid.dnssd.interf.ServiceBrowser;
import fr.prima.omiscid.dnssd.interf.ServiceEvent;
import fr.prima.omiscid.dnssd.interf.ServiceEventListener;
import fr.prima.omiscid.user.exception.ServiceRepositoryListenerAlreadyPresent;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.service.ServiceRepository;
import fr.prima.omiscid.user.service.ServiceRepositoryListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class ServiceRepositoryImpl implements ServiceRepository {
    
    private final Set<ServiceProxy> services = new HashSet<ServiceProxy>();
    private final Map<ServiceRepositoryListener, ServiceFilter> serviceRepositoryListeners = new HashMap<ServiceRepositoryListener, ServiceFilter>();
    private final Map<ServiceRepositoryListener, List<Integer>> serviceRepositoryAddedServices = new Hashtable<ServiceRepositoryListener, List<Integer>>();
    private ServiceImpl service;
    private ServiceBrowser serviceBrowser;
    private boolean stopped = false;

    /*diggable*/ ServiceRepositoryImpl(ServiceImpl service) {
        this(service, OmiscidService.REG_TYPE());
    }
    public ServiceRepositoryImpl(ServiceImpl service, String regType) {
        this.service = service;
        serviceBrowser = OmiscidService.dnssdFactory.createServiceBrowser(regType);
        serviceBrowser.addListener(new ServiceEventListener() {
            public void serviceEventReceived(ServiceEvent e) {
                if (e.isFound()) serviceFound(e); else serviceLost(e);
            }
        });
        serviceBrowser.start();
    }
    
    public synchronized void stop() {
        if (stopped) {
            return;
        }
        serviceBrowser.stop();
        serviceRepositoryListeners.clear();
        serviceRepositoryAddedServices.clear();
        services.clear();
        stopped = true;
    }
    private void checkRunning() {
        if (stopped) {
            throw new RuntimeException("ServiceRepository used after being stopped");
        }
    }
    
    public synchronized List<ServiceProxy> getAllServices() {
        checkRunning();
        Vector<ServiceProxy> res = new Vector<ServiceProxy>();
        res.addAll(services);
        return res;
    }

    private void added(ServiceRepositoryListener listener, ServiceProxy serviceProxy) {
        try {
            ServiceFilter filter = serviceRepositoryListeners.get(listener);
            if (filter == null) {
                listener.serviceAdded(serviceProxy);
            } else if (filter.acceptService(serviceProxy)) {
                serviceRepositoryAddedServices.get(listener).add(serviceProxy.getPeerId());
                listener.serviceAdded(serviceProxy);
            }
        } catch (Exception e) {
            System.err.println("Omiscid caught an exception thrown by a listener on addition in a service repository, it is shown here:");
            e.printStackTrace();
        }
    }
    private void removed(ServiceRepositoryListener listener, ServiceProxy serviceProxy) {
        try {
            List<Integer> addedServices = serviceRepositoryAddedServices.get(listener);
            if (addedServices == null || addedServices.contains(serviceProxy.getPeerId())) {
                listener.serviceRemoved(serviceProxy);
            }
        } catch (Exception e) {
            System.err.println("Omiscid caught an exception thrown by a listener on removal from a service repository, it is shown here:");
            e.printStackTrace();
        }
    }


    private synchronized void serviceFound(ServiceEvent e) {
        checkRunning();
        // This should be different in a real integration or layering to omiscid
        ServiceProxy serviceProxy = ServiceProxyImpl.forService(service, new OmiscidService(((ServiceImpl)service).getPeerId(), e.getServiceInformation()));
        if (serviceProxy != null) {
            services.add(serviceProxy);
            for (ServiceRepositoryListener listener : new ArrayList<ServiceRepositoryListener>(serviceRepositoryListeners.keySet())) {
                added(listener, serviceProxy);
            }
        }
    }

    private synchronized void serviceLost(ServiceEvent e) {
        checkRunning();
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
            for (ServiceRepositoryListener listener : new ArrayList<ServiceRepositoryListener>(serviceRepositoryListeners.keySet())) {
                removed(listener, matching);
            }
        } else {
            // This happens when the ServiceProxy could not be built in #serviceFound
            // For example, when a service appears only for a short time,
            // it can be visible under dnssd and disappear before it could be queried for its description.
            // In such a case, it is not added to the repository by #serviceFound.
        }
    }
    
    public synchronized void addListener(ServiceRepositoryListener listener) {
        addListener(listener, false);
    }

    public synchronized void addListener(ServiceRepositoryListener listener, boolean notifyOnlyNewEvents) {
        addListener(listener, null, notifyOnlyNewEvents);
    }

    public synchronized void addListener(ServiceRepositoryListener listener, ServiceFilter filter) {
        addListener(listener, filter, false);
    }

    public synchronized void addListener(ServiceRepositoryListener listener, ServiceFilter filter, boolean notifyOnlyNewEvents) {
        checkRunning();
        if (serviceRepositoryListeners.containsKey(listener)) {
            throw new ServiceRepositoryListenerAlreadyPresent("Listener already added to this repository");
        }
        if (filter != null) {
            serviceRepositoryAddedServices.put(listener, new ArrayList<Integer>());
        }
        serviceRepositoryListeners.put(listener, filter);
        if (!notifyOnlyNewEvents) {
            for (ServiceProxy proxy : services) {
                added(listener, proxy);
            }
        }
    }


    public synchronized void removeListener(ServiceRepositoryListener listener) {
        removeListener(listener, false);
    }

    public synchronized void removeListener(ServiceRepositoryListener listener, boolean notifyAsIfExistingServicesDisappear) {
        checkRunning();
        boolean present = serviceRepositoryListeners.containsKey(listener);
        if (present && notifyAsIfExistingServicesDisappear) {
            for (ServiceProxy proxy : services) {
                removed(listener, proxy);
            }
        }
        serviceRepositoryListeners.remove(listener);
        serviceRepositoryAddedServices.remove(listener);
    }

}
