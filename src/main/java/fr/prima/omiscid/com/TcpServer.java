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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.prima.omiscid.com.interf.BipMessageListener;
import fr.prima.omiscid.user.util.Utility;
import java.net.SocketTimeoutException;

/**
 * TCP Server. Accept multiple connections. Enables sending messages to one or
 * all clients. Receives message from client identified by their ids. Manages a
 * set of MessageSocketTcp.
 *
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
// \REVIEWTASK shouldn't this be a monitor?
public class TcpServer implements CommunicationServer {
    /** Set of connections : set of MessageSocketTcp objects */
    private final Set<MessageSocketTCP> connectionsSet;

    /** Service id used to identify connecton in BIP exchanges */
    protected int peerId;

    /** Server Socket that listens for connection */
    private ServerSocket serverSocket;

    /** Thread to listen on the socket */
    private Thread listeningThread;

    /** Set of listener call when OMiSCID messages arrive */
    protected final Set<BipMessageListener> listenersSet;

    /**
     * Creates a new instance of TcpServer.
     *
     * @param peerId
     *            the BIP peer id to use in BIP exchange to represent the local
     *            peer
     * @param port
     *            the TCP port number to listen to
     * @exception IOException
     *                if an error occurs during socket creation
     */
    public TcpServer(int peerId, int port) throws IOException {
        serverSocket = new ServerSocket(port);
        this.peerId = peerId;
        connectionsSet = new HashSet<MessageSocketTCP>();
        listenersSet = new HashSet<BipMessageListener>();
    }



    /**
     * Closses this server and all previously initiated connections.
     *
     * See {@link #closeServer()} if you need to close the server
     * and wants to keep the initiated connections alive.
     *
     */
    public void close() {
        closeServer();
        synchronized (connectionsSet) {
            for (MessageSocketTCP socket : connectionsSet) {
                socket.closeConnection();
            }
            connectionsSet.clear();
        }
    }

    /**
     * Closes this server. The main socket is closed, the listening thread is
     * stopped. However all the initiated connections are still alive.
     * See {@link #close()} for a complete close (closing already initiated connections}
     */
    public final void closeServer() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(TcpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean closeConnection(int peerId) {
        synchronized (connectionsSet) {
            for (MessageSocketTCP socket : connectionsSet) {
                if (socket.getRemotePeerId() == peerId) {
                    socket.closeConnection();
                    connectionsSet.remove(socket);
                    return true;
                }
            }
            return false;
        }
    }

    void closeAllConnections() {
        synchronized (connectionsSet) {
            for (MessageSocketTCP socket : connectionsSet) {
                socket.closeConnection();
            }
            connectionsSet.clear();
        }
    }

    /**
     * Method run by the listening thread to accept new connections and
     * initialize {@link MessageSocketTCP}.
     */
    private void run() {
        try {
            serverSocket.setSoTimeout(900);
        } catch (SocketException ex) {
            throw new RuntimeException("socket problem", ex);
        }
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(true);
                // \REVIEWTASK tcp no delay policy
                MessageSocketTCP messageSocket = new MessageSocketTCP(peerId, socket);
                synchronized (listenersSet) {
                    for (BipMessageListener listener : listenersSet) {
                        messageSocket.addBipMessageListener(listener);
                    }
                }
                synchronized (connectionsSet) {
                    connectionsSet.add(messageSocket);
                }
                messageSocket.start(true);
                messageSocket.initializeConnection();
            } catch (SocketTimeoutException e) {
                // normal timeout to allow proper closing of this thread when the service is stopped
            } catch (IOException e) {
                /** Connection closed by {@link #close()} */
            }
        }
    }

    /**
     * @deprecated Use {@link #sendToAllClients(byte[])} instead.
     */
    @Deprecated
    public void sendToClients(byte[] buffer) {
        sendToAllClients(buffer);
    }

    /**
     * Sends a message to all still connected clients.
     *
     * @param buffer
     *            the message to send
     */
    public void sendToAllClients(byte[] buffer) {
        synchronized (connectionsSet) {
            Set<MessageSocketTCP> disconnectedClients = new HashSet<MessageSocketTCP>();
            for (MessageSocketTCP client : connectionsSet) {
                if (client.isConnected()) {
                    client.send(buffer);
                } else {
                    disconnectedClients.add(client);
                }
            }
            connectionsSet.removeAll(disconnectedClients);
        }
    }

    /**
     * Sends an XML DOM message to all still connected clients.
     *
     * @param message
     *            the XML message to send
     */
    public void sendToAllClients(Element message) {
        sendToAllClients(Utility.Xml.elementToByteArray(message));
    }

    /**
     * Sends an XML DOM message to all still connected clients.
     *
     * @param message
     *            the XML message to send
     */
    public void sendToAllClients(Document message) {
        sendToAllClients(message.getDocumentElement());
    }


    /**
     * Sends a String message to all still connected clients. The string is
     * encoded using the BIP encoding. To check that the encoding process went
     * right, you must do it yourself using
     * {@link fr.prima.omiscid.user.util.Utility#stringToByteArray(String)}.
     *
     * @param message
     *            the message to send
     */
    public void sendToAllClientsUnchecked(String message) {
        sendToAllClients(Utility.stringToByteArray(message));
    }

    /**
     * Sends a message to a particular client.
     *
     * @param buffer
     *            the message to send
     * @param peerId
     *            identify the client to contact
     * @return whether the client to contact has been found and the message was
     *         delivered to it
     */
    public boolean sendToOneClient(byte[] buffer, int peerId) {
        MessageSocket client = findConnection(peerId);
        if (client != null) {
            client.send(buffer);
            // \REVIEWTASK what if ! client.isConnected() ? to be though of when
            // changing this class to a monitor
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sends an XML DOM message to a particular client.
     *
     * @param message
     *            the XML message to send
     * @param peerId
     *            identify the client to contact
     * @return whether the client to contact has been found and the message was
     *         delivered to it
     */
    public boolean sendToOneClient(Element message, int peerId) {
        return sendToOneClient(Utility.Xml.elementToByteArray(message), peerId);
    }

    /**
     * Sends an XML DOM message to a particular client.
     *
     * @param message
     *            the XML message to send
     * @param peerId
     *            identify the client to contact
     * @return whether the client to contact has been found and the message was
     *         delivered to it
     */
    public boolean sendToOneClient(Document message, int peerId) {
        return sendToOneClient(message.getDocumentElement(), peerId);
    }

    /**
     * Sends a String message to a given client. The string is encoded using the
     * BIP encoding. To check that the encoding process went right, you must do
     * it yourself using {@link fr.prima.omiscid.user.util.Utility#stringToByteArray(String)}.
     *
     * @param message
     *            the message to send
     * @param peerId
     *            identify the client to contact
     */
    public void sendToOneClientUnchecked(String message, int peerId) {
        sendToOneClient(Utility.stringToByteArray(message), peerId);
    }

    /**
     * Adds a listener for the received BIP messages.
     *
     * @param listener
     *            a listener interested in the message received by the TCP
     *            server
     */
    public void addBipMessageListener(BipMessageListener listener) {
        synchronized (listenersSet) {
            listenersSet.add(listener);
            synchronized (connectionsSet) {
                Set<MessageSocketTCP> disconnectedClients = new HashSet<MessageSocketTCP>();
                for (MessageSocketTCP client : connectionsSet) {
                    if (client.isConnected()) {
                        client.addBipMessageListener(listener);
                    } else {
                        disconnectedClients.add(client);
                    }
                }
                connectionsSet.removeAll(disconnectedClients);
            }
        }
    }
    
    void removeAllBIPMessageListeners() {
        synchronized (listenersSet) {
            for (BipMessageListener listener : listenersSet) {
                synchronized (connectionsSet) {
                    Set<MessageSocketTCP> disconnectedClients = new HashSet<MessageSocketTCP>();
                    for (MessageSocketTCP client : connectionsSet) {
                        if (client.isConnected()) {
                            client.removeBipMessageListener(listener);
                        } else {
                            disconnectedClients.add(client);
                        }
                    }
                    connectionsSet.removeAll(disconnectedClients);
                }
            }
            listenersSet.clear();
        }
    }

    /**
     * Removes a listener for the received OMiSCID messages.
     *
     * @param listener
     *            the listener to remove
     */
    public void removeBIPMessageListener(BipMessageListener listener) {
        synchronized (listenersSet) {
            if (listenersSet.remove(listener)) {
                // the listener was actually in the listeners list
                synchronized (connectionsSet) {
                    Set<MessageSocketTCP> disconnectedClients = new HashSet<MessageSocketTCP>();
                    for (MessageSocketTCP client : connectionsSet) {
                        if (client.isConnected()) {
                            client.removeBipMessageListener(listener);
                        } else {
                            disconnectedClients.add(client);
                        }
                    }
                    connectionsSet.removeAll(disconnectedClients);
                }
            }
        }
    }

    /**
     * Finds a connection using a peer id.
     *
     * @param peerId
     * @return the connection found or null if none
     */
    protected MessageSocket findConnection(int peerId) {
        synchronized (connectionsSet) {
            Set<MessageSocketTCP> disconnectedClients = new HashSet<MessageSocketTCP>();
            MessageSocket found = null;
            for (MessageSocketTCP client : connectionsSet) {
                if (client.isConnected()) {
                    if (client.isConnectedToPeer(peerId)) {
                        assert found == null;
                        found = client;
                        // could break but the assert stuff adds some checking when running with -ea
                    }
                } else {
                    disconnectedClients.add(client);
                }
            }
            connectionsSet.removeAll(disconnectedClients);
            return found;
        }
    }

    /**
     * Tests whether a peer is still connected to the server.
     *
     * @param peerId
     *            the id of the peer
     * @return if the peer is connected
     */
    public boolean isStillConnected(int peerId) {
        MessageSocket m = findConnection(peerId);
        return m != null && m.isConnected();
    }

    /**
     * Accesses the port the server is listening to new connections.
     *
     * @return the port the server is listening to.
     */
    public int getTcpPort() {
        return serverSocket.getLocalPort();
    }

    /** @return 0 */
    public int getUdpPort() {
        return 0;
    }

    /**
     * Accesses the host name of the server.
     *
     * @return the host name
     */
    public String getHost() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.err.println("in TcpServer::getHost : Error");
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Queries the number of connections.
     *
     * @return number of connected client
     */
    public int getNbConnections() {
        synchronized (connectionsSet) {
            Set<MessageSocketTCP> disconnectedClients = new HashSet<MessageSocketTCP>();
            for (MessageSocketTCP client : connectionsSet) {
                if (!client.isConnected()) {
                    disconnectedClients.add(client);
                }
            }
            connectionsSet.removeAll(disconnectedClients);
            return connectionsSet.size();
        }
    }


    /**
     * Starts this server. A background thread is started to listen for incoming
     * connections on the server port. The started thread is automatically
     * stopped on any call to {@link #close()}.
     */
    public void start() {
        if (listeningThread == null) {
            listeningThread = new Thread(new Runnable() {
                public void run() {
                    TcpServer.this.run();
                }
            }, "Omiscid Connection Listener Thread");
            listeningThread.start();
        } else {
            System.err.println("Warning: TcpServer start() method called more than once");
        }
    }

    public int getConnectedPeerIds(java.util.Vector<Integer> vec) {
        synchronized (connectionsSet) {
            int nb = 0;
            Set<MessageSocketTCP> disconnectedClients = new HashSet<MessageSocketTCP>();
            for (MessageSocketTCP client : connectionsSet) {
                if (client.isConnected()) {
                    vec.add(new Integer(client.getRemotePeerId()));
                    nb++;
                } else {
                    disconnectedClients.add(client);
                }
            }
            connectionsSet.removeAll(disconnectedClients);
            return nb;
        }
    }



    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }
}
