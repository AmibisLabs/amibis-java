package fr.prima.omiscid.control ;

import fr.prima.omiscid.com.interf.Message;

/**
 * XML message stored as tree.
 * @author  Sebastien Pesnel  Refactoring by Patrick Reignier
 */
public class XmlMessage {

    /** id of the service (source of this message) */
    private int pid = 0;

    /** message id */
    private int mid = 0;

    /** xml tree build on a message */
    private org.w3c.dom.Document document = null;

    /**
     * Create a new instance of XmlMessage
     * 
     * @param ePid
     *            id of the source of message
     * @param eMid
     *            id of the message
     */
    public XmlMessage(int ePid, int eMid) {
        pid = ePid;
        mid = eMid;
    }

    /**
	 * Access to the id of the message source
	 * @return  id of the service
	 * @uml.property  name="pid"
	 */
    public int getPid() {
        return pid;
    }

    /**
	 * Access to the message id
	 * @return  the message id
	 * @uml.property  name="mid"
	 */
    public int getMid() {
        return mid;
    }

    /**
     * Access to the root of the XML tree
     * 
     * @return the first element of the XML tree, null if not XML tree
     */
    public org.w3c.dom.Element getRootNode() {
        if (document == null)
            return null;
        else
            return document.getDocumentElement();
    }


    /**
     * Change a OMiSCID message into XmlMessage
     * 
     * @param msg
     *            a OMiSCID message
     */
    static public XmlMessage changeMessageToXmlTree(Message msg) {        
        org.w3c.dom.Document doc = XmlUtils.changeStringToXmlTree(msg.getBuffer());
        if (doc != null) {
            XmlMessage xmlMsg = new XmlMessage(msg.getPeerId(), msg.getMsgId());
            xmlMsg.document = doc;
            return xmlMsg;
        }        
        return null;
    }
}
