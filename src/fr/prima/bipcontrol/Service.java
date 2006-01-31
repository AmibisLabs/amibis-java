/*
 * Created on 6 juin 2005
 *
 */
package fr.prima.bipcontrol ;

import com.apple.dnssd.TXTRecord;

/**
 * Structure to group the data about a service.
 * Group the data extracted from DNS-SD.
 * 
 * Provides also a method to generate id for BIP service 
 * (used in BIP exchange).
 * 
 * @author Sebastien Pesnel
 */
public class Service {
    /** name of the service */
    public String fullName = null;

    /** host name where the service is launched */
    public String hostName = null;

    /** port where the control server listens */
    public int port;

    /** records registers with the service */
    public TXTRecord txtRecord = null;

    /** register type */
    public String regType = null;

    /** domain name */
    public String domain = null;


    /** Create a new instance of Service */
    Service() {
    }
    
    /** Create a new instance of Service by copying the data about a service */
    Service(Service s) {
        fullName = s.fullName;
        hostName = s.hostName;
        port = s.port;
        txtRecord = s.txtRecord;
        regType = s.regType;
        domain = s.domain;
    }

    /**
     * Create a new instance of Service
     * 
     * @param name
     *            service name
     * @param regType
     *            register type
     * @param aDomain
     *            domain name
     */
    Service(String name, String regType, String aDomain) {
        fullName = name;
        this.regType = regType;
        domain = aDomain;
    }

    /**
     * Create a new instance of Service
     * 
     * @param name
     *            service name
     * @param host
     *            host name where the service is launched
     * @param portNb
     *            port where the control server listens
     * @param txtrecord
     *            records registered
     */
    Service(String name, String host, int portNb, TXTRecord txtrecord) {
        fullName = name;
        hostName = host;
        port = portNb;
        txtRecord = txtrecord;
    }
    
    public byte[] getTxtRecordValue(String key){
        return txtRecord.getValue(key);
    }
}
