/*
 * TcpServer.java
 *
 */
package fr.prima.bipcom;


import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;

import fr.prima.bipcom.interf.BipMessageListener;


/**
 * TCP Server. Accept multiple connection. Enable to send message to one or all
 * clients, and to receive message from client identify by their ids. Manage a
 * set of MsgSocketTcp.
 * 
 * @author Sebastien Pesnel
 * Refactoring by Patrick Reignier
 */
public class TcpServer extends Thread implements ComTools {
    /** Set of connections : set of MsgSocketTcp objects */
    private Set<MsgSocketTCP> connectionSet;

    /** Service id used to identify connecton in BIP exchange */
    private int serviceId;

    /** Server Socket thay listen for connection */
    private ServerSocket serverSocket;

    /** Set of listener call when BIP messages arrive */
    private Set<BipMessageListener> listenerSet;

    /**
     * Creates a new instance of TcpServer
     * 
     * @param serviceId
     *            the id use in BIP exchange
     * @param port
     *            the port number where the TCP server must listen
     * @exception IOException
     *                if error during socket creation
     */
    public TcpServer(int serviceId, int port) throws IOException {
        serverSocket = new ServerSocket(port);
        this.serviceId = serviceId;
        connectionSet = new HashSet<MsgSocketTCP>();
        listenerSet = new HashSet<BipMessageListener>();
    }
    
    public void close()
    {
    		try {
				serverSocket.close() ;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }

    /**
     * Method run in a thread Accept connection on the TCP server Creation of
     * MsgSocket add to the set of connection
     */
    public void run() {
        while (!serverSocket.isClosed()) {
            try {

                Socket s = serverSocket.accept();
                // System.out.println("New Connection " + getNbConnections());
                MsgSocketTCP msgSocket = new MsgSocketTCP(serviceId, s);

                synchronized (listenerSet) {
                    java.util.Iterator<BipMessageListener> it = listenerSet.iterator();
                    while (it.hasNext()) {
                        msgSocket.addBipMessageListener(it.next());
                    }
                }
                msgSocket.start();
                msgSocket.send((byte[])null);
                
                /*while(!msgSocket.getEmptyMsgReceived()){
                    try{
                        Thread.sleep(1);
                    }catch(InterruptedException e){}
                }                
                */
                
                synchronized (connectionSet) {                                       
                /*    MsgSocket m = null;
                    m = findConnection(msgSocket.getPeerId());
                    while(m != null){
                        m.closeConnection();
                        m = findConnection(msgSocket.getPeerId());
                    }
                    */                    
                    connectionSet.add(msgSocket);
                }
                
            } catch (IOException e) {
//                System.out.println("TcpServer::run");
//                System.out.println(e);
            }
        }
    }

    /**
     * Send a message to all connected clients
     * 
     * @param buffer
     *            the message to send
     */
    public void sendToClients(byte[] buffer) {
        synchronized (connectionSet) {
            Set<MsgSocketTCP> tmpSet = new HashSet<MsgSocketTCP>();
            java.util.Iterator<MsgSocketTCP> it = connectionSet.iterator();
            while (it.hasNext()) {
                MsgSocketTCP client = it.next();
                if (client.isConnected()) {
                    client.send(buffer);
                } else {
                    tmpSet.add(client);
                }
            }
            it = tmpSet.iterator();
            while (it.hasNext()) {
                connectionSet.remove(it.next());
            }
        }
    }

    /**
     * Send a message to a particular client
     * 
     * @param buffer
     *            the message to send
     * @param pid
     *            identify the client to contact
     * @return if the client to contact has been found
     */
    public boolean sendToOneClient(byte[] buffer, int pid) {
        MsgSocket m = findConnection(pid);
        if (m != null) {
            m.send(buffer);
            return true;
        }
        return false;
    }

    /**
     * Add a listener on the BIP message
     * 
     * @param listener
     *            a listener interested in the message received by the TCP
     *            server
     */
    public void addBipMessageListener(BipMessageListener listener) {
        synchronized (listenerSet) {
            listenerSet.add(listener);
            synchronized (connectionSet) {
                Set<MsgSocketTCP> tmpSet = new HashSet<MsgSocketTCP>();
                java.util.Iterator<MsgSocketTCP> it = connectionSet.iterator();
                while (it.hasNext()) {
                    MsgSocketTCP client = it.next();
                    if (client.isConnected()) {
                        client.addBipMessageListener(listener);
                    } else {
                        tmpSet.add(client);
                    }
                }
                it = tmpSet.iterator();
                while (it.hasNext()) {
                    connectionSet.remove(it.next());
                }
            }
        }
    }

    /**
     * Remove a listener on the BIP message
     * 
     * @param listener
     *            the listener to remove
     */
    public void removeBipMessageListener(BipMessageListener listener) {
        synchronized (listenerSet) {
            if (listenerSet.remove(listener)) {
                listenerSet.add(listener);
                synchronized (connectionSet) {
                    Set<MsgSocketTCP> tmpSet = new HashSet<MsgSocketTCP>();
                    java.util.Iterator<MsgSocketTCP> it = connectionSet.iterator();
                    while (it.hasNext()) {
                        MsgSocketTCP client = it.next();
                        if (client.isConnected()) {
                            client.removeBipMessageListener(listener);
                        } else {
                            tmpSet.add(client);
                        }
                    }
                    it = tmpSet.iterator();
                    while (it.hasNext()) {
                        connectionSet.remove(it.next());
                    }
                }
            }
        }
    }

    /**
     * Finds a connection
     * 
     * @param pid
     * 
     */
    protected MsgSocket findConnection(int pid) {
        synchronized (connectionSet) {
            //System.out.println("findConnection "+ pid);
            Set<MsgSocketTCP> tmpSet = new HashSet<MsgSocketTCP>();
            java.util.Iterator<MsgSocketTCP> it = connectionSet.iterator();
            MsgSocket found = null;
            while (it.hasNext() /*&& found == null*/) {
                MsgSocketTCP current = it.next();
                // System.out.println(pid +" // "+ current.getPeerId());
                if (current.isConnected()) {
                    if (current.isConnectedToPeer(pid)){
                        found = current;
                        //System.out.println("findConnection "+ pid+" found");
                    }
                } else {
                    tmpSet.add(current);
                }
            }
            it = tmpSet.iterator();
            while (it.hasNext()) {
                connectionSet.remove(it.next());
            }
            return found;
        }
    }
    
    /** Test if a peer is still connected to the server 
     * @param peerId the id of the peer
     * @return if the peer is connected
     */
    public boolean isStillConnected(int peerId){
        MsgSocket m = findConnection(peerId);
        if(m != null && m.isConnected()) return true;
        return false;
    }
    
    /**
     * The port where listen the server
     * 
     * @return the port where listen the server
     */
    public int getTcpPort() {
        return serverSocket.getLocalPort();
    }

    /** @return 0 */
    public int getUdpPort() {
        return 0;
    }

    /**
     * The host name of the server
     * 
     * @return the host name
     */
    public String getHost() {
        try {
            // System.out.println("InetAddr: " + InetAddress.getLocalHost());
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.err.println("in TcpServer::getHost : Error");
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Number of connections
     * 
     * @return number of connected client
     */
    public int getNbConnections() {
        synchronized (connectionSet) {
            int nb = 0;
            Set<MsgSocketTCP> tmpSet = new HashSet<MsgSocketTCP>();
            java.util.Iterator<MsgSocketTCP> it = connectionSet.iterator();
            while (it.hasNext()) {
                MsgSocketTCP client = it.next();
                if (client.isConnected()) {
                    nb++;
                } else {
                    tmpSet.add(client);
                }
            }
            it = tmpSet.iterator();
            while (it.hasNext()) {
                connectionSet.remove(it.next());
            }
            return nb;
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
                            tcpServer.sendToClients(str.getBytes());
                            nb = 0;
                        }
                    }
                }
            };
            t.start();

            /*
             * tcpClient.addBipMessageListener( new BipMessageListener() {
             * public void receivedBipMessage(Message msg) {
             * System.out.println("in toto listener:"); System.out.println(msg); }
             * });
             */
            MsgManager msgManager = new MsgManager();
            tcpClient.addBipMessageListener(msgManager);

            while (true) {
                msgManager.waitForMessage();
                int nbProcess = msgManager.processMessages();
                System.out.println("----------------process : " + nbProcess);
            }
        } catch (IOException e) {
            System.out.println("main");
            System.out.println(e);
        }
    }
    
    public int getPeerId(java.util.Vector<Integer> vec){
        synchronized (connectionSet) {
            int nb = 0;
            Set<MsgSocketTCP> tmpSet = new HashSet<MsgSocketTCP>();
            java.util.Iterator<MsgSocketTCP> it = connectionSet.iterator();
            while (it.hasNext()) {
                MsgSocketTCP client = it.next();
                if (client.isConnected()) {
                    vec.add(new Integer(client.getPeerId()));
                    nb++;
                } else {
                    tmpSet.add(client);
                }
            }
            it = tmpSet.iterator();
            while (it.hasNext()) {
                connectionSet.remove(it.next());
            }
            return nb;
        }
    }
}
