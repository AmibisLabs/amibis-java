package fr.prima.omiscid.com;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import fr.prima.omiscid.com.interf.BipMessageListener;

/**
 * @author Patrick Reignier (UJF/Gravir)
 */
// \REVIEWTASK shouldn't this be a monitor?
public class TcpClientServer extends TcpServer {

    /** List of clients connected to a remote server (accessed by peer Id) */
    protected final HashMap<Integer, TcpClient> clientsList = new HashMap<Integer, TcpClient>();

    /**
     * @param peerId
     *            the local BIP peer id to use for BIP communications
     * @throws IOException
     */
    public TcpClientServer(int peerId) throws IOException {
        super(peerId, 0);
    }

    /**
     * Connects to a remote server.
     * 
     * @param host
     *            the host address
     * @param port
     *            the port number
     * @return the peer id of the remote peer
     */
    public int connectTo(String host, int port) {
        TcpClient tcpClient = new TcpClient(peerId);
        try {
            tcpClient.connectTo(host, port);
            while (tcpClient.getRemotePeerId() == 0) {
                Thread.yield();
                // \REVIEWTASK probably 100% cpu usage, should be tested and
                // done differently (small sleep?)
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        synchronized (this) {
            clientsList.put(tcpClient.getRemotePeerId(), tcpClient);
        }
        return tcpClient.getRemotePeerId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.prima.omiscid.com.TcpServer#sendToClients(byte[])
     */
    @Override
    public void sendToClients(byte[] buffer) {
        super.sendToAllClients(buffer);
        synchronized (this) {
            for (TcpClient client : clientsList.values()) {
                client.send(buffer);
            }
        }
    }

    /**
     * Sends a buffer to a particular client.
     * 
     * @param buffer
     *            the buffer to send
     * @param pid
     *            the peer id
     * @return true if the client exists
     */
    public boolean sendToOneClient(byte[] buffer, int pid) {
        TcpClient client = clientsList.get(pid);
        if (client != null) {
            client.send(buffer);
            return true;
        } else {
            return super.sendToOneClient(buffer, pid);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.prima.omiscid.com.TcpServer#addOmiscidMessageListener(fr.prima.omiscid.com.interf.OmiscidMessageListener)
     */
    @Override
    public void addOmiscidMessageListener(BipMessageListener listener) {
        super.addOmiscidMessageListener(listener);
        synchronized (this) {
            cleanListOfClients();
            for (TcpClient client : clientsList.values()) {
                if (client.isConnected()) {
                    client.addOmiscidMessageListener(listener);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.prima.omiscid.com.TcpServer#getNbConnections()
     */
    @Override
    public int getNbConnections() {
        cleanListOfClients();
        return super.getNbConnections() + clientsList.size();
    }

    /**
     * Removes disconnected clients.
     */
    protected synchronized void cleanListOfClients() {
        Vector<Integer> disconnectedClientsPeerIds = new Vector<Integer>();
        for (java.util.Map.Entry<Integer, TcpClient> client : clientsList.entrySet()) {
            if (!client.getValue().isConnected()) {
                disconnectedClientsPeerIds.add(client.getKey());
            }
        }
        for (Integer clientPeerIdToRemove : disconnectedClientsPeerIds) {
            clientsList.remove(clientPeerIdToRemove);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.prima.omiscid.com.TcpServer#getPeerId(java.util.Vector)
     */
    @Override
    public int getConnectedPeerIds(Vector<Integer> vec) {
        cleanListOfClients();
        int nb = super.getConnectedPeerIds(vec);
        synchronized (this) {
            for (TcpClient client : clientsList.values()) {
                nb++;
                vec.add(client.getRemotePeerId());
            }
        }
        return nb;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.prima.omiscid.com.TcpServer#removeOmiscidMessageListener(fr.prima.omiscid.com.interf.OmiscidMessageListener)
     */
    @Override
    public void removeOmiscidMessageListener(BipMessageListener listener) {
        cleanListOfClients();
        super.removeOmiscidMessageListener(listener);
        synchronized (this) {
            for (TcpClient client : clientsList.values()) {
                client.removeOmiscidMessageListener(listener);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.prima.omiscid.com.TcpServer#isStillConnected(int)
     */
    @Override
    public boolean isStillConnected(int peerId) {
        if (super.isStillConnected(peerId)) {
            return true;
        } else {
            synchronized (this) {
                cleanListOfClients();
                for (TcpClient client : clientsList.values()) {
                    if (client.isConnected()) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.prima.omiscid.com.TcpServer#close()
     */
    @Override
    public void close() {
        super.close();
        for (TcpClient client : clientsList.values()) {
            client.closeConnection();
        }
    }
}
