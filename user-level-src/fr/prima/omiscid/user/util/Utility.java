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

package fr.prima.omiscid.user.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public final class Utility {

    private static final String encoding = "utf-8";

    public static int rootPeerIdFromConnectorPeerId(int peerId) {
        return peerId & 0xFFFFFF00;
    }
    
    public static int connectorPeerIdFromIndex(int rootPeerId, int connectorIndex) {
        if (rootPeerId != rootPeerIdFromConnectorPeerId(rootPeerId)) {
            throw new RuntimeException("TODO");
        }
        if (connectorIndex != (connectorIndex&0xFF)) {
            throw new RuntimeException("TODO 2");
        }
        return rootPeerId | connectorIndex;
    }


    /**
     * Create a string with an hexadecimal representation of an integer. The
     * representation has 8 characters completed by 0.
     *
     * @param i
     *            the value to change into string
     */
    public static String intTo8HexString(int i) {
        String str = Integer.toHexString(i).toUpperCase();
        while (str.length() < 8) {
            str = "0" + str;
        }
        // System.out.println("IntTo8HexString: "+str);
        return str;
    }

    /**
     * Change a string with an hexadecimal representation of an integer into the
     * integer value
     *
     * @param str
     *            the string to transform
     * @return the integer value associated to the string
     */
    public static int hexStringToInt(String str) {
        if (str == null) {
            return -1;
        }
        str = str.toLowerCase();
        int nb = str.length();
        byte cstr[];
        try {
            cstr = str.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            cstr = str.getBytes();
            e.printStackTrace();
        }
        int i = 0;
        int res = 0;

        while (i < nb) {
            res = res * 16;
            // System.out.println(cstr[i]);
            if (cstr[i] <= 57)
                res += (cstr[i] - 48);
            else
                res += (cstr[i] - 97) + 10;
            i++;
        }
        // System.out.println("str:["+str+"] ("+cstr[0]+"):"+res);
        return res;
    }

    /**
     * Builds a byte array from the given string using the default OMiSCID encoding for string messages.
     *
     * BIP encoding should be available on any platform and should allow
     * the encoding of any String.
     *
     * @param string
     *            the string data to encode as a byte array
     * @return the encoded byte array or null if the BIP encoding couldn't be
     *         found (or if string is null)
     */
    public static byte[] stringToByteArray(String string) {
        if (string == null) {
            return null;
        } else {
            try {
                return string.getBytes(encoding);
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Builds a String from the given byte array using OMiSCID encoding for string messages.
     *
     * @param buffer
     *            the data buffer to decode as a string
     * @return the decoded String or null if the BIP encoding couldn't be found or
     * if the given byte array could not be decoded.
     */
    public static String byteArrayToString(byte[] buffer) {
        try {
            return new String(buffer, encoding);
        } catch (Exception e) {
            return null;
        }
    }

    public static final class Xml {
        /**
         * The shared parser and writer used to change messages into DOM trees and vice-versa.
         */
        private static DocumentBuilder documentBuilder = null;
        private static Transformer transformer = null;

        private static DocumentBuilder getDocumentBuilder() {
            if (documentBuilder == null) {
                try {
                    documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }
            return documentBuilder;
        }

        private static Transformer getTransformer() {
            if (transformer == null) {
                try {
                    transformer = TransformerFactory.newInstance().newTransformer();
                } catch (TransformerConfigurationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (TransformerFactoryConfigurationError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return transformer;
        }

        private static synchronized Document streamToDomDocument(InputStream is) throws SAXException, IOException {
            Document doc = getDocumentBuilder().parse(is);
            return doc;
        }

        /**
         * Builds a XML DOM document using the given byte array and returns its root
         * element.
         *
         * @param buffer
         * @return the root element of the built document
         * @throws SAXException
         */
        public static Element byteArrayToDomElement(byte[] buffer) throws SAXException {
            try {
                return streamToDomDocument(new ByteArrayInputStream(buffer)).getDocumentElement();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * Builds a XML DOM document using the given byte array and returns it.
         *
         * @param buffer
         * @return the document built
         * @throws SAXException
         */
        public static Document byteArrayToDomDocument(byte[] buffer) throws SAXException {
            try {
                return streamToDomDocument(new ByteArrayInputStream(buffer));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public synchronized static byte[] elementToByteArray(Element element) {
            DOMSource source = new DOMSource(element);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            StreamResult streamResult = new StreamResult(byteArrayOutputStream);
            try {
                getTransformer().transform(source, streamResult);
            } catch (TransformerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            byte[] res = byteArrayOutputStream.toByteArray();
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return res;
        }

        public synchronized static Document createDocument() {
            return getDocumentBuilder().newDocument();
        }

        public static Document createDocument(String rootTag, String...attributeNameValuePairs) {
            return createDocumentWithTextContent(rootTag, null, attributeNameValuePairs);
        }

        public static Document createDocumentWithTextContent(String rootTag, String content, String...attributeNameValuePairs) {
            Document document = createDocument();
            Element root = document.createElement(rootTag);
            for (int i = 0; i < attributeNameValuePairs.length; i+=2) {
                root.setAttribute(attributeNameValuePairs[i], attributeNameValuePairs[i+1]);
            }
            if (content != null) {
                root.appendChild(document.createTextNode(content));
            }
            document.appendChild(root);
            return document;
        }
    }

}
