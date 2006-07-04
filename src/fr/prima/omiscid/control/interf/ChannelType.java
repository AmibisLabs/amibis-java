package fr.prima.omiscid.control.interf;

/**
 * Enumeration of kind of input/output
 * 
 * @author Sebastien Pesnel refactoring emonet
 */
public enum ChannelType {
    INPUT("Input", GlobalConstants.channelTypeInputXMLTag),
    OUTPUT("Output", GlobalConstants.channelTypeOutputXMLTag),
    INOUTPUT("InOutput", GlobalConstants.channelTypeInOutputXMLTag);

    /** String representation of the input/output kind */
    private String stringRepresentation = null;

    /** String representation in XML request of an input/output kind */
    private String xmlTagName = null;

    /**
     * Creates a new instance of InOutputKind.
     * 
     * @param k
     *            string representation for the kind
     * @param xml
     *            string representation in xml for the kind
     */
    private ChannelType(String k, String xml) {
        stringRepresentation = k;
        xmlTagName = xml;
    }

    /**
     * Accesses to the xml tag.
     */
    public String getXMLTag() {
        return xmlTagName;
    }

    /**
     * Accesses the string representation.
     */
    public String toString() {
        return stringRepresentation;
    }
}