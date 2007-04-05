/*
 * Created on 5 avr. 07
 *
 */
package fr.prima.omiscid.user.service;

import java.util.List;

public interface ServiceRepository {

    // defaults to false
    void addListener(ServiceRepositoryListener listener);
    void addListener(ServiceRepositoryListener listener, boolean notifyOnlyNewEvents);
    
    void removeListener(ServiceRepositoryListener listener);
    void removeListener(ServiceRepositoryListener listener, boolean notifyAsIfExistingServicesDisappear);
    
    List<ServiceProxy> getAllServices();
    void stop();
    
}
