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

package fr.prima.omiscid.control;

import java.util.Collections;
import java.util.List;
import java.util.Vector;


import fr.prima.omiscid.com.CommunicationServer;
import fr.prima.omiscid.com.TcpServer;
import fr.prima.omiscid.control.message.answer.CA_InOutputType;
import fr.prima.omiscid.control.message.answer.ControlAnswerItem;
import fr.prima.omiscid.control.message.answer.Inoutput;
import fr.prima.omiscid.control.message.answer.Input;
import fr.prima.omiscid.control.message.answer.Output;
import fr.prima.omiscid.control.message.answer.Peers;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.util.Utility;

/**
 * Stores an in/output description. The in/output description is composed of a
 * tcp port, udp port, a textual description, a description of the format, an
 * access kind (input, output, or inoutput) and a {@link CommunicationServer}
 * instance that can provide a list of the connected peer. The class exposes
 * methods to generate xml string with the description. Theses strings are used
 * in exhange with the Control Server.
 *
 * @see fr.prima.omiscid.control.ControlServer
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
public class InOutputAttribute extends Attribute {

    /** Kind of in/output */
    private ConnectorType connectorType = ConnectorType.INPUT;

    /**
     * Object used on the ControlServer side to provide data as TCP/UDP port,
     * connected peer.
     */
    private CommunicationServer communicationServer = null;

    /** the tcp port: used on the ControlClient side to store the tcp port */
    private int tcpPort = 0;

    /** the udp port: used on the ControlClient side to store the udp port */
    private int udpPort = 0;

    private int peerId = -1;

    /**
     * Used on the ControlClient side to store the id of connected peers.
     * (vector of Integer objects)
     */
    private Vector<Integer> peerVector = new Vector<Integer>();

    /**
     * Creates a new instance of InOutputAttribute.
     *
     * @param aName
     *            name for the in/output
     */
    public InOutputAttribute(String aName) {
        super(aName);
    }

    public InOutputAttribute(String aName, ConnectorType type, int tcpPort) {
        super(aName);
        this.connectorType = type;
        this.tcpPort = tcpPort;
    }

    /**
     * Creates a new instance of InOutputAttribute.
     *
     * @param aName
     *            name for the in/output
     * @param ct
     *            object associated to this description.
     */
    public InOutputAttribute(String aName, fr.prima.omiscid.com.CommunicationServer ct, int connectorPeerId) {
        super(aName);
        this.communicationServer = ct;
        this.peerId = connectorPeerId;
    }

    public InOutputAttribute(ControlAnswerItem item) {
        super("");
        init(item);
    }


    public void init(ControlAnswerItem item) {
        CA_InOutputType inoutput = null;
        if (item.getChoiceValue() instanceof Input) {
            this.connectorType = ConnectorType.INPUT;
            inoutput = item.getInput();
        } else if (item.getChoiceValue() instanceof Output) {
            connectorType = ConnectorType.OUTPUT;
            inoutput = item.getOutput();
        } else if (item.getChoiceValue() instanceof Inoutput) {
            connectorType = ConnectorType.INOUTPUT;
            inoutput = item.getInoutput();
        } else {
            System.err.println("unhandled ControlAnswerItem type in InOutputAttribute "+item.getChoiceValue());
        }
        if (inoutput.getName() != null) this.setName(inoutput.getName());
        if (inoutput.getDescription() != null) this.setDescription(inoutput.getDescription());
        if (inoutput.getFormatDescription() != null) this.setFormatDescription(inoutput.getFormatDescription());
        if (inoutput.getPeerId() != null) this.setPeerId(Utility.hexStringToInt(inoutput.getPeerId()));
        this.setTcpPort(inoutput.getTcp());
        this.setUdpPort(inoutput.getUdp());
        this.peerVector.clear();
        for (String peer : inoutput.getPeers().getPeer()) {
            addPeer(Utility.hexStringToInt(peer));
        }
    }

    public void init(fr.prima.omiscid.control.message.servicexml.ConnectorType inoutput) {
        // TODO Auto-generated method stub

    }

    public ControlAnswerItem generateControlAnswer() {
        CA_InOutputType inoutput = null;
        switch (getConnectorType()) {
        case INPUT: inoutput = new Input(); break;
        case OUTPUT: inoutput = new Output(); break;
        case INOUTPUT: inoutput = new Inoutput(); break;
        default: System.err.println("unhandled connector type in InOutputAttribute generateControlAnswer");
        }
        inoutput.setDescription(getDescription());
        inoutput.setFormatDescription(getFormatDescription());
        inoutput.setName(getName());
        inoutput.setPeerId(Utility.intTo8HexString(getPeerId()).toLowerCase());
        inoutput.setTcp(getTcpPort());
        if (getUdpPort() != 0) inoutput.setTcp(getTcpPort());
        inoutput.setUdp(getUdpPort());
        Peers peers = new Peers();
        for (int peerId : getPeerVector()) {
            peers.addPeer(Utility.intTo8HexString(peerId).toLowerCase());
        }
        inoutput.setPeers(peers);
        ControlAnswerItem controlAnswerItem = new ControlAnswerItem();
        switch (getConnectorType()) {
        case INPUT: controlAnswerItem.setInput((Input) inoutput); break;
        case OUTPUT: controlAnswerItem.setOutput((Output) inoutput); break;
        case INOUTPUT: controlAnswerItem.setInoutput((Inoutput) inoutput); break;
        default: System.err.println("unhandled connector type in InOutputAttribute generateControlAnswer");
        }
        return controlAnswerItem;
    }

    public ControlAnswerItem generateShortControlAnswer() {
        CA_InOutputType inoutput = null;
        switch (getConnectorType()) {
        case INPUT: inoutput = new Input(); break;
        case OUTPUT: inoutput = new Output(); break;
        case INOUTPUT: inoutput = new Inoutput(); break;
        default: System.err.println("unhandled connector type in InOutputAttribute generateShortControlAnswer");
        }
        inoutput.setName(getName());
        ControlAnswerItem controlAnswerItem = new ControlAnswerItem();
        switch (getConnectorType()) {
        case INPUT: controlAnswerItem.setInput((Input) inoutput); break;
        case OUTPUT: controlAnswerItem.setOutput((Output) inoutput); break;
        case INOUTPUT: controlAnswerItem.setInoutput((Inoutput) inoutput); break;
        default: System.err.println("unhandled connector type in InOutputAttribute generateShortControlAnswer");
        }
        return controlAnswerItem;
    }


    /**
     * Sets the kind of in/output.
     *
     * @param aKind
     *            kind of in/output
     */
    public void setConnectorType(ConnectorType connectorType) {
        this.connectorType = connectorType;
    }

    /**
     * Gets the kind of in/output
     *
     * @return the kind of in/output
     */
    public ConnectorType getConnectorType() {
        return connectorType;
    }

    /**
     * Tests whether this attribute represents exactly an input connector.
     *
     * @return whether it is an input connector
     */
    public boolean isInput() {
        return connectorType == ConnectorType.INPUT;
    }

    /**
     * Tests whether this attribute represents exactly an output connector.
     *
     * @return whether it is an output connector
     */
    public boolean isOutput() {
        return connectorType == ConnectorType.OUTPUT;
    }

    /**
     * Tests whether this attribute represents exactly an inoutput connector.
     *
     * @return whether it is an inoutput connector
     */
    public boolean isInOutput() {
        return connectorType == ConnectorType.INOUTPUT;
    }

    /**
     * Sets the TCP port value. Used by the ControlClient when it receives an
     * in/output description
     *
     * @param tcp
     *            the port number
     */
    public void setTcpPort(int tcp) {
        tcpPort = tcp;
    }

    /**
     * Sets the UDP port value. Used by the ControlClient when it receives an
     * in/output description
     *
     * @param udp
     *            the port number
     */
    public void setUdpPort(int udp) {
        udpPort = udp;
    }

    /**
     * Accesses the TCP port.
     *
     * @return the tcp port, 0 if it does not exist.
     */
    public int getTcpPort() {
        if (communicationServer == null) {
            return tcpPort;
        } else {
            return communicationServer.getTcpPort();
        }
    }

    /**
     * Accesses the UDP port
     *
     * @return the udp port, 0 if it does not exist.
     */
    public int getUdpPort() {
        if (communicationServer == null) {
            return udpPort;
        } else {
            return communicationServer.getUdpPort();
        }
    }

    /**
     * Accesses the list of the id of the connected peer
     *
     * @return the list of id of connected peer
     */
    public List<Integer> getPeerVector() {
        if (communicationServer != null) {
            peerVector.clear();
            communicationServer.getConnectedPeerIds(peerVector);
        }
        return Collections.unmodifiableList(peerVector);
    }

    /**
     * Adds a peer id to list of connected peer. Used by ControlClient when it
     * receives a description for an in/output
     *
     * @param peer
     *            id of a connected peer
     */
    public void addPeer(int peer) {
        peerVector.add(new Integer(peer));
    }

    /**
     * Generates a string with port number.
     *
     * @return a string with the TCP port following by "/" and the udp port
     */
    public String generateRecordData() {
        String str = "";
        int t = getTcpPort();
        int u = getUdpPort();
        if (t != 0) {
            str += t;
        }
        if (u != 0) {
            str += "/" + u;
        }
        return str;
    }

    /**
     * Returns the name for the in/output.
     *
     * @return the name for the in/output
     */
    public String toString() {
        return getName();
    }

    public CommunicationServer getCommunicationServer() {
        return communicationServer;
    }

    public void setCommunicationServer(CommunicationServer communicationServer) {
        this.communicationServer = communicationServer;
        this.setPeerId(this.getPeerId());
    }

    public int getPeerId() {
        return peerId;
    }

    void setPeerId(int peerId) {
        this.peerId = peerId;
        if (communicationServer != null) {
            if (communicationServer instanceof TcpServer) {
                ((TcpServer)this.communicationServer).setPeerId(peerId);  
            } else {
                System.err.println("Warning! : unhandled case in setPeerId in InOutputAttribute "+communicationServer);
            }
        }
    }

}
