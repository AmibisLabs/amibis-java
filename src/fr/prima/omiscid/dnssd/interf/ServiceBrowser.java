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

package fr.prima.omiscid.dnssd.interf;

/**
 * ServiceBrowser allows browsing for services registered under dnssd with a
 * given dnssd registration type.
 * 
 * @author emonet
 */
public interface ServiceBrowser {

    /**
     * Adds a listener to this browser. When a service event occurs, all
     * listeners are notified.
     * 
     * @param l
     *            listener interested in service event
     */
    void addListener(ServiceEventListener l);

    /**
     * Removes the given listener.
     * 
     * @param l
     *            listener no more interested in service event
     */
    void removeListener(ServiceEventListener l);

    /**
     * Starts the service browser. All the listeners will first be notified of
     * the declared services and then will start receiving service event
     * associated to registration/unregistration of services.
     */
    void start();

    /**
     * Stops the service browse ; listeners won't be notified any more (but are
     * still listeners).
     */
    void stop();
}
