package fr.prima.bipcontrol ;

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.TXTRecord;

/**
 * Register a Bip Service on DNS-SD; The service is registered with the type "_bip._tcp".  The service is automatically renamed, if a service with the same name already exists. The name obtained finally kept after the registration is available through this object (getRegisteredName()).
 * @author  Sebastien Pesnel
 * Refactoting Patrick Reignier
 * Adding the stop method to unregister the service on DSN SD (Patrick Reignier)
 */
public class RegisterBipService implements com.apple.dnssd.RegisterListener {
    /** Flag for the registration */
    private static final int FLAG = 0;
    /** IF_INDEX for the registration */
    private static final int IF_INDEX = 0;
    /** Type for the registration */
    private static final String REG_TYPE = BipService.REG_TYPE;
    /** Domain for the registration */
    private static final String DOMAIN = new String("local");
 
    /** name of the service */
    private String serviceName = null;
    
    /** name under which the service has been registered */
    private String serviceNameRegistered = null;
    
    /** Give if the service is registered */
    private boolean registered = false;
    
    /** text records (static properties given to DNS-SD)*/
    private TXTRecord txtRecord = new TXTRecord();
    
    /** Object given during the registration */
    private DNSSDRegistration dnssdRegistration = null;
    /** Object used to wait the answer of the registration */
    private Object registerEvent = new Object();

    
    /** Construct a new instance of RegisterBipService
     * @param name the name of the service (name use in the registration)*/
    public RegisterBipService(String name) {
        serviceName = name;
    }
    
    /** Add a property as a name 
     * @param name the name of the property */
    public void addProperty(String name) {
        txtRecord.set(name, (String) null);
    }
    /** Add a property to the text record as a couple name, value
     * @param name the name of the property to add
     * @param value the value of this property
     */
    public void addProperty(String name, String value) {
        txtRecord.set(name, value);
    }
    
    /** Ask for the registration, and wait for the result.
     * Register the service with the service name, the txtRecord build since the creation of this object
     * and the host name and port given as parameters.
     * @param port port number to give to DNS-SD 
     * @return if the registration is ok */
    public boolean register(int port) {
        synchronized (registerEvent) {
            registered = false;
            try {
                dnssdRegistration = DNSSD.register(FLAG, IF_INDEX, serviceName,
                        REG_TYPE, DOMAIN, null, port, txtRecord, this);
                registerEvent.wait();
                // System.err.println("out register");
            } catch (com.apple.dnssd.DNSSDException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return registered;
        }
    }
    
    /**
	 * Indicates if the registration is ok 
	 * @return  if the service is correctly registered
	 * @uml.property  name="registered"
	 */
    public boolean isRegistered() {
        return registered;
    }
    
    /** Access to the service name 
     * @return the service name */
    public String getName() {
        return serviceName;
    }
    
    /**
     * Unregisters the service on DNS-SD
     */
    public void stop() 
    {
    	dnssdRegistration.stop() ;
    }
    
    /** Access to the registration name.
     * (Only if the service is registered)
     * @return the name of the service in DNS-SD */
    public String getRegisteredName() {
        return serviceNameRegistered;
    }
    /**
	 * Enables to change the service name.  Must be used before registration : before a call to the method register
	 * @uml.property  name="serviceName"
	 */
    public void setServiceName(String serviceName){
        if(!isRegistered())
            this.serviceName = serviceName;
    }
    
    /** Method of RegisterListener Interface 
     * signal that the registration failed */
    public void operationFailed(DNSSDService service, int errorCode) {
        synchronized (registerEvent) {
            // System.err.println("operation failed");
            registered = false;
            registerEvent.notify();
        }
    }
    
    /** Method of RegisterListener Interface 
     * signal that the registration succeeded 
     * keep the registration name : the service name with may be a number */
    public void serviceRegistered(DNSSDRegistration registration, int flags,
            String serviceName, String regType, String domain) {
        synchronized (registerEvent) {
            if (registration == dnssdRegistration) {
                // System.err.println("service registered");
                registered = true;
                serviceNameRegistered = serviceName;
                registerEvent.notify();
            }
        }
    }
    
    /** main for test
     * @param arg not used*/
    public static void main(String[] arg) {
        RegisterBipService r = new RegisterBipService("toto");
        if (r.register(666)) {
            System.out.println("Service Registered");
        } else
            System.out.println("Service NO Registered !!");

        System.exit(0);
    }
}
