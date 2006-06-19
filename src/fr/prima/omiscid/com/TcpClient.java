/*
 * TcpClient.java
 *
 */

package fr.prima.omiscid.com;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Client for exchanges of BIP messages over TCP connexion.
 *
 * @author Sebastien Pesnel Refactoring by Patick Reignier
 */
public class TcpClient extends MessageSocketTCP {
    /**
     * Creates a new instance of TcpClient.
     *
     * @param peerId
     *            BIP peer id of the local peer
     */
    public TcpClient(int peerId) {
        super(peerId);
    }

    public static boolean stripTrailingDotLocalDot = true;
    static {
        try {
            stripTrailingDotLocalDot = null == System.getenv("OMISCIDNS_USE_MDNS_NAME_SOLVING");
            // \REVIEWTASK this variable name should be documented somewhere
        } catch (SecurityException e) {
            // Access to environment variable is forbidden
            System.err.println("Warning: access to environment variables is forbidden.");
        }

    };

    /**
     * Connects to a server.
     *
     * @param host
     *            host name where listens the server
     * @param port
     *            port number where listens the server
     * @exception IOException
     *                if error during socket creation
     */
    public void connectTo(String host, int port) throws IOException {
        Socket socket = new Socket();
        socket.setTcpNoDelay(true);
        // \REVIEWTASK should have a policy on tcp no delay
        if (stripTrailingDotLocalDot && host.endsWith(".local.")) {
            host = host.replaceAll("[.]local[.]$", "");
            // System.out.println(host);
        }
        InetSocketAddress endpoint = new InetSocketAddress(host, port);
        socket.connect(endpoint);
        super.setSocket(socket);
        super.start();
        initializeConnection();
    }
}
