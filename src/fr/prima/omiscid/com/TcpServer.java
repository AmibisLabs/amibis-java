/*
 * TcpServer.java
 *
 */
package fr.prima.omiscid.com;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import fr.prima.omiscid.com.interf.BipMessageListener;
import fr.prima.omiscid.com.interf.Message;

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
    private Set<MessageSocketTCP> connectionsSet;

    /** Service id used to identify connecton in BIP exchanges */
    protected int peerId;

    /** Server Socket that listens for connection */
    private ServerSocket serverSocket;

    /** Thread to listen on the socket */
    private Thread listeningThread;

    /** Set of listener call when OMiSCID messages arrive */
    private Set<BipMessageListener> listenersSet;

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
     * Closes this server. The main socket is closed, the listening thread is
     * stopped. However all the initiated connections are still alive.
     */
    // \REVIEWTASK should we close all the subconnections ? should we propose
    // both ? ...
    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Method run by the listening thread to accept new connections and
     * initialize {@link MessageSocketTCP}.
     */
    private void run() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(true);
                // \REVIEWTASK tcp no delay policy
                MessageSocketTCP messageSocket = new MessageSocketTCP(peerId, socket);
                synchronized (listenersSet) {
                    for (BipMessageListener listener : listenersSet) {
                        messageSocket.addOmiscidMessageListener(listener);
                    }
                }
                messageSocket.start();
                messageSocket.initializeConnection();
                synchronized (connectionsSet) {
                    connectionsSet.add(messageSocket);
                }
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
     * Sends a String message to all still connected clients. The string is
     * encoded using the BIP encoding. To check that the encoding process went
     * right, you must do it yourself using
     * {@link BipUtils#stringToByteArray(String)}.
     * 
     * @param message
     *            the message to send
     */
    public void sendToAllClientsUnchecked(String message) {
        sendToAllClients(BipUtils.stringToByteArray(message));
    }

    /**
     * Sends a message to a particular client.
     * 
     * @param buffer
     *            the message to send
     * @param peerid
     *            identify the client to contact
     * @return whether the client to contact has been found and the message was
     *         delivered to it
     */
    public boolean sendToOneClient(byte[] buffer, int peerid) {
        MessageSocket client = findConnection(peerid);
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
     * Sends a String message to a given client. The string is encoded using the
     * BIP encoding. To check that the encoding process went right, you must do
     * it yourself using {@link BipUtils#stringToByteArray(String)}.
     * 
     * @param buffer
     *            the message to send
     * @param peerid
     *            identify the client to contact
     */
    public void sendToOneClientUnchecked(String message, int peerId) {
        sendToOneClient(BipUtils.stringToByteArray(message), peerId);
    }

    /**
     * Adds a listener for the received BIP messages.
     * 
     * @param listener
     *            a listener interested in the message received by the TCP
     *            server
     */
    public void addOmiscidMessageListener(BipMessageListener listener) {
        synchronized (listenersSet) {
            listenersSet.add(listener);
            synchronized (connectionsSet) {
                Set<MessageSocketTCP> disconnectedClients = new HashSet<MessageSocketTCP>();
                for (MessageSocketTCP client : connectionsSet) {
                    if (client.isConnected()) {
                        client.addOmiscidMessageListener(listener);
                    } else {
                        disconnectedClients.add(client);
                    }
                }
                connectionsSet.removeAll(disconnectedClients);
            }
        }
    }

    /**
     * Removes a listener for the received OMiSCID messages.
     * 
     * @param listener
     *            the listener to remove
     */
    public void removeOmiscidMessageListener(BipMessageListener listener) {
        synchronized (listenersSet) {
            if (listenersSet.remove(listener)) {
                // the listener was actually in the listeners list
                synchronized (connectionsSet) {
                    Set<MessageSocketTCP> disconnectedClients = new HashSet<MessageSocketTCP>();
                    for (MessageSocketTCP client : connectionsSet) {
                        if (client.isConnected()) {
                            client.removeOmiscidMessageListener(listener);
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
                        // could break but the assert stuff adds some checking
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
     * main for test Create a TCP server and a TCP client The server send
     * message and the client connects to the server and receives the message. A
     * listener displays the message.
     */
    public static void main(String[] arg) {

        int port = 5001;

        TcpClient tcpClient = null;
        try {
            final TcpServer tcpServer = new TcpServer(666, port);
            System.out.println("Connect?");

            tcpServer.start();

            tcpClient = new TcpClient(999);
            tcpClient.connectTo("localhost", port);

            Thread t = new Thread() {
                public void run() {
                    int nb = -1;
                    String str = "toto12345678901234567890";
                    while (true) {
                        nb++;
                        if (nb % 100 == 0) {
                            tcpServer.sendToAllClientsUnchecked(str);
                            nb = 0;
                        }
                    }
                }
            };
            t.start();

            MessageManager messageManager = new MessageManager() {
                protected void processMessage(Message message) {
                    try {
                        System.out.println(message.getBufferAsString());
                    } catch (BipMessageInterpretationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            tcpClient.addOmiscidMessageListener(messageManager);

            while (true) {
                messageManager.waitForMessages();
                int nbProcess = messageManager.processMessages();
                System.out.println("----------------process : " + nbProcess);
            }
        } catch (IOException e) {
            System.out.println("main");
            System.out.println(e);
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
            });
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
}
