package fr.prima.omiscid.control.interf;

/**
 * Enumeration of kind of input/output
 * 
 * @author Sebastien Pesnel
 */
public class InOutputKind {

    public static final InOutputKind Input = new InOutputKind("Input", "input");

    public static final InOutputKind Output = new InOutputKind("Output", "output");

    public static final InOutputKind InOutput = new InOutputKind("InOutput", "inoutput");

    /** String representation of the input/output kind */
    private String kind = null;

    /** String representation in XML request of an input/output kind */
    private String xmlKind = null;

    /**
     * Creates a new instance of InOutputKind.
     * 
     * @param k
     *            string representation for the kind
     * @param xml
     *            string representation in xml for the kind
     */
    private InOutputKind(String k, String xml) {
        kind = k;
        xmlKind = xml;
    }

    /**
     * Accesses to the xml tag.
     */
    public String getXMLTag() {
        return xmlKind;
    }

    /**
     * Accesses the string representation.
     */
    public String toString() {
        return kind;
    }
}