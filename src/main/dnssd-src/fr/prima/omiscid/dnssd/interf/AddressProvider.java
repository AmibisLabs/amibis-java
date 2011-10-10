package fr.prima.omiscid.dnssd.interf;

import java.net.InetAddress;


public interface AddressProvider {
    
    public InetAddress[] getInetAddresses();
    
}
