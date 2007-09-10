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
import java.util.HashMap;
import java.util.Vector;

import fr.prima.omiscid.com.interf.BipMessageListener;

/**
 * @author Patrick Reignier (UJF/Gravir)
 */
// \REVIEWTASK shouldn't this be a monitor?
// \REVIEWTASK avoid inheritance whenever possible
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

    public void closeAllConnections() {
        super.closeAllConnections();
        synchronized (this) {
            for (TcpClient tcpClient : clientsList.values()) {
                tcpClient.closeConnection();
            }
            clientsList.clear();
        }        
    }

    @Override
    public boolean closeConnection(int peerId) {
        if (super.closeConnection(peerId)) {
            return true;
        }
        synchronized (this) {
            TcpClient tcpClient = clientsList.get(peerId);
            if (tcpClient != null) {
                tcpClient.closeConnection();
                clientsList.remove(peerId);
                return true;
            }
        }
        return false;
    }
    
    public int getPeer() {return peerId;}

    /**
     * Connects to a remote server.
     *
     * @param host
     *            the host address
     * @param port
     *            the port number
     * @return the peer id of the remote peer
     * @throws IOException 
     */
    public int connectTo(String host, int port) throws IOException {
        TcpClient tcpClient = new TcpClient(peerId);
        for (BipMessageListener listener : listenersSet) {
            tcpClient.addBipMessageListener(listener);
        }
        tcpClient.connectTo(host, port); // throws the IOException
        while (tcpClient.getRemotePeerId() == 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        synchronized (this) {
            if (tcpClient.isConnected()) {
                clientsList.put(tcpClient.getRemotePeerId(), tcpClient);
            }
        }
        return tcpClient.getRemotePeerId();
    }

    public void removeAllBIPMessageListeners() {
        listenersSet.clear();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.prima.omiscid.com.TcpServer#sendToClients(byte[])
     */
    @Override
    public void sendToAllClients(byte[] buffer) {
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
     * @see fr.prima.omiscid.com.TcpServer#addBipMessageListener(fr.prima.omiscid.com.interf.BipMessageListener)
     */
    @Override
    public void addBipMessageListener(BipMessageListener listener) {
        super.addBipMessageListener(listener);
        synchronized (this) {
            cleanListOfClients();
            for (TcpClient client : clientsList.values()) {
                if (client.isConnected()) {
                    client.addBipMessageListener(listener);
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
     * @see fr.prima.omiscid.com.TcpServer#removeBipMessageListener(fr.prima.omiscid.com.interf.BipMessageListener)
     */
    @Override
    public void removeBIPMessageListener(BipMessageListener listener) {
        cleanListOfClients();
        super.removeBIPMessageListener(listener);
        synchronized (this) {
            for (TcpClient client : clientsList.values()) {
                client.removeBipMessageListener(listener);
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
    public synchronized void close() {
        super.close();
        for (TcpClient client : clientsList.values()) {
            client.closeConnection();
        }
        clientsList.clear();
    }
}
