package fr.prima.omiscid.control;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.prima.omiscid.com.BipUtils;
import fr.prima.omiscid.com.CommunicationServer;
import fr.prima.omiscid.control.interf.ConnectorType;

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
    private ConnectorType channelType = ConnectorType.INPUT;

    /**
     * Object used on the ControlServer side to provide data as TCP/UDP port,
     * connected peer.
     */
    private CommunicationServer communicationServer = null;

    /** the tcp port: used on the ControlClient side to store the tcp port */
    private int tcpPort = 0;

    /** the udp port: used on the ControlClient side to store the udp port */
    private int udpPort = 0;

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

    /**
     * Creates a new instance of InOutputAttribute.
     *
     * @param aName
     *            name for the in/output
     * @param ct
     *            object associated to this description.
     */
    public InOutputAttribute(String aName, fr.prima.omiscid.com.CommunicationServer ct) {
        super(aName);
        communicationServer = ct;
    }

    /**
     * Sets the kind of in/output.
     *
     * @param aKind
     *            kind of in/output
     */
    public void setChannelType(ConnectorType channelType) {
        this.channelType = channelType;
    }

    /**
     * Gets the kind of in/output
     *
     * @return the kind of in/output
     */
    public ConnectorType getChannelType() {
        return channelType;
    }

    /**
     * Tests whether this attribute represents exactly an input channel.
     *
     * @return whether it is an input channel
     */
    public boolean isInput() {
        return channelType == ConnectorType.INPUT;
    }

    /**
     * Tests whether this attribute represents exactly an output channel.
     *
     * @return whether it is an output channel
     */
    public boolean isOutput() {
        return channelType == ConnectorType.OUTPUT;
    }

    /**
     * Tests whether this attribute represents exactly an inoutput channel.
     *
     * @return whether it is an inoutput channel
     */
    public boolean isInOutput() {
        return channelType == ConnectorType.INOUTPUT;
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
     * Generates a short XML description of the in/output.
     *
     * @return the name of the in/output as &lt;inoutput_kind
     *         name=&quot;inoutput_name&quot;/&gt;
     */
    public String generateShortDescription() {
        return generateHeaderDescription(channelType.getXMLTag(), true);
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
     * Generates a long XML description of the in/output.
     *
     * @return the XML description as a String
     */
    public String generateLongDescription() {
        String str = generateHeaderDescription(channelType.getXMLTag(), false);
        if (getTcpPort() != 0) {
            str += "<tcp>" + getTcpPort() + "</tcp>";
        }
        if (getUdpPort() != 0) {
            str += "<udp>" + getUdpPort() + "</udp>";
        }
        str += generateTagDescriptionToStr();
        str += "<peers>";
        for (Integer peerId : getPeerVector()) {
            str += "<peer>" + BipUtils.intTo8HexString(peerId) + "</peer>";
        }
        str += "</peers></" + channelType.getXMLTag() + ">";
        return str;
    }

    /**
     * Generates a string containing the answer to a connect query.
     *
     * @return the answer to a connect query
     */
    public String generateConnectAnswer() {
        return generateLongDescription();
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

    /**
     * Extracts the information to initialize the field of the input/output
     * description from a XML document.
     *
     * @param elt
     *            the element of the XML description
     */
    public void extractInfoFromXML(Element elt) {
        NodeList nodeList = elt.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node current = nodeList.item(i);
            if (current.getNodeType() == Node.ELEMENT_NODE) {
                String currentName = current.getNodeName();
                if (currentName.equals("description")) {
                    setDescription(current.getTextContent());
                } else if (currentName.equals("formatDescription")) {
                    setFormatDescription(current.getTextContent());
                } else if (currentName.equals("tcp")) {
                    setTcpPort(Integer.parseInt(current.getTextContent()));
                } else if (currentName.equals("udp")) {
                    setUdpPort(Integer.parseInt(current.getTextContent()));
                } else if (currentName.equals("peers")) {
                    NodeList listPeer = current.getChildNodes();
                    for (int p = 0; p < listPeer.getLength(); p++) {
                        Node peerNode = listPeer.item(p);
                        if (peerNode.getNodeName().equals("peer")) {
                            addPeer(BipUtils.hexStringToInt(peerNode.getTextContent()));
                        }
                    }
                } else {
                    System.err.println("InOutputAttribute::extractInfoFromXML : Unexpected Tag : " + currentName);
                }
            }
        }
    }

    static public ConnectorType IOKindFromName(String str) {
        if (str.equals(ConnectorType.INOUTPUT.getXMLTag())) {
            return ConnectorType.INOUTPUT;
        } else if (str.equals(ConnectorType.OUTPUT.getXMLTag())) {
            return ConnectorType.OUTPUT;
        } else if (str.equals(ConnectorType.INPUT.getXMLTag())) {
            return ConnectorType.INPUT;
        } else {
            return null;
        }
    }

    public Element createXmlElement(Document doc) {
        Element eltIo = doc.createElement(channelType.getXMLTag());
        eltIo.setAttribute("name", getName());

        Element elt = null;
        CDATASection cdata = null;

        elt = doc.createElement("tcp");
        elt.setTextContent(Integer.toString(getTcpPort()));
        eltIo.appendChild(elt);

        elt = doc.createElement("udp");
        elt.setTextContent(Integer.toString(getUdpPort()));
        eltIo.appendChild(elt);

        if (getDescription() != null && !getDescription().equals("")) {
            elt = doc.createElement("description");
            cdata = doc.createCDATASection(getDescription());
            elt.appendChild(cdata);
            eltIo.appendChild(elt);
        }
        if (getFormatDescription() != null && !getFormatDescription().equals("")) {
            elt = doc.createElement("formatDescription");
            cdata = doc.createCDATASection(getFormatDescription());
            elt.appendChild(cdata);
            eltIo.appendChild(elt);
        }

        List<Integer> v = getPeerVector();
        if (!v.isEmpty()) {
            Element eltPeers = doc.createElement("peers");
            for (Integer peerId : v) {
                elt = doc.createElement("peer");
                elt.setTextContent(BipUtils.intTo8HexString(peerId));
                eltPeers.appendChild(elt);
            }
            eltIo.appendChild(eltPeers);
        }

        return eltIo;
    }

    public CommunicationServer getCommunicationServer() {
        return communicationServer;
    }

    public void setCommunicationServer(CommunicationServer communicationServer) {
        this.communicationServer = communicationServer;
    }

}
