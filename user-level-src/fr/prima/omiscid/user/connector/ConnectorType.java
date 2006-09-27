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

package fr.prima.omiscid.user.connector;

import fr.prima.omiscid.user.util.Constants;

/**
 * Enumeration of kind of input/output
 *
 * @author Sebastien Pesnel refactoring emonet
 */
public enum ConnectorType {
    INPUT("Input",
            Constants.connectorTypeInputXMLTag,
            Constants.prefixForInputInDnssd
            ),
    OUTPUT("Output",
            Constants.connectorTypeOutputXMLTag,
            Constants.prefixForOutputInDnssd
            ),
    INOUTPUT("InOutput",
            Constants.connectorTypeInOutputXMLTag,
            Constants.prefixForInoutputInDnssd
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
