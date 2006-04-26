/*
 * MsgSocketTCP.java
 *
 * Created on 3 avril 2005, 15:29
 */

package fr.prima.omiscid.com;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * OMiSCID Communication based on TCP protocol
 * @author  Sebastien Pesnel  Refactoring by Patrick Reignier
 */
public class MsgSocketTCP extends MsgSocket {

    /** The socket for the TCP connection */
    private Socket socket = null;

    /**
     * Creates a new instance of MsgSocketTCP
     * 
     * @param serviceId
     *            the id use in OMiSCID exchange
     */
    public MsgSocketTCP(int serviceId) {
        super(serviceId);
    }

    /**
     * Creates a new instance of MsgSocketTCP
     * 
     * @param serviceId
     *            the id use in OMiSCID exchange
     * @param aSocket
     *            the socket associate to a TCP connection
     * @see MsgSocketTCP#setSocket(Socket)
     */
    public MsgSocketTCP(int serviceId, Socket aSocket) {
        super(serviceId);
        setSocket(aSocket);
    }

    /**
	 * Set the socket associate to a TCP connection
	 * @param aSocket  the socket associate to a TCP connection
	 * @uml.property  name="socket"
	 */
    public void setSocket(Socket aSocket) {
        socket = aSocket;
        connected = true;
    }

    /**
     * Send an array of byte with a OMiSCID header
     * 
     * @param buffer
     *            the array of byte to send
     */
    public synchronized void send(byte[] buffer) {
        try {
            OutputStream output = new BufferedOutputStream( socket.getOutputStream());
            if (buffer == null) {
                String header = GenerateHeader(0);
                output.write(header.getBytes());
            } else {
                String header = GenerateHeader(buffer.length);
                output.write(header.getBytes());
                output.write(buffer);
            }
            output.write(msgEnd);
            output.flush();
        } catch (IOException e) {
            // e.printStackTrace();
            connected = false;
        }
    }
    
    /**
     * Sends a String message with a OMiSCID header
     * 
     * @param msgBody
     *            the message to send once completed with the OMiSCID header
     */
    public void send( String msgBody) {
        send(msgBody.getBytes());
        //TODO should getBytes with the OMiSCID charset (if any)
    }
    
    /** Receive byte from the TCP connection */
    protected void receive() {
        try {
            // System.out.println("in MsgSocketTCP:Receive");
            InputStream input = socket.getInputStream();
            int nbRead = input.read(receiveBuffer.buffer, receiveBuffer
                    .offset(), receiveBuffer.available());
            if (nbRead <= 0) {
                //System.out.println("MsgSocketTCP::receive (read<=0)");
                connected = false;
            } else {
                // System.out.println("MsgSocketTCP::receive read="+nbRead);
                receiveBuffer.received(nbRead);
                receiveBuffer.process();
            }
        } catch (IOException e) {
            // System.out.println("MsgSocketTCP::receive Error");
            // e.printStackTrace();
            connected = false;
        }
    }

    /** Close the connection */
    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {}
        
        socket = null;
        connected = false;
    }

    /**
     * Return the port of the TCP socket
     * 
     * @return the port of the TCP socket
     */
    public int getTcpPort() {
        if (socket == null)
            return 0;
        else
            return socket.getPort();
    }

    /** No UDP port  
     * @return 0 */
    public int getUdpPort() {
        return 0;
    }
    
    public int getPeerId(java.util.Vector<Integer> vec){
        if(isConnected()){
            vec.add(new Integer(getPeerId()));
            return 1;
        }
        return 0;
    }
    
    public String toString()
    {
    	  return "MsgSocket " + Integer.toHexString(getServiceId()) + " to " + Integer.toHexString(getPeerId()) + ":" + getTcpPort() ; 
    }
        
    
}
