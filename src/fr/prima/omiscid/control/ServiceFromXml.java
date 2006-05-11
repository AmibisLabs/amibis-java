package fr.prima.omiscid.control;

import java.io.InputStream;

import org.w3c.dom.Document;

public class ServiceFromXml extends fr.prima.omiscid.control.ControlServer {

    /*
     * ServiceFromXml(String path_ontology, String basefile_ontology, String
     * file_name){ super(); GenerateXMLDescr gxd = new
     * GenerateXMLDescr(path_ontology, basefile_ontology); Document doc =
     * gxd.loadBaseDocument(file_name);
     * gxd.transformDocumentToServiceDescr(doc);
     * setServiceName(XmlToService.getServiceName(doc.getDocumentElement()));
     * XmlToService.initServiceFromXml(this, doc.getDocumentElement()); }
     */

    public ServiceFromXml(String fileName) {
        super();
        Document doc = XmlUtils.parseXMLFile(fileName);
        setServiceName(XmlToService.getServiceName(doc.getDocumentElement()));
        XmlToService.initServiceFromXml(this, doc.getDocumentElement());
    }

    public ServiceFromXml(InputStream stream) {
        super();
        Document doc = XmlUtils.parseXMLStream(stream);
        setServiceName(XmlToService.getServiceName(doc.getDocumentElement()));
        XmlToService.initServiceFromXml(this, doc.getDocumentElement());
    }

    public ServiceFromXml(Document doc) {
        super(XmlToService.getServiceName(doc.getDocumentElement()));
        XmlToService.initServiceFromXml(this, doc.getDocumentElement());
    }

    public static void main(String arg[]) {
        String path = "src/OmiscidSearch/xml/";
        String fileName = path + "generatedfile.xml";
        ServiceFromXml sfx = new ServiceFromXml(fileName) {
            protected void variableModificationQuery(byte[] buffer, int status, VariableAttribute va) {
                va.setValueStr(new String(buffer));
            }
        };

        sfx.startServer(0);
        sfx.startProcessMessagesThread();
    }
}
