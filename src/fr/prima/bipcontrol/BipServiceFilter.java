package fr.prima.bipcontrol ;

/** Interface to implement to test if a service has the good properties.
 * When the user wait for a service the method isAGoodService is called on each service
 * with the name whished by the user, to check other properties : it enabled to connect to the control server
 * to check the existence of particular output, the values of some variables, ... 
 *
 * @author Sebastien Pesnel refactoring emonet
 */
public interface BipServiceFilter {
    /** Call on each service with a good name
     * @param s the data about the service providing by DNS-SD
     * @return true is this service is ok according to the wishes of the user,
     * false otherwise, then the process will wait for another service.
     */
    public boolean isAGoodService(BipService s);
}
