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
