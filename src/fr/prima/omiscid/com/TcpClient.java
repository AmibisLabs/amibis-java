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
            socket.connect(endpoint);
        } catch (UnknownHostException e) {
            String removalPattern = "-[0-9][0-9]?$";
            if (host.matches(removalPattern)) {
                host = host.replaceFirst(removalPattern, "");
                endpoint = new InetSocketAddress(host, port);
                socket.connect(endpoint);
            } else {
                throw e;
            }
        }
        super.setSocket(socket);
        super.start();
        initializeConnection();
    }
}
