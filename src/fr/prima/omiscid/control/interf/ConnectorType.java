package fr.prima.omiscid.control.interf;

/**
 * Enumeration of kind of input/output
 *
 * @author Sebastien Pesnel refactoring emonet
 */
public enum ConnectorType {
    INPUT("Input",
            GlobalConstants.connectorTypeInputXMLTag,
            GlobalConstants.prefixForInputInDnssd
            ),
    OUTPUT("Output",
            GlobalConstants.connectorTypeOutputXMLTag,
            GlobalConstants.prefixForOutputInDnssd
            ),
    INOUTPUT("InOutput",
            GlobalConstants.connectorTypeInOutputXMLTag,
            GlobalConstants.prefixForInoutputInDnssd
            );

    /** String representation of the input/output kind */
    private String stringRepresentation = null;

    /** String representation in XML request of an input/output kind */
    private String xmlTagName = null;

    private String prefixInDnssd;

    /**
     * Creates a new instance of InOutputKind.
     *
     * @param k
     *            string representation for the kind
     * @param xml
     *            string representation in xml for the kind
     */
    private ConnectorType(String k, String xml, String prefixInDnssd) {
        this.stringRepresentation = k;
        this.xmlTagName = xml;
        this.prefixInDnssd = prefixInDnssd;
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

    public String getPrefixInDnssd() {
        return prefixInDnssd;
    }

    public static ConnectorType fromDnssdValue(String propertyValue) {
        if (propertyValue.startsWith(OUTPUT.prefixInDnssd)) {
            return OUTPUT;
        } else if (propertyValue.startsWith(INPUT.prefixInDnssd)) {
            return INPUT;
        } else if (propertyValue.startsWith(INOUTPUT.prefixInDnssd)) {
            return INOUTPUT;
        }
        return null;
    }

    public static String realValueFromDnssdValue(String propertyValue) {
        ConnectorType connectorType = fromDnssdValue(propertyValue);
        return connectorType == null ?
                null : propertyValue.replaceFirst(connectorType.getPrefixInDnssd(), "");
    }

}