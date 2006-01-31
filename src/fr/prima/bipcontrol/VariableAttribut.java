package fr.prima.bipcontrol ;

import java.util.Set;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/** Stores Variable Description.
 * The variable description is composed of a value, a default value, a type
 * a description, a description for the format, a kind of access 
 * (Read, Read-Write, Read-Write before init).
 * <br>
 * The class gives methods to generate xml string with the description.
 * Theses strings are used in exhange with the Control Server.
 * <br>
 * It also accepts several listeners interested in the variable modification.
 * <br>
 * Note: VariableAttribut do not contains the variable but only a description.
 * The user must change the description when the variable change,
 * for example when the value is modified. This coherence can be maintened with objects as IntVariableAttribut.
 * IntVariableAttribut puts together the description for an int and the integer.
 * It provides accessors that maintains the coherence between the description and the integer when the integer is modified.
 * 
 * @see fr.prima.bipcontrol.ControlServer
 * @see fr.prima.bipcontrol.IntVariableAttribut
 * 
 * @author Sebastien Pesnel
 * Refactoring by Patrick Reignier
 */
public class VariableAttribut extends Attribut {

    /** Class to manage a enumerated type for the kind of access on a variable.
     * The values are :
     * <ul>
     * <li> read
     * </li><li> read_write
     * </li><li> read_write_before_init
     * </li></ul>
     * The string that are above are the symbol used in xml message.*/
    static final class AccessKind{
        /** The string used in the XML messages */
        private String str;
        /** Constructor only used in the VariableAttribut class *
         * @param s the string associated to this kind of access. */
        private AccessKind(String s){
            str = s;
        };
        /** @return the string ssociated to this kind of access */
        public String toString(){ return str; }
    };
    /** Object for Read Access*/
    public static final AccessKind READ = new AccessKind("read");
    /** Object for Read-Write Access*/
    public static final AccessKind READ_WRITE = new AccessKind("read_write");
    /** Object for Read-Write Access before init */
    public static final AccessKind READ_WRITE_BEFORE_INIT = new AccessKind("read_write_before_init");

    /** the value for the variable (string representation)*/
    private String valueStr = null;
    /** the type for the variable */
    private String type = null;
    /** the default value for the variable (string representation)*/
    private String defaultValue = null;
    /** the kind of access on the variable */
    private AccessKind accessKind = READ;
    /** A set of listener interested in variable modification.
     * A set of object implementing the {@link VariableChangeListener} interface. */
    private Set<VariableChangeListener> listenerSet = new java.util.HashSet<VariableChangeListener>();
    /** A set of peer interested in the variable modification.
     * A set of Integer object.*/
    private Set<Integer> peerInterestedIn = new java.util.HashSet<Integer>();

    /** Creates a new instance of VariableAttribut
     * @param aName the name of the variable */
    public VariableAttribut(String aName) {
        super(aName);
    }
    /** Access to the value 
     * @return the value */
    public String getValueStr() {
        return valueStr;
    }
    /** Access to the type 
     * @return the type */
    public String getType() {
        return type;
    }
    /** Access to the default value
     * @return the default value */
    public String getDefaultValue() {
        return defaultValue;
    }
    /** Access to the kind of access 
     * @return the kind of access */
    public AccessKind getAccess() {
        return accessKind;
    }
    /** Access to the string representation of the kind of access.
     * @return the string associated to the kind of access */
    public String getAccessString() {
        return accessKind.toString();
    }
    /** Test if the variable can be modified according to its kind of access.
     * A variable can be modified if its kind of access is Read-Write, 
     * or Read-Write-before-init if the service is not running 
     * (that is to say that the service has a current status different of 2). 
     * @return if the variable can be modified */
    public boolean canBeModified(int status) {
        return (accessKind == READ_WRITE)
                || (accessKind == READ_WRITE_BEFORE_INIT && status != ControlServer.STATUS_RUNNING);
    }
    /** Defines the value for the variable 
     * @param str the new value for the variable */
    public void setValueStr(String str) {
        valueStr = str;
        valueChanged();
    }
    /** Defines the type for the variable 
     * @param t the new type for the variable */
    public void setType(String t) {
        type = t;
    }
    /** Defines the default value for the variable 
     * @param dv the new default value */
    public void setDefaultValue(String dv) {
        defaultValue = dv;
    }
    /** Defines the kind of access for the variable
     * @param a the new kind of access */
    public void setAccess(AccessKind a) {
        accessKind = a;
    }
    /** Addss a listener interested in variable modification 
     * @param l the new listener */
    public void addListenerChange(VariableChangeListener l) {
        synchronized(listenerSet){
            listenerSet.add(l);
        }
    }
    /** Removes a listener no more interested in variable modification
     * @param l the listener to remove */
    public void removeListenerChange(VariableChangeListener l) {
        synchronized(listenerSet){
            listenerSet.remove(l);
        }
    }
    /** Signal to the listeners that the value has been modified.
     * This method is called in {@link VariableAttribut#setValueStr(String)}*/
    private void valueChanged() {
        synchronized(listenerSet){
            java.util.Iterator<VariableChangeListener> it = listenerSet.iterator();
            while (it.hasNext())
                it.next().changeOccured(this);
        }
    }
    /** Adds the id of a peer interested in variable modification.
     * @param pid the id of the peer */
    public void addPeer(int pid) {
        peerInterestedIn.add(new Integer(pid));
    }
    /** Removes an id of a peer no more interested in variable modification.
     * @param pid the id of the peer to remove */
    public void removePeer(int pid) {
        peerInterestedIn.remove(new Integer(pid));
    }
    /** Removes an id of a peer no more interested in variable modification.
     * @param p contains the id of the peer to remove */
    public void removePeer(Integer p) {
        peerInterestedIn.remove(p);
    }
    /** Access to the list of peer interested in the variable modification 
     * @return a set of Integer object. Their value are the id of peer. */
    public Set<Integer> getPeerInterestedIn() {
        return peerInterestedIn;
    }
    
    /** Generates a short description of the variable.
     * The description has an XML format, 
     * and contains only the name of the variable.
     * It is used in control server to generate a short global description of the service.
     * @return &lt;variable name=&quot;variable_name&quot;/&gt; 
     * @see ControlServer#generateShortGlobalDescription() */
    public String generateShortDescription() {
        return generateHeaderDescription("variable", true);
    }
    /** Generates a long description of the variable.
     * The description has an XML format,
     * and contains all the information available about the variable.
     * It is used by the control server to answer to query about variable 
     * @return the XML description of the variable 
     * @see ControlServer */
    public String generateLongDescription() {
        String str = generateHeaderDescription("variable", false);
        // System.out.println("generateLongDescription : " + getValueStr());
        str += "<value>" + XmlUtils.generateCDataSection(getValueStr()) + "</value>";
        if (defaultValue != null && !defaultValue.equals(""))
            str += "<default>" + XmlUtils.generateCDataSection(defaultValue)
                    + "</default>";
        str += "<access>" + getAccessString() + "</access>";
        str += "<type>" + getType() + "</type>";
        str += generateTagDescriptionToStr();
        str += "</variable>";
        return str;
    }
    /** Generates a XML message containing the variable name and its value.
     * The message has the form :
     * <br>
     * &lt;variable name=&quot;variable_name&quot;&gt;<br>
     * &lt;value&gt;&lt;![CDATA[variable_value]]&gt;&lt;/value&gt;<br>
     * &lt;/variable&gt;
     * @return the generated XML message*/
    public String generateValueMessage() {
        String str = generateHeaderDescription("variable", false);
        str += "<value>" + XmlUtils.generateCDataSection(getValueStr()) + "</value>";
        str += "</variable>";
        return str;
    }
    /** Returns the name of the variable
     * To have a more complete description to display,
     * {@link VariableAttribut#generateLongDescription()} can be used. 
     * @return the name of the variable */
    public String toString() {
        return getName();
    }
    
    /** Extract the information to initialize the field of the variable from
     * a XML document.
     * @param elt the element of the XML description 
     */
    public void extractInfoFromXML(Element elt){
        String tmpValue = null;
        
        NodeList nodeList = elt.getChildNodes();
        for(int i =0; i<nodeList.getLength(); i++){       
            Node current = nodeList.item(i);
            if(current.getNodeType() == Node.ELEMENT_NODE){       
                String currentName = current.getNodeName();
                if (currentName.equals("description")) {
                    setDescription(current.getTextContent());
                } else if (currentName.equals("formatDescription")) {
                    setFormatDescription(current.getTextContent());
                } else if (currentName.equals("value")) {
                    tmpValue = current.getTextContent();
                } else if (currentName.equals("default")) {
                    setDefaultValue(current.getTextContent());
                } else if (currentName.equals("type")) {
                    setType(current.getTextContent());
                } else if (currentName.equals("access")) {
                    setAccess(current.getTextContent());
                } else {
                    System.err.println("VariableAttribut::extractInfoFromXml : Unwaited Tag : " + currentName);
                }
            }
        }
        if(tmpValue != null){
            setValueStr(tmpValue);
        }
    }      
    
    /** Define the kind of access according to the string representation */
    protected void setAccess(String accessStr){
        if (accessStr.equals(VariableAttribut.READ.toString())) {
            setAccess(VariableAttribut.READ);
        } else if (accessStr.equals(VariableAttribut.READ_WRITE.toString())) {
            setAccess(VariableAttribut.READ_WRITE);
        } else if (accessStr.equals(VariableAttribut.READ_WRITE_BEFORE_INIT.toString())) {
            setAccess(VariableAttribut.READ_WRITE_BEFORE_INIT);
        }        
    }
    
    public Element createXmlElement(Document doc){
        Element eltVar = doc.createElement("variable");
        eltVar.setAttribute("name", getName());
        
        Element elt = null;
        CDATASection cdata = null;
        
        elt = doc.createElement("access");
        elt.setTextContent(getAccessString());
        eltVar.appendChild(elt);
        
        elt = doc.createElement("value");
        cdata = doc.createCDATASection(getValueStr());
        elt.appendChild(cdata);
        eltVar.appendChild(elt);
        
        if (defaultValue != null && !defaultValue.equals(""))
        {
            elt = doc.createElement("default");
            cdata = doc.createCDATASection(defaultValue);
            elt.appendChild(cdata);
            eltVar.appendChild(elt);
        }
        
        elt = doc.createElement("type");
        elt.setTextContent(getType());
        eltVar.appendChild(elt);
        
        if (getDescription() != null && !getDescription().equals("")){              
            elt = doc.createElement("description");
            cdata = doc.createCDATASection(getDescription());
            elt.appendChild(cdata);
            eltVar.appendChild(elt);
        }
        if (getFormatDescription() != null
                && !getFormatDescription().equals(""))
        {
            elt = doc.createElement("formatDescription");
            cdata = doc.createCDATASection(getFormatDescription());
            elt.appendChild(cdata);
            eltVar.appendChild(elt);
        }        
        return eltVar;
    }
}
