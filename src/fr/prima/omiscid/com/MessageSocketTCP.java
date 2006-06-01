/*
 * MessageSocketTCP.java
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
 * BIP Communication based on TCP protocol.
 *
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
public class MessageSocketTCP extends MessageSocket {

    /** The socket for the TCP connection */
    private Socket socket = null;

    /**
     * Creates a new instance of MessageSocketTCP.
     *
     * @param peerId
     *            the local BIP peer id used in BIP exchanges
     */
    public MessageSocketTCP(int peerId) {
        super(peerId);
    }

    /**
     * Creates a new instance of MessageSocketTCP.
     *
     * @param peerId
     *            the local BIP peer id used in OMiSCID exchanges
     * @param aSocket
     *            the socket associate to a TCP connection
     * @see MessageSocketTCP#setSocket(Socket)
     */
    public MessageSocketTCP(int peerId, Socket aSocket) {
        super(peerId);
        setSocket(aSocket);
    }

    /**
     * Sets the socket associate to a TCP connection.
     *
     * @param aSocket
     *            the socket associate to a TCP connection
     */
    public void setSocket(Socket aSocket) {
        socket = aSocket;
        connected = true;
    }

    /**
     * Sends an array of byte with a BIP header.
     *
     * @param buffer
     *            the array of byte to send
     */
    public synchronized void sendExplicit(byte[] buffer) throws IOException {
        try {
            OutputStream output = new BufferedOutputStream(socket.getOutputStream());
            if (buffer == null) {
                output.write(generateHeaderByte(0));
            } else {
                output.write(generateHeaderByte(buffer.length));
                output.write(buffer);
            }
            output.write(BipUtils.messageEnd);
            output.flush();
        } catch (IOException e) {
            connected = false;
            throw e;
        }
    }

    /**
     * Receives bytes from the TCP connection
     */
    protected void receive() {
        try {
            InputStream input = socket.getInputStream();
            int nbRead = input.read(receiveBuffer.buffer, receiveBuffer.offset(), receiveBuffer.available());
            if (nbRead <= 0) {
                connected = false;
            } else {
                receiveBuffer.received(nbRead);
                receiveBuffer.process();
            }
        } catch (IOException e) {
            connected = false;
        }
    }

    /**
     * Closes the connection.
     */
    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
        }

        socket = null;
        connected = false;
    }

    /**
     * Accesses the port of the TCP socket.
     *
     * @return the port of the TCP socket
     */
    public int getTcpPort() {
        if (socket == null)
            return 0;
        else
            return socket.getPort();
    }

    /**
     * No UDP port.
     *
     * @return 0
     */
    public int getUdpPort() {
        return 0;
    }

    public String toString() {
        return "MessageSocket " + Integer.toHexString(getLocalPeerId()) + " to " + Integer.toHexString(getRemotePeerId()) + ":" + getTcpPort();
    }

}
