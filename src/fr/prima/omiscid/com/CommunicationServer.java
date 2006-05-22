package fr.prima.omiscid.com;

import java.util.Vector;

/**
 * Represents a communication server (intended to be implemented by tcp and udp
 * servers)
 *
 * @author emonet
 */
//\REVIEWTASK is this intended to represent a local or a remote server or both?
public interface CommunicationServer {
    int getTcpPort();

    int getUdpPort();

    int getConnectedPeerIds(Vector<Integer> vec);
}
