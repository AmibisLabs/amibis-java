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

package fr.prima.omiscid.control;

import java.util.List;
import java.util.Vector;

import fr.prima.omiscid.com.BipUtils;
import fr.prima.omiscid.control.filter.OmiscidServiceFilter;
import fr.prima.omiscid.dnssd.interf.ServiceBrowser;
import fr.prima.omiscid.dnssd.interf.ServiceEvent;
import fr.prima.omiscid.dnssd.interf.ServiceEventListener;

/**
 * Represents a service repository keeping an up-to-date view of the running
 * services. This repository can be queried for the current list of services for
 * example via {@link #getAllServices()} or
 * {@link #getMatchingServices(OmiscidServiceFilter)}.
 */
public class OmiscidServicesRepository implements ServiceEventListener {

    private final Vector<OmiscidService> servicesList = new Vector<OmiscidService>();

    private final ServiceBrowser serviceBrowser = OmiscidService.dnssdFactory.createServiceBrowser(OmiscidService.REG_TYPE());

    private int peerId;

    /**
     * Creates and starts a new OMiSCID services repository. No BIP peer id is
     * specified and one is generated automatically. It can be accessed via
     * {@link #getPeerId()}.
     */
    public OmiscidServicesRepository() {
        this(BipUtils.generateBIPPeerId());
    }

    /**
     * Creates and starts a new OMiSCID services repository.
     *
     * @param peerId
     *            the peerId that this database will be using
     */
    public OmiscidServicesRepository(int peerId) {
        this.peerId = peerId;
        serviceBrowser.addListener(this);
        serviceBrowser.start();
    }

    /**
     * Creates and starts a new OMiSCID services repository. Also wait for the
     * given delay before returning. The delay could be useful to let the
     * repository be filled by the dnssd service browser before use.
     */
    //\REVIEWTASK close signatures warning
    public OmiscidServicesRepository(long sleepTime) {
        this();
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            // throw new RuntimeException(e);
        }
    }

    public synchronized void serviceEventReceived(ServiceEvent e) {
        if (e.isFound()) {
            servicesList.add(new OmiscidService(peerId, e.getServiceInformation()));
        } else if (e.isLost()) {
            OmiscidService toRemove = null;
            for (OmiscidService service : servicesList) {
                if (e.getServiceInformation().getFullName().equals(service.getFullName())) {
                    toRemove = service;
                    break;
                }
            }
            if (toRemove != null) {
                servicesList.remove(toRemove);
            }
        }
    }

    /**
     * Adds all services matching the given filter to the provided list.
     *
     * @param filter
     * @param into
     *            a list to add the matching services into
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
     * Creates a list containing all the services matching the provided filter.
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
     * Creates and returns a copy of the current services list.
     *
     * @return a modifiable copy of the current vector containing all the
     *         services
     */
    public synchronized Vector<OmiscidService> getAllServices() {
        Vector<OmiscidService> res = new Vector<OmiscidService>(servicesList);
        return res;
    }

    /**
     * Gets the peer id used by this repository when creating omiscid services.
     * This id is for example used by filters when accessing control port of
     * other services.
     */
    public int getPeerId() {
        return peerId;
    }
}
