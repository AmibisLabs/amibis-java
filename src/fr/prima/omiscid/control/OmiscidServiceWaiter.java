/*
 * Created on Feb 13, 2006
 *
 */
package fr.prima.omiscid.control;

import fr.prima.omiscid.com.BipUtils;
import fr.prima.omiscid.control.filter.OmiscidServiceFilter;
import fr.prima.omiscid.control.filter.OmiscidServiceFilters;
import fr.prima.omiscid.dnssd.interf.ServiceBrowser;
import fr.prima.omiscid.dnssd.interf.ServiceEvent;
import fr.prima.omiscid.dnssd.interf.ServiceEventListener;

/**
 * Waits for the presence of a service matching a given filter.
 */
public class OmiscidServiceWaiter implements ServiceEventListener {

    private ServiceBrowser serviceBrowser;

    private OmiscidServiceFilter omiscidServiceFilter;

    private OmiscidService foundService = null;

    /**
     * The BIP peer id used to describe the local peer.
     */
    private int peerId;

    /**
     * Creates a service waiter. Once started ({@link #startSearch()}), it
     * will wait for a service having its base name matching the given regular
     * expression ({@link OmiscidServiceFilters#nameIs(String)}), and matching
     * the given service filter ({@link OmiscidServiceFilter}). The peer id
     * provided as a parameter will be propagated to the created services. If
     * your application has no BIP peer id, {@link BipUtils#generateBIPPeerId()}
     * can be used to get one.
     *
     * @param omiscidServiceFilter
     * @param localPeerId
     *            the BIP peer id of the local peer
     */
    public OmiscidServiceWaiter(OmiscidServiceFilter omiscidServiceFilter, int localPeerId) {
        this.omiscidServiceFilter = omiscidServiceFilter;
        this.peerId = localPeerId;
    }

    /**
     * Starts the search for a matching service. The status of the search
     * process is given by {@link #isResolved()}. Once the search is complete,
     * the search is automatically stopped and the service found can be accessed
     * using {@link #getOmiscidService()}.
     */
    public void startSearch() {
        this.serviceBrowser = OmiscidService.dnssdFactory.createServiceBrowser(OmiscidService.REG_TYPE());
        this.serviceBrowser.addListener(this);
        this.serviceBrowser.start();
    }

    /**
     * Tests whether a service matching the given filter has been found.
     *
     * @return whether a service has been found
     */
    public synchronized boolean isResolved() {
        return foundService != null;
    }

    /**
     * Waits for up to the given timeout for {@link #isResolved()} to become
     * true.
     *
     * @param timeout
     *            maximum time in milliseconds to wait before returning
     * @return the value returned by {@link #isResolved()}
     */
    public synchronized boolean waitForResolution(long timeout) {
        if (isResolved()) {
            return true;
        } else {
            try {
                this.wait(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return isResolved();
        }
    }

    /**
     * Accesses the service found.
     *
     * @return the service found by the search process if {@link #isResolved()},
     *         null otherwise
     */
    public synchronized OmiscidService getOmiscidService() {
        return foundService;
    }

    public synchronized void serviceEventReceived(ServiceEvent e) {
        if (e.isFound() && !isResolved()) {
            OmiscidService omiscidService = new OmiscidService(peerId, e.getServiceInformation());
            if (omiscidServiceFilter == null || omiscidServiceFilter.isAGoodService(omiscidService)) {
                foundService = omiscidService;
                serviceBrowser.stop();
                this.notify();
            }
        }
    }
}
