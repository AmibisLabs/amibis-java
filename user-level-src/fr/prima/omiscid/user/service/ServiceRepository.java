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

package fr.prima.omiscid.user.service;

import java.util.List;

/**
 * Represents a service repository containing an up-to-date list of running services.
 * Concrete ServiceRepository instances should be obtained from {@link ServiceFactory#createServiceRepository(Service)}.
 * A service repository basic use case is to register some listeners to listen for appearing and disappearing services.
 * A service repository can also be queried for a list of running services (a snapshot taken when the {@link #getAllServices()} method is called.
 * 
 * For a given service appearing and then disappearing, the parameter passed to {@link ServiceRepositoryListener#serviceAdded(ServiceProxy)} and {@link ServiceRepositoryListener#serviceRemoved(ServiceProxy)} methods will be the same object.
 *
 */
public interface ServiceRepository {

    /**
     * Adds a listener to be notified of service apparition and disapparition.
     * This listener will be notified of currently running services (see {@link #addListener(ServiceRepositoryListener, boolean)} for details).
     * Behaves like calling {@link #addListener(ServiceRepositoryListener, boolean)} with false.
     * 
     * @param listener the listener that will be notified of service events
     */
    public void addListener(ServiceRepositoryListener listener);
    
    /**
     * Adds a listener to be notified of service apparition and disapparition.
     * From this addition, the listener will be notified of each service event (see {@link ServiceRepositoryListener}).
     * When a listener is added to the repository, this latter can already contain some services.
     * The boolean parameter tells whether the listener should be notified of these services as if they were appearing.
     * 
     * @param listener the listener that will be notified of service events
     * @param notifyOnlyNewEvents a boolean used to ignore services running at the time of listener addition. Setting it to false will cause the listener to be immediately notified of services already present in the repository
     */
    public void addListener(ServiceRepositoryListener listener, boolean notifyOnlyNewEvents);
    
    /**
     * Removes a previously added listener.
     * Behaves like calling {@link #removeListener(ServiceRepositoryListener, boolean)} with false.
     * 
     * @param listener the listener to be removed
     */
    public void removeListener(ServiceRepositoryListener listener);
    
    /**
     * Removes a previously added listener.
     * When a listener is removed from the repository, this latter can still contain some services.
     * The boolean parameter tells whether the listener should be notified of these services as if they were disappearing. 
     * 
     * @param listener the listener to be removed
     * @param notifyAsIfExistingServicesDisappear a boolean used to notify the removed listener as if currently running services have been stopped. Setting it to false will cause the listener <b>not</b> to be notified.
     */
    public void removeListener(ServiceRepositoryListener listener, boolean notifyAsIfExistingServicesDisappear);
    
    /**
     * Takes a snapshot of currently running services.
     * 
     * @return a {@link ServiceProxy} list of currently running services. This list is a freely usable by the caller.
     */
    public List<ServiceProxy> getAllServices();
    
    /**
     * Stops the service repository.
     * No more notifications will be fired by this repository after this call.
     *
     */
    public void stop();
    
}
