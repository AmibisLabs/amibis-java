package fr.prima.bipcontrol ;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlUtils {
    static public org.w3c.dom.Element firstChild(org.w3c.dom.Element elt, String name){
        NodeList nodeList = elt.getChildNodes();
        for(int i=0; i<nodeList.getLength(); i++){
            Node current = nodeList.item(i);
            if(current.getNodeType() == Node.ELEMENT_NODE && current.getNodeName().equals(name)){
               return (org.w3c.dom.Element)current; 
            }
        }
        return null;
    }
    
    static public String xmlDocToString(Document doc){        
        return elementToString(doc.getDocumentElement());
    }
    static public String elementToString(Element elt){        
        return elementToString(elt, "");
    }
    
    /**
     * Add CData tag around a string
     * 
     * @param content
     *            the string to "protect"
     * @return "&lt;![CDATA[" + content + "]]&gt;"
     */
    public static String generateCDataSection(String content) {
        return "<![CDATA[" + content + "]]>";
    }
    
    static public String elementToString(Element elt, String prefix){        
        String str = prefix+"<" + elt.getNodeName();
        
        NamedNodeMap nnm = elt.getAttributes();
        for(int i=0; i<nnm.getLength(); i++){
            str += " " + nnm.item(i).getNodeName() + 
            "=\"" + nnm.item(i).getNodeValue() +"\"";
        }
        
        boolean hasSubElt = false;
        NodeList nodeList = elt.getChildNodes();                
        String str2 = "";      
        for(int i=0; i<nodeList.getLength(); i++){
            Node cur = nodeList.item(i);
            if(cur.getNodeType() == Node.ELEMENT_NODE){
                str2 += elementToString((Element)cur, prefix+"  ");
                hasSubElt = true;
            }else if(cur.getNodeType() == Node.CDATA_SECTION_NODE){
                str2 += prefix+"  " + generateCDataSection(cur.getTextContent());
                hasSubElt = true;                
            }
        }        
        
        if(hasSubElt){
            str += ">\r\n" + str2 + prefix +"</" + elt.getNodeName() + ">\r\n";
        }else{
            str2 = elt.getTextContent();
            if(str2.equals("")){
                str += "/>\r\n";
            }else{
                str += ">" + str2 + "</" + elt.getNodeName() + ">\r\n";
            }
        }
        return str;

    }
    
    
    /** parser used to change message into XML tree */
    static public DocumentBuilder parser = null;
    
    static public Document changeStringToXmlTree(InputStream is) {
        if(parser == null){
            try{
                parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            }catch(ParserConfigurationException e){
                e.printStackTrace();
                return null;
            }
        }
        try {
            org.w3c.dom.Document doc;
            synchronized (parser) {
                doc = parser.parse(is);
            }
            return doc;
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    static public Document changeStringToXmlTree(byte[] buffer) {
        return changeStringToXmlTree(new java.io.ByteArrayInputStream(buffer));
    }
    static public Document changeStringToXmlTree(String str) {
        return changeStringToXmlTree(new java.io.ByteArrayInputStream(str.getBytes()));
    }
    
    static public Document parseXMLFile(String fileName){
        Document doc = null;
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            doc = docBuilder.parse(new File(fileName));
        }catch(ParserConfigurationException e){
            e.printStackTrace();
        }catch(SAXException e){
            e.printStackTrace();
        }catch(java.io.IOException e){
            e.printStackTrace();
        }
        return doc;
    }
    
    static public Document parseXMLStream(InputStream stream){
        Document doc = null;
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            doc = docBuilder.parse(stream);
        }catch(ParserConfigurationException e){
            e.printStackTrace();
        }catch(SAXException e){
            e.printStackTrace();
        }catch(java.io.IOException e){
            e.printStackTrace();
        }
        return doc;
    }   
}
