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

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility class. Provides static methods to do some conversion between string
 * and xml.
 */
// \REVIEWTASK most of this class should to be rewritten or deleted (objective:
// generate clean xml in any case and move code to ...com.BipUtils )
public class XmlUtils {

    /**
     * Extracts from the given Element the first child having the given name.
     *
     * @param element
     * @param name
     * @return the first child with the given name or null if none
     */
    public static Element firstChild(Element element, String name) {
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node current = nodeList.item(i);
            if (current.getNodeType() == Node.ELEMENT_NODE && current.getNodeName().equals(name)) {
                return (org.w3c.dom.Element) current;
            }
        }
        return null;
    }

    public static String xmlDocToString(Document doc) {
        return elementToString(doc.getDocumentElement());
    }

    public static String elementToString(Element elt) {
        return elementToString(elt, "");
    }

    /**
     * Adds a CData tag around a string
     *
     * @param content
     *            the string to "protect"
     * @return "&lt;![CDATA[" + content + "]]&gt;"
     */
    public static String generateCDataSection(String content) {
        return "<![CDATA[" + content + "]]>";
    }

    public static String elementToString(Element elt, String prefix) {
        String str = prefix + "<" + elt.getNodeName();

//      XERCES
//        NamedNodeMap nnm = elt.getAttributes();
//        for (int i = 0; i < nnm.getLength(); i++) {
//            str += " " + nnm.item(i).getNodeName() + "=\"" + nnm.item(i).getNodeValue() + "\"";
//        }
//
//        boolean hasSubElt = false;
//        NodeList nodeList = elt.getChildNodes();
//        String str2 = "";
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            Node cur = nodeList.item(i);
//            if (cur.getNodeType() == Node.ELEMENT_NODE) {
//                str2 += elementToString((Element) cur, prefix + "  ");
//                hasSubElt = true;
//            } else if (cur.getNodeType() == Node.CDATA_SECTION_NODE) {
//                str2 += prefix + "  " + generateCDataSection(cur.getTextContent());
//                hasSubElt = true;
//            }
//        }
//
//        if (hasSubElt) {
//            str += ">\r\n" + str2 + prefix + "</" + elt.getNodeName() + ">\r\n";
//        } else {
//            str2 = elt.getTextContent();
//            if (str2.equals("")) {
//                str += "/>\r\n";
//            } else {
//                str += ">" + str2 + "</" + elt.getNodeName() + ">\r\n";
//            }
//        }
        return str;

    }

    // /** parser used to change message into XML tree */
    // private static DocumentBuilder parser = null;
    //
    // public static synchronized Document changeStringToXmlTree(InputStream is)
    // {
    // if (parser == null){
    // try{
    // parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    // }catch(ParserConfigurationException e){
    // e.printStackTrace();
    // return null;
    // }
    // }
    // try {
    // org.w3c.dom.Document doc = parser.parse(is);
    // return doc;
    // } catch (SAXException e) {
    // e.printStackTrace();
    // } catch (java.io.IOException e) {
    // e.printStackTrace();
    // }
    // return null;
    // }
    // public static Document changeStringToXmlTree(byte[] buffer) {
    // return changeStringToXmlTree(new java.io.ByteArrayInputStream(buffer));
    // }
    // public static Document changeStringToXmlTree(String str) {
    // return changeStringToXmlTree(new
    // java.io.ByteArrayInputStream(str.getBytes()));
    // }

    public static Document parseXMLFile(String fileName) {
        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            doc = docBuilder.parse(new File(fileName));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public static Document parseXMLStream(InputStream stream) {
        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            doc = docBuilder.parse(stream);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return doc;
    }
}
