/*
 * Created on Apr 28, 2006
 *
 */
package fr.prima.omiscid.com;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Utility class containing BIP related constants and some Com Layer tools.
 */
public final class BipUtils {

    /** characters found at the beginning of the message header */
    public static final byte[] messageBegin = { 'B', 'I', 'P', '/', '1', '.', '0', ' ' };

    /** string found at the end of the header */
    public static final byte[] headerEnd = { '\r', '\n' };

    /** string found at the end of the message */
    public static final byte[] messageEnd = { '\r', '\n' };

    public static final String encoding = "utf-8";

    /**
     * Create a string with an hexadecimal representation of an integer. The
     * representation has 8 characters completed by 0.
     * 
     * @param i
     *            the value to change into string
     */
    public static String intTo8HexString(int i) {
        String str = Integer.toHexString(i);
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

    private static Random randomForThisJVM = new Random(System.currentTimeMillis());

    /**
     * Generates an id for a BIP peer based on a random number and the current
     * time. Warning!!! If two jvms init their variables at the same
     * currentTimeMillis and call generateServiceId at the same
     * currentTimeMillis there *will* be a problem. Note that this is virtually
     * not guaranteed that this id is unique.
     * 
     * @return a new id for a BIP peer
     */
    public static int generateBIPPeerId() {
        // System.out.println(Thread.currentThread().getId() + " , "+
        // Thread.currentThread().getName() + " , "+
        // Thread.currentThread().getThreadGroup().getName() + ": " +
        // randomForThisJVM);
        int partTime = (int) (System.currentTimeMillis() & 0x0000FFFF);
        double r = randomForThisJVM.nextDouble();
        int partRandom = ((int) (r * 0xEFFFFFFF) & 0xFFFF0000);
        return partTime + partRandom;
        // return (int) (System.currentTimeMillis() & 0xFFFFFFFF);
    }

    /**
     * The shared parser used to change messages into DOM trees.
     */
    private static DocumentBuilder parser = null;

    private static synchronized Document streamToDomDocument(InputStream is) throws SAXException, IOException {
        if (parser == null) {
            try {
                parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                return null;
            }
        }
        Document doc = parser.parse(is);
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

    /**
     * Builds a String from the given byte array using BIP encoding.
     * 
     * @param buffer
     *            the data buffer to decode as a string
     * @return the decoded String or null if the BIP encoding couldn't be found
     */
    public static String byteArrayToString(byte[] buffer) {
        try {
            return new String(buffer, encoding);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Builds a byte array from the given string using BIP encoding.
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

}
