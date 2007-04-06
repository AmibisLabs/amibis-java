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
    private boolean stopped = false;

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
    
    public synchronized void stop() {
        if (stopped) {
            return;
        }
        serviceBrowser.stop();
        serviceRepositoryListeners.clear();
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



    private synchronized void serviceFound(ServiceEvent e) {
        checkRunning();
        // This should be different in a real integration or layering to omiscid
        ServiceProxy serviceProxy = ServiceProxyImpl.forService(service, new OmiscidService(((ServiceImpl)service).getPeerId(), e.getServiceInformation()));
        if (serviceProxy != null) {
            services.add(serviceProxy);
            for (ServiceRepositoryListener listener : serviceRepositoryListeners) {
                listener.serviceAdded(serviceProxy);
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
            for (ServiceRepositoryListener listener : serviceRepositoryListeners) {
                listener.serviceRemoved(matching);
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
        checkRunning();
        if (!notifyOnlyNewEvents) {
            for (ServiceProxy proxy : services) {
                listener.serviceAdded(proxy);
            }
        }
        serviceRepositoryListeners.add(listener);
    }

    public synchronized void removeListener(ServiceRepositoryListener listener) {
        removeListener(listener, false);
    }

    public synchronized void removeListener(ServiceRepositoryListener listener, boolean notifyAsIfExistingServicesDisappear) {
        checkRunning();
        boolean present = serviceRepositoryListeners.remove(listener);
        if (present && notifyAsIfExistingServicesDisappear) {
            for (ServiceProxy proxy : services) {
                listener.serviceRemoved(proxy);
            }
        }
    }

}
