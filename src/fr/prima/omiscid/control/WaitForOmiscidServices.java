/*
 * Created on 6 juin 2005
 * TODO wait for 2 different instance of a service
 */
package fr.prima.omiscid.control;

import java.util.Vector;

import fr.prima.omiscid.com.BipUtils;
import fr.prima.omiscid.control.filter.OmiscidServiceFilter;

/**
 * Waits for several OMiSCID services at the same time. Allows to search in the
 * same time several OMiSCID services matching the specified services. Allows to
 * wait that they are all found.
 *
 * @author Sebastien Pesnel refactoring emonet
 */
public class WaitForOmiscidServices {
    /**
     * Services we are searching for.
     */
    private final Vector<OmiscidServiceWaiter> searchServiceArray = new Vector<OmiscidServiceWaiter>();

    /**
     * The BIP peer id used to describe the local peer.
     */
    private int peerId;

    /**
     * Creates a WaitForOmiscidServices using the given local peer id. If you
     * application has already a BIP peer id, you can pass it to this
     * constructor to have it used through all subsequent BIP connections. BIP
     * peer ids can be generated using {@link BipUtils#generateBIPPeerId()}.
     *
     * @param peerId
     *            the peer id representing the local BIP peer
     */
    public WaitForOmiscidServices(int peerId) {
        this.peerId = peerId;
    }

    /**
     * Adds a new service to the required services.
     *
     * @param name
     *            the name of the wanted service
     * @param filter
     *            an {@link OmiscidServiceFilter} representing the acceptance
     *            test for the service
     * @return the index to retrieve the wanted service or to know if it has
     *         been found
     */
    public synchronized int needService(OmiscidServiceFilter filter) {
        searchServiceArray.add(new OmiscidServiceWaiter(filter, peerId));
        searchServiceArray.lastElement().startSearch();
        return searchServiceArray.size() - 1;
    }

    /**
     * Tests whether all the required services have been found.
     *
     * @return whether all the required services have been found
     */
    private synchronized boolean areAllResolved() {
        for (OmiscidServiceWaiter waiter : searchServiceArray) {
            if (!waiter.isResolved()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Waits until all the required services have been found.
     */
    public synchronized void waitResolve() {
        while (!areAllResolved()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Waits until all the required services have been found.
     *
     * @return whether the resolution has been complete
     */
    public synchronized boolean waitResolve(long timeoutInMilliseconds) {
        long timeout = System.currentTimeMillis() + timeoutInMilliseconds;
        while (!areAllResolved() && System.currentTimeMillis() < timeout) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return areAllResolved();
    }

    /**
     * Tests whether a given awaited service has been found.
     *
     * @param index
     *            the index associated to the needed service (returned for
     *            example by {@link #needService(String, OmiscidServiceFilter)})
     * @return whether the awaited service has been found
     */
    public synchronized boolean isResolved(int index) {
        return searchServiceArray.get(index).isResolved();
    }

    /**
     * Accesses the found service associated to the search given index.
     *
     * @param index
     *            the index associated to the required service
     * @return the required service or null if not found yet
     * @see WaitForOmiscidServices#needService(String)
     * @see WaitForOmiscidServices#isResolved(int)
     */
    public synchronized OmiscidService getService(int index) {
        if (searchServiceArray.get(index).isResolved()) {
            return searchServiceArray.get(index).getOmiscidService();
        } else {
            return null;
        }
    }
}
