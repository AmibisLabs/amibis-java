/*
 * MsgSocketUDP.java
 *
 * Created on 3 avril 2005, 15:29
 */

package fr.prima.omiscid.com;


import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.InetAddress;
import java.io.IOException;

/**
 * OMiSCID Communication based on UDP protocol
 * 
 * @author Sebastien Pesnel
 * Refactoring by Patrick Reignier
 */
public class MsgSocketUDP extends MsgSocket {
    /** Datagram socket for the UDP connection */
    private DatagramSocket datagramSocket;

    /** max size for the buffer */
    private int BUFFER_SIZE = 2000;

    /** buffer sent */
    private byte[] BufferSent;

    /** host where send data */
    private String host;

    /** port where send data */
    private int port;

    /**
     * Creates a new instance of MsgSocketUDP
     * 
     * @param serviceId use in OMiSCID exchange
     * @throws SocketException
     *             if error in creating datagram socket
     */
    public MsgSocketUDP(int serviceId) throws SocketException {
        super(serviceId);
        datagramSocket = new DatagramSocket();
        BufferSent = new byte[BUFFER_SIZE];
        connected = true;
    }

    /**
     * Creates a new instance of MsgSocketUDP
     * 
     * @param serviceId
     *            the id use in OMiSCID exchange
     * @param port
     *            port number where bind the datagram socket
     * @throws SocketException
     *             if error in creating datagram socket
     */
    public MsgSocketUDP(int serviceId, int port) throws SocketException {
        super(serviceId);
        datagramSocket = new DatagramSocket(port);
        BufferSent = new byte[BUFFER_SIZE];
        connected = true;
    }

    /**
     * Set the destination where send data
     * 
     * @param eHost
     *            name of the host
     * @param ePort
     *            port number
     */
    public void setDestination(String eHost, int ePort) {
        host = eHost;
        port = ePort;
    }

    /**
     * Send an array of byte on a datagram socket. Send the data to the
     * destination specify by setDestination
     * 
     * @param buffer
     *            the array of bytes to send
     */
    public synchronized void send(byte[] buffer) {
        // System.out.println("in MsgSocketUDP::Send");
        try {
            String header = GenerateHeader(buffer.length);
            byte tab[] = header.getBytes();
            int offset = 0;
            for (int i = 0; i < tab.length; i++)
                BufferSent[i] = tab[i];
            offset += tab.length;
            for (int i = 0; i < buffer.length; i++)
                BufferSent[i + offset] = buffer[i];
            offset += buffer.length;
            BufferSent[offset] = '\r';
            BufferSent[offset + 1] = '\n';
            offset += 2;
            DatagramPacket p = new DatagramPacket(BufferSent, 0, offset,
                    InetAddress.getByName(host), port);
            datagramSocket.send(p);
        } catch (IOException e) {
            System.out.println("MsgSocketUDP::send \n");
            e.printStackTrace();
            connected = false;
        }
    }

    /** Receive byte on a datagram socket */
    protected void receive() {
        // System.out.println("in MsgSocketUDP::Receive");
        try {
            DatagramPacket p = new DatagramPacket(receiveBuffer.buffer,
                    receiveBuffer.offset(), receiveBuffer.available());
            datagramSocket.receive(p);
            int nbRead = p.getLength();
            if (nbRead == 0) {
                System.out.println("MsgSocketUDP::receive (read=0)");
                connected = false;
            } else {
                receiveBuffer.received(nbRead);
                receiveBuffer.process();
            }
        } catch (IOException e) {
            System.out.println("MsgSocketUDP::receive Error");
            e.printStackTrace();
            connected = false;
        }
    }

    /** Close the connection */
    public void closeConnection() {
        datagramSocket.close();
        datagramSocket = null;
        connected = false;
    }

    /** @return 0 */
    public int getTcpPort() {
        return 0;
    }

    /** @return the datagramsocket port */
    public int getUdpPort() {
        if (datagramSocket == null)
            return 0;
        else
            return datagramSocket.getPort();
    }
    
    public int getPeerId(java.util.Vector<Integer> vec){
        if(isConnected()){
            vec.add(new Integer(getPeerId()));
            return 1;
        }
        return 0;
    }
}
