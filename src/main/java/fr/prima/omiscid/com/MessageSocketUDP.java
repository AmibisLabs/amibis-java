/**
 * Copyright© 2005-2006 INRIA/Université Pierre Mendès-France/Université Joseph Fourier.
 *
 * O3MiSCID (aka OMiSCID) Software written by Sebastien Pesnel, Dominique
 * Vaufreydaz, Patrick Reignier, Remi Emonet and Julien Letessier.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package fr.prima.omiscid.com;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * BIP Communication based on UDP protocol
 *
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
public class MessageSocketUDP extends MessageSocket {
    /** Datagram socket for the UDP connection */
    private DatagramSocket datagramSocket;

    /** max size for the buffer */
    private int BUFFER_SIZE = 2000;

    /** buffer sent */
    private byte[] bufferSent;

    /** host where send data */
    private String host;

    /** port where send data */
    private int port;

    /**
     * Creates a new instance of MessageSocketUDP.
     *
     * @param peerId
     *            the local BIP peer id used in BIP exchanges
     * @throws SocketException
     *             if error in creating datagram socket
     */
    public MessageSocketUDP(int peerId) throws SocketException {
        super(peerId);
        datagramSocket = new DatagramSocket();
        bufferSent = new byte[BUFFER_SIZE];
        connected = true;
    }

    /**
     * Creates a new instance of MessageSocketUDP.
     *
     * @param peerId
     *            the local BIP peer id used in BIP exchanges
     * @param port
     *            port number where to bind the datagram socket (port to listen
     *            to)
     * @throws SocketException
     *             if error occurs during the creation of the datagram socket
     */
    public MessageSocketUDP(int peerId, int port) throws SocketException {
        super(peerId);
        datagramSocket = new DatagramSocket(port);
        bufferSent = new byte[BUFFER_SIZE];
        connected = true;
    }

    /**
     * Sets the destination where send data.
     *
     * @param host
     *            name of the host
     * @param port
     *            port number
     */
    public void setDestination(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Send an array of byte on a datagram socket. Send the data to the
     * destination specify by setDestination
     *
     * @param buffer
     *            the array of bytes to send
     */
    public synchronized void sendExplicit(byte[] buffer) throws IOException {
        try {
            byte header[] = generateHeaderByte(buffer.length);
            int offset = 0;
            for (byte b : header) {
                bufferSent[offset++] = b;
            }
            for (byte b : buffer) {
                bufferSent[offset++] = b;
            }
            for (byte b : BipUtils.messageEnd) {
                bufferSent[offset++] = b;
            }
            DatagramPacket p = new DatagramPacket(bufferSent, 0, offset, InetAddress.getByName(host), port);
            datagramSocket.send(p);
        } catch (IOException e) {
//            System.out.println("MessageSocketUDP::send \n");
//            e.printStackTrace();
            connected = false;
            throw e;
        }

    }

    /** Receive byte on a datagram socket */
    protected void receive() {
        // System.out.println("in MessageSocketUDP::Receive");
        try {
            DatagramPacket p = new DatagramPacket(receiveBuffer.buffer, receiveBuffer.offset(), receiveBuffer.available());
            datagramSocket.receive(p);
            int nbRead = p.getLength();
            if (nbRead == 0) {
                System.out.println("MessageSocketUDP::receive (read=0)");
                connected = false;
            } else {
                receiveBuffer.received(nbRead);
                receiveBuffer.process();
            }
        } catch (IOException e) {
            System.out.println("MessageSocketUDP::receive Error");
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
        if (datagramSocket == null) {
            return 0;
        } else {
            return datagramSocket.getPort();
        }
    }

}
