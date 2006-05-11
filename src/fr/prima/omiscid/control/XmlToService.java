package fr.prima.omiscid.control;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.prima.omiscid.control.interf.InOutputKind;

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
                } else if (nodeName.equals(InOutputAttribute.Input.getXMLTag()) || nodeName.equals(InOutputAttribute.Output.getXMLTag())
                        || nodeName.equals(InOutputAttribute.InOutput.getXMLTag())) {
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
        InOutputKind iok = InOutputAttribute.IOKindFromName(elt.getNodeName());
        InOutputAttribute ioa = ctrlServer.findInOutput(ioName, iok);
        if (ioa == null)
            ioa = ctrlServer.addInOutput(ioName, null, iok);
        ioa.extractInfoFromXML(elt);
    }
}
