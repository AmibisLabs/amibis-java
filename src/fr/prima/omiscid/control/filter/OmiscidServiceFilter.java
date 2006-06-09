package fr.prima.omiscid.control.filter;

import fr.prima.omiscid.control.OmiscidService;

/**
 * Interface to implement to test whether a service has the desired properties.
 * When the user wait for a service the method isAGoodService is called on each
 * service with the name specified by the user, to check other properties: it
 * enabled to connect to the control server to check the existence of particular
 * output, the values of some variables, ...
 * 
 * @author Sebastien Pesnel refactoring emonet
 */
public interface OmiscidServiceFilter {
    /**
     * Tells whether a service is matching the filter.
     * 
     * @param s
     *            the data about the service providing by DNS-SD
     * @return whether the service matches the filter requirements
     */
    public boolean isAGoodService(OmiscidService s);
}
