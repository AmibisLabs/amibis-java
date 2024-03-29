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

import fr.prima.omiscid.control.ControlClient;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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
            host = host.replaceFirst("[.]local[.]$", "");
            // System.out.println(host);
        }
        InetSocketAddress endpoint = new InetSocketAddress(host, port);
        try {
            socket.connect(endpoint, ControlClient.maxTimeToWait);
        } catch (UnknownHostException e) {
            String removalPattern = "^(.*)-[0-9][0-9]?$";
            if (host.matches(removalPattern)) {
                host = host.replaceFirst(removalPattern, "$1");
                endpoint = new InetSocketAddress(host, port);
                socket.connect(endpoint, ControlClient.maxTimeToWait);
            } else {
                throw e;
            }
        }
        super.setSocket(socket);
        super.start(true);
        initializeConnection();
    }

/*    public static void main(String[] args) throws IOException, InterruptedException {
        final ServiceFactory f = new ServiceFactoryImpl();
        final Service s = f.create("da");
        s.addConnector("c", "...", ConnectorType.INPUT);
        s.addConnectorListener("c", new ConnectorListener() {

            public void messageReceived(Service service, String localConnectorName, Message message) {
            }

            public void disconnected(Service service, String localConnectorName, int peerId) {
            }

            public void connected(Service service, String localConnectorName, int peerId) {
                System.err.println("CONNECTED");
            }
        });
        ServiceRepository rep = f.createServiceRepository();
        rep.addListener(new ServiceRepositoryListener() {

            public void serviceAdded(ServiceProxy serviceProxy) {
                System.err.println("ADD "+serviceProxy.getName()+" on "+serviceProxy.getHostName());
                s.connectTo("c", serviceProxy, "events");
            }

            public void serviceRemoved(ServiceProxy serviceProxy) {

            }
        }, ServiceFilters.hasConnector("events"));
        Thread.sleep(10000);
        System.exit(0);
    }*/
}
