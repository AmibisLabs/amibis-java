package fr.prima.bipcontrol ;

import fr.prima.bipcom.MsgSocket;
import fr.prima.bipcontrol.interf.InOutputKind;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Stores In/Output Description. The in/output description is composed of a tcp port, udp port,  a description, a description for the format, a kind  (input, output, or in_output) and a object ComTools that can provides the connected peer. <br> The class gives methods to generate xml string with the description. Theses strings are used in exhange with the Control Server. <br>
 * @see fr.prima.bipcontrol.ControlServer
 * @author  Sebastien Pesnel  Refactoring by Patrick Reignier
 */
public class InOutputAttribut extends Attribut {
    /** Kind input */
    public static final InOutputKind Input = InOutputKind.Input;
    /** Kind output */
    public static final InOutputKind Output = InOutputKind.Output;
    /** Kind in_output */
    public static final InOutputKind InOutput = InOutputKind.InOutput;
    
    /** Kind of in/output */
    private InOutputKind kind = Input;
    /** Object used on the ControlServer side to provide data as TCP/UDP port, 
     * connected peer. */
    private fr.prima.bipcom.ComTools comTool = null;
    /** the tcp port : used on the ControlClient side to store the tcp port */
    private int tcpPort = 0;
    /** the udp port : used on the ControlClient side to store the udp port */
    private int udpPort = 0;
    /** Used on the ControlClient side to store the id of connected peers.
     * (vector of Integer objects)*/
    private java.util.Vector<Integer> peerVector = new java.util.Vector<Integer>();
    
    /** Creates a new instance of InOutputAttribut 
     * @param aName name for the in/output */
    public InOutputAttribut(String aName) {
        super(aName);
    }
    /** Creates a new instance of InOutputAttribut 
     * @param aName name for the in/output 
     * @param ct object associated to this description.
     * */
    public InOutputAttribut(String aName, fr.prima.bipcom.ComTools ct) {
        super(aName);
        comTool =ct;
    }
    /**
	 * Defines the kind of in/output
	 * @param aKind  kind of in/output
	 * @uml.property  name="kind"
	 */
    public void setKind(InOutputKind aKind) {
        kind = aKind;
    }
    /**
	 * Returns the kind of in/output
	 * @return  the kind of in/output
	 * @uml.property  name="kind"
	 */
    public InOutputKind getKind() {
        return kind;
    }
 
    /** Returns if the kind of this in/output is input */
    public boolean isInput() {
        return kind == Input;
    }
    /** Returns if the kind of this in/output is output */
    public boolean isOutput() {
        return kind == Output;
    }
    /** Returns if the kind of this in/output is inOutput */
    public boolean isInOutput() {
        return kind == InOutput;
    }
    /**
	 * Defines the TCP port value. Used by the ControlClient when it receives an in/output description 
	 * @param tcp  the port number
	 * @uml.property  name="tcpPort"
	 */
    public void setTcpPort(int tcp) {
        tcpPort = tcp;
    }
    /**
	 * Defines the UDP port value. Used by the ControlClient when it receives an in/output description 
	 * @param udp  the port number
	 * @uml.property  name="udpPort"
	 */
    public void setUdpPort(int udp) {
        udpPort = udp;
    }
    /**
	 * Access to the TCP port
	 * @return  the tcp port, 0 if it does not exist.
	 * @uml.property  name="tcpPort"
	 */
    public int getTcpPort() {
        if (comTool == null)
            return tcpPort;
        else
            return comTool.getTcpPort();
    }
    /**
	 * Access to the UDP port
	 * @return  the udp port, 0 if it does not exist.
	 * @uml.property  name="udpPort"
	 */
    public int getUdpPort() {
        if (comTool == null)
            return udpPort;
        else
            return comTool.getUdpPort();
    }
    /** Generates a short XML description of the in/output 
     * @return the name of the in/output as &lt;inoutput_kind name=&quot;inoutput_name&quot;/&gt;*/
    public String generateShortDescription() {
        return generateHeaderDescription(kind.getXMLTag(), true);
    }
    /**
	 * Access to the list of the id of the connected peer 
	 * @return  the list of id of connected peer
	 * @uml.property  name="peerVector"
	 */
    public java.util.Vector<Integer> getPeerVector(){
        if(comTool != null){
            peerVector.clear();
            comTool.getPeerId(peerVector);
        }
        return peerVector; 
    }
    /** Add a peer id to list of connected peer.
     * Used by ControlClient when it receives a description for an in/output
     * @param peer id of a connected peer */
    public void addPeer(int peer) {
        //System.out.println("InOutputAttribut::addPeer");
        peerVector.add(new Integer(peer));
    }
    /** Generates a long XML description of the in/output
     * @return the XML description */
    public String generateLongDescription() {
        String str = generateHeaderDescription(kind.getXMLTag(), false);

        if (getTcpPort() != 0) {
            str += "<tcp>" + getTcpPort() + "</tcp>";
        }
        if (getUdpPort() != 0) {
            str += "<udp>" + getUdpPort() + "</udp>";
        }

        str += generateTagDescriptionToStr();

        str += "<peers>";
        
        java.util.Iterator<Integer> it = getPeerVector().iterator();
        while (it.hasNext()) {
            str += "<peer>" + fr.prima.bipcom.MsgSocket.intTo8HexString(it.next().intValue()) + "</peer>";
        }
        str += "</peers></" + kind.getXMLTag() + ">";
        return str;
    }
    /** Generates a string containing the answer to a connect query 
     * @return the answer to a connect query */
    public String generateConnectAnswer() {
        return generateLongDescription();
    }
    /** Generates a string with port number.
     * @return a string with the TCP port following by "/" and the udp port */
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
    /** Returns the name for the in/output
     * @return the name for the in/output */
    public String toString() {
        return getName();
    }
    
    /**
	 * Define the communication tool associated to this description
	 * @uml.property  name="comTool"
	 */
    public void setComTool(fr.prima.bipcom.ComTools comTool){
        this.comTool = comTool;
    }
    
    /** Extract the information to initialize the field of the input/output description from
     * a XML document.
     * @param elt the element of the XML description 
     */
    public void extractInfoFromXML(Element elt){        
        NodeList nodeList = elt.getChildNodes();
        for(int i=0; i<nodeList.getLength(); i++){
            Node current = nodeList.item(i);
            if(current.getNodeType() == Node.ELEMENT_NODE){
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
                    for(int p=0; p<listPeer.getLength(); p++){                        
                        Node peerNode = listPeer.item(p);
                        if (peerNode.getNodeName().equals("peer")){
                            addPeer(MsgSocket.hexStringToInt(peerNode.getTextContent()));
                        }
                    }
                } else {
                    System.err.println("InOutputAttribut::extractInfoFromXML : Unwaited Tag : " + currentName);
                }
            }
        }
    }
    
    static public InOutputKind IOKindFromName(String str){
        if (str.equals(InOutput.getXMLTag()))
            return InOutput;
        else if (str.equals(Output.getXMLTag()))
            return Output;
        else if (str.equals(Input.getXMLTag()))
            return Input;
        return null;
    }
    
    public Element createXmlElement(Document doc){
        Element eltIo = doc.createElement(kind.getXMLTag());
        eltIo.setAttribute("name", getName());
        
        Element elt = null;
        CDATASection cdata = null;
        
        elt = doc.createElement("tcp");
        elt.setTextContent(Integer.toString(getTcpPort()));
        eltIo.appendChild(elt);
        
        elt = doc.createElement("udp");
        elt.setTextContent(Integer.toString(getUdpPort()));
        eltIo.appendChild(elt);
        
        
        if (getDescription() != null && !getDescription().equals("")){ 
            elt = doc.createElement("description");
            cdata = doc.createCDATASection(getDescription());
            elt.appendChild(cdata);
            eltIo.appendChild(elt);
        }
        if (getFormatDescription() != null
                && !getFormatDescription().equals(""))
        {
            elt = doc.createElement("formatDescription");
            cdata = doc.createCDATASection(getFormatDescription());
            elt.appendChild(cdata);
            eltIo.appendChild(elt);
        } 

        
        
        java.util.Vector<Integer> v = getPeerVector();
        if(!v.isEmpty()){
            Element eltPeers = doc.createElement("peers");
            java.util.Iterator<Integer> it = v.iterator();
            while (it.hasNext()) {
                elt = doc.createElement("peer");            
                elt.setTextContent(fr.prima.bipcom.MsgSocket.intTo8HexString(it.next().intValue()));
                eltPeers.appendChild(elt);            
            }
            eltIo.appendChild(eltPeers);
        }
        
        return eltIo;
    }
    
    
	/**
	 * @return  Returns the comTool.
	 * @uml.property  name="comTool"
	 */
	public fr.prima.bipcom.ComTools getComTool() {
		return comTool;
	}
}
