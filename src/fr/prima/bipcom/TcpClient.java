/*
 * TcpClient.java
 *
 */

package fr.prima.bipcom;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * TCP Client. Exchange of BIP message over TCP connexion.
 * 
 * @author Sebastien Pesnel
 * Refactoring by Patick Reignier
 */
public class TcpClient extends MsgSocketTCP {
    /**
     * Creates a new instance of TcpClient
     * 
     * @param serviceId
     *            id of service associated to this object (id exchanged in BIP
     *            message)
     */
    public TcpClient(int serviceId) {
        super(serviceId);
    }
    
    public static boolean stripTrailingDotLocalDot = true;
    static {
        try {
            stripTrailingDotLocalDot = null == System.getenv("BIPNS_USE_MSDN_NAME_SOLVING");
        } catch (SecurityException e) {
            // Access to environment variable is forbidden
            System.out.println("Warning: access to environment variables is forbidden.");
        }        
        
    };

    /**
     * Connexion to a server.
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
        if (stripTrailingDotLocalDot && host.endsWith(".local.")) {
            host = host.replaceAll("[.]local[.]$","");
            //System.out.println(host);
        }
        InetSocketAddress endpoint = new InetSocketAddress(host, port);
        socket.connect(endpoint);
        super.setSocket(socket);
        super.start();
        send((byte[])null);
    }
}
