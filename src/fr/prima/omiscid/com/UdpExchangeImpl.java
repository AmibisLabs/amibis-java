/*
 * UdpExchange.java
 *
 * Created on 3 avril 2005, 16:31
 */

package fr.prima.omiscid.com;

import fr.prima.omiscid.com.interf.OmiscidMessageListener;
import fr.prima.omiscid.com.interf.Message;
import fr.prima.omiscid.com.interf.UdpExchange;



/**
 * Communication with OMiSCID message on UDP protocol
 * 
 * @author Sebastien Pesnel
 * Refactoring by Patrick Reignier
 */
public class UdpExchangeImpl extends MsgSocketUDP implements UdpExchange {

    /**
     * Creates a new instance of UdpExchange
     * 
     * @param serviceId
     *            the id use in OMiSCID exchange
     * @exception SocketException
     *                if error during socket creation
     */
    public UdpExchangeImpl(int serviceId) throws java.net.SocketException {
        super(serviceId);
    }

    /**
     * Creates a new instance of UdpExchange Bind the socket to a port
     * 
     * @param serviceId
     *            the id use in OMiSCID exchange
     * @param port
     *            port where bind the socket
     * @exception SocketException
     *                if error during socket creation
     */
    public UdpExchangeImpl(int serviceId, int port)
            throws java.net.SocketException {
        super(serviceId, port);
    }

    /* (non-Javadoc)
	 * @see fr.prima.omiscid.com.UdpExchange#send(byte[], java.lang.String, int)
	 */
    public void send(byte[] buffer, String host, int port) {
        setDestination(host, port);
        send(buffer);
    }

    /** Main for test */
    public static void main(String[] arg) {
        UdpExchange udpSent = null;
        UdpExchangeImpl udpRecv = null;
        try {
            udpSent = new UdpExchangeImpl(666);
            udpRecv = new UdpExchangeImpl(999, 5555);

            System.out.println("launched");

            udpRecv.addOmiscidMessageListener(new OmiscidMessageListener() {
                public void receivedOmiscidMessage(Message msg) {
                    System.out.println("in toto listener:");
                    System.out.println(msg);
                }
            });

            udpRecv.start();

            while (true) {
                String str = "toto12345678901234567890";
                udpSent.send(str.getBytes(), "localhost", 5555);
            }
        } catch (java.io.IOException e) {
            System.out.println("main");
            System.out.println(e);
        }
    }
}
