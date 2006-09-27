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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.prima.omiscid.user.connector.ConnectorType;

/**
 * Group methods to initialize service from an XML file. The XML file contains
 * the service description : the service name, the variable, the input, output
 * and in_output Initialize a ControlServer object, add variables, add the
 * in/outputs description.
 * 
 * @author Sebastien Pesnel
 */
public class XmlToService {

    public static String getServiceName(Element elt) {
        return elt.getAttribute("name");
    }

    public static void initServiceFromXml(ControlServer ctrlServer, Element elt) {
        NodeList nodeList = elt.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = node.getNodeName();
                if (nodeName.equals("variable")) {
                    createVariableFromXml(ctrlServer, (Element) node);
                } else if (nodeName.equals(ConnectorType.INPUT.getXMLTag()) || nodeName.equals(ConnectorType.OUTPUT.getXMLTag())
                        || nodeName.equals(ConnectorType.INOUTPUT.getXMLTag())) {
                    createIOFromXml(ctrlServer, (Element) node);
                }
            }
        }
    }

    public static void createVariableFromXml(ControlServer ctrlServer, Element elt) {
        String varName = elt.getAttribute("name");
        VariableAttribute va = ctrlServer.findVariable(varName);
        if (va == null)
            va = ctrlServer.addVariable(varName);
        va.extractInfoFromXML(elt);
    }

    public static void createIOFromXml(ControlServer ctrlServer, Element elt) {
        String ioName = elt.getAttribute("name");
        ConnectorType iok = InOutputAttribute.IOKindFromName(elt.getNodeName());
        InOutputAttribute ioa = ctrlServer.findInOutput(ioName, iok);
        if (ioa == null)
            ioa = ctrlServer.addInOutput(ioName, null, iok);
        ioa.extractInfoFromXML(elt);
    }
}
