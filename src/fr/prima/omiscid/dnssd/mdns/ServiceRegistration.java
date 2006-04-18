/*
 * Created on Feb 14, 2006
 *
 */
package fr.prima.omiscid.dnssd.mdns;

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.RegisterListener;
import com.apple.dnssd.TXTRecord;

/**
 * 
 * @author emonet initial build from RegisterOmiscidService by pesnel and reignier
 *
 */
public class ServiceRegistration
implements RegisterListener,
fr.prima.omiscid.dnssd.interf.ServiceRegistration {

    /** Flag for the registration */
    private static final int FLAG = 0;
    /** IF_INDEX for the registration */
    private static final int IF_INDEX = 0;
    /** Domain for the registration */
    private static final String DOMAIN = new String("local");

    
    private String serviceName;
    private String registrationType;
    private DNSSDRegistration dnssdRegistration;
    private String registeredServiceName;
    private boolean registered;
    private TXTRecord txtRecord = new TXTRecord();
    
    /*package*/ ServiceRegistration(String serviceName, String registrationType) {
        this.serviceName = serviceName;
        this.registrationType = registrationType;
    }

    public synchronized void operationFailed(DNSSDService service, int errorCode) {
//        synchronized (registerEvent) {
//            // System.err.println("operation failed");
        registered = false;
        this.notify();
//        }
    }
    
    public synchronized void serviceRegistered(DNSSDRegistration registration, int flags,
            String serviceName, String regType, String domain) {
        if (registration != dnssdRegistration) {
//            System.out.println(registration);
//            System.out.println(dnssdRegistration);
        }
        registered = true;
        registeredServiceName = serviceName;
        this.notify();
    }
    
    public void addProperty(String name, String value) {
        txtRecord.set(name, value);
    }

    public void setName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getName() {
        return serviceName;
    }

    public synchronized boolean register(int port) {
        registered = false;
        try {
            dnssdRegistration = DNSSD.register(FLAG, IF_INDEX, serviceName,
                    registrationType, DOMAIN, null, port, txtRecord, this);
            this.wait();
        } catch (com.apple.dnssd.DNSSDException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return registered;
    }

    public synchronized boolean isRegistered() {
        return registered;
    }

    public synchronized void unregister() {
        dnssdRegistration.stop();
    }

    public synchronized String getRegisteredName() {
        return registeredServiceName;
    }

}
