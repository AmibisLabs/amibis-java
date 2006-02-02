/*
 * Created on 6 juin 2005
 *
 */
package fr.prima.bipcontrol ;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.ResolveListener;
import com.apple.dnssd.TXTRecord;
import java.util.LinkedList;

//TODO check if no conflict during research when a service is disocvered if a service is already being resolved.
/**
 * Wait for a BIP service with a particular name. It can test other properties by implementing the interface  {@link fr.prima.bipcontrol.WaitForTheGoodBipService} . When a service is found with a good name, and it is  resolved then the method  {@link fr.prima.bipcontrol.WaitForTheGoodBipService#isAGoodService(BipService)}  of the object implementing  the WaitForTheGoodBipService is called on the data about this service. The service is kept or not according to the result of this method. <br> Use : <ul> <li>Create a new instance of the object SearchBipService, with the name to find. </li> <li>Start the research :  {@link SearchBipService#startSearch(WaitForTheGoodBipService)} </li> <li>wait for the service : the service is found when  {@link SearchBipService#isResolved()}  returns true. </li> </ul>   <br> Note : This class is used by  {@link fr.prima.bipcontrol.WaitForBipServices}   that enables to launch several service research, and to wait that they are all found.
 * @author  Sebastien Pesnel  Refactoring by Patrick Reignier
 */
public class SearchBipService implements ResolveListener, BrowseListener {
    /** The registration type for the service BIP */
    private static final String BIP_REG_TYPE = BipService.REG_TYPE;

    /**
	 * @author  reignier
	 */
    static class CoupleDnssdService{
        public BipService service;
        public DNSSDService dnssdService;
    }
    
    /** The name to search for the service */
    private String searchedName = null;
    /** Indicates if the wanted service has been found or not */
    private boolean resolved = false;

 
    /** The object to use to test the compabiltity of a discovered service
     * with a good name and the user wishes. Can be null if the test on the name
     * is sufficient. */
    public WaitForTheGoodBipService waitForTheGoodBipService = null;
    
    public LinkedList<CoupleDnssdService> liste = new LinkedList<CoupleDnssdService>();
    public BipService theService = null;
//    /** The service found service. 
//     * Available when the service has been found (resolved = true ) */
//    public BipService service = null;
//    /** Instance for the browsing */
//    public DNSSDService dnssdService = null;
 
    /** Create a new instance of SearchBipService 
     * @param aName the name to search for the service */
    SearchBipService(String aName) {
        searchedName = aName;
    }
    
    /**
     * Start to search the wanted service. 
     * Begins to browse for BIP services.
     * @param w Object used for test the service validity 
     * according to the wishes of the user. (Can be null)
     */
    public void startSearch(WaitForTheGoodBipService w) {
        waitForTheGoodBipService = w;
        try {
            DNSSD.browse(0, 0, BIP_REG_TYPE, null, this);
        } catch (DNSSDException e) {
            System.out.println("Error in SearchBipService::startSearch");
            System.out.println(e);
        }
    }
    /**
	 * Search finished ?
	 * @return  if the service has been found or not. 
	 * @see  SearchBipService#resolved
	 * @uml.property  name="resolved"
	 */
    public boolean isResolved() {
        return resolved;
    }

    
    /** Method of BrowseListener interface
     * If a new discovered service as the good name, 
     * it asked to DNS-SD to resolve the service.
     * the parameter {@link SearchBipService#theService} is instantiate
     * with the first available information
     */
    public void serviceFound(DNSSDService browser, int flags, int ifIndex,
            String serviceName, String regType, String domain) {
        if (serviceName.startsWith(searchedName)) {
            // System.out.println("Service found :"+serviceName);
            try {
                synchronized (liste) {
                    CoupleDnssdService c = new CoupleDnssdService();
                    c.service = new BipService(serviceName, regType, domain);
                    c.dnssdService = DNSSD.resolve(0, ifIndex, serviceName, regType,
                            domain, this);
                    
                    liste.add(c);
                }
            } catch (DNSSDException e) {
                System.out.println("Error in serviceFound");
                System.out.println(e);
            }
        }
    }
    /** Method of BrowseListener interface*/
    public void serviceLost(DNSSDService browser, int flags, int ifIndex,
            String serviceName, String regType, String domain) {
    }
    /** Method of BrowseListener and ResolveListener interfaces*/
    public void operationFailed(DNSSDService service, int errorCode) {
        System.err.println("SearchServices : operation failed (" + errorCode
                + ")");
        // TODO operationFailed y faire qqchose
    }
    
    private  CoupleDnssdService findCouple(DNSSDService ds){
        synchronized (liste) {
            java.util.Iterator<CoupleDnssdService> it = liste.iterator();
            while(it.hasNext()){
                CoupleDnssdService cds = it.next();
                if(cds.dnssdService == ds) return cds;
            }
            return null;
        }        
    }
    
    /** Method of ResolveListener interface
     * Extract the data about a service with a correct name.
     * If an object {@link fr.prima.bipcontrol.WaitForTheGoodBipService} has been done, the service is tested with
     * the methods of this object.
     * The service is considered as found if this test is ok or if there is no object.
     * Then the search is finished, the browsing is stopped, and {@link SearchBipService#resolved} is set to true.
     */
    public void serviceResolved(DNSSDService resolver, int flags, int ifIndex,
            String fullName, String hostName, int port, TXTRecord txtRecord) {
        // System.out.println("callback resolved");
        CoupleDnssdService cds = findCouple(resolver);
        if (cds != null) {            
            cds.service.hostName = hostName;
            cds.service.port = port;
            cds.service.txtRecord = txtRecord;

            if (waitForTheGoodBipService == null
                    || waitForTheGoodBipService.isAGoodService(cds.service)) {

                // System.out.println("callback resolved : "+ service.fullName+"
                // "+
                // "("+hostName+":"+port+")");
                
                cds.dnssdService.stop();
                cds.dnssdService = null;
                
                resolved(cds.service);
            } else {
                cds.service = null;
                cds.dnssdService.stop();
                cds.dnssdService = null;
                
                synchronized (liste) {
                    liste.remove(cds);
                }
            }

        }
    }
    
    private void resolved(BipService s){
        theService = s;
        resolved = true;
        synchronized (liste) {
            java.util.Iterator<CoupleDnssdService> it = liste.iterator();
            while(it.hasNext()){
                CoupleDnssdService cds = it.next();
                if(cds.dnssdService != null) cds.dnssdService.stop();
            }
            liste.clear();
        }
    }
}
