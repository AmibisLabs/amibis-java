/*
 * Created on 5 avr. 07
 *
 */
package fr.prima.omiscid.user.service;


public interface ServiceRepositoryListener {

    void serviceAdded(ServiceProxy serviceProxy);
    void serviceRemoved(ServiceProxy serviceProxy);
    
}
