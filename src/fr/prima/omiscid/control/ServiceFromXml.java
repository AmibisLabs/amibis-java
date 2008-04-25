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

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import fr.prima.omiscid.com.TcpClientServer;
import fr.prima.omiscid.control.interf.VariableChangeListener;
import fr.prima.omiscid.control.interf.VariableChangeQueryListener;
import fr.prima.omiscid.control.message.servicexml.Inoutput;
import fr.prima.omiscid.control.message.servicexml.Input;
import fr.prima.omiscid.control.message.servicexml.Output;
import fr.prima.omiscid.control.message.servicexml.Service;
import fr.prima.omiscid.control.message.servicexml.ServiceItem;
import fr.prima.omiscid.control.message.servicexml.Variable;
import fr.prima.omiscid.user.connector.ConnectorType;

public class ServiceFromXml extends ControlServer {

    /*
     * ServiceFromXml(String path_ontology, String basefile_ontology, String
     * file_name){ super(); GenerateXMLDescr gxd = new
     * GenerateXMLDescr(path_ontology, basefile_ontology); Document doc =
     * gxd.loadBaseDocument(file_name);
     * gxd.transformDocumentToServiceDescr(doc);
     * setServiceName(XmlToService.getServiceName(doc.getDocumentElement()));
     * XmlToService.initServiceFromXml(this, doc.getDocumentElement()); }
     */

    public ServiceFromXml(String fileName) throws MarshalException, ValidationException, IOException {
        super();
        init(Service.unmarshal(new FileReader(fileName)));

//        Document doc = XmlUtils.parseXMLFile(fileName);
//        setServiceName(XmlToService.getServiceName(doc.getDocumentElement()));
//        XmlToService.initServiceFromXml(this, doc.getDocumentElement());
    }

    public ServiceFromXml(InputStream inputStream) throws MarshalException, ValidationException, IOException {
        super();
        init(Service.unmarshal(new InputStreamReader(inputStream)));
//        Document doc = XmlUtils.parseXMLStream(stream);
//        setServiceName(XmlToService.getServiceName(doc.getDocumentElement()));
//        XmlToService.initServiceFromXml(this, doc.getDocumentElement());
    }

    private void init(Service service) throws IOException {
        this.setServiceName(service.getName());
//        service.getClazz();
//        service.getDocURL();
        service.getServiceItem();
        for (ServiceItem item : service.getServiceItem()) {
            if (item.getChoiceValue() instanceof Variable) {
                VariableAttribute variableAttribute = findVariable(item.getVariable().getName());
                if (variableAttribute == null) {
                    variableAttribute = this.addVariable(item.getVariable().getName());
                }
                variableAttribute.init(item.getVariable());
            } else if (item.getChoiceValue() instanceof Input) {
                Input inoutput = item.getInput();
                TcpClientServer tcpClientServer = new TcpClientServer(this.getPeerId());
                this.addInOutput(inoutput.getName(), tcpClientServer, ConnectorType.INPUT).init(inoutput);
                tcpClientServer.start();
            } else if (item.getChoiceValue() instanceof Output) {
                Output inoutput = item.getOutput();
                TcpClientServer tcpClientServer = new TcpClientServer(this.getPeerId());
                this.addInOutput(inoutput.getName(), tcpClientServer, ConnectorType.OUTPUT).init(inoutput);
                tcpClientServer.start();
            } else if (item.getChoiceValue() instanceof Inoutput) {
                Inoutput inoutput = item.getInoutput();
                TcpClientServer tcpClientServer = new TcpClientServer(this.getPeerId());
                this.addInOutput(inoutput.getName(), tcpClientServer, ConnectorType.INOUTPUT).init(inoutput);
                tcpClientServer.start();
            } else {
                System.err.println("unhandled service item in ServiceFromXml.init");
            }
        }
    }

//    public ServiceFromXml(Document doc) {
//        super(XmlToService.getServiceName(doc.getDocumentElement()));
//        XmlToService.initServiceFromXml(this, doc.getDocumentElement());
//    }

    public static void main(String arg[]) throws MarshalException, ValidationException, IOException {
        String path = "src/OmiscidSearch/xml/";
        String fileName = path + "generatedfile.xml";
        fileName = "service.xml";
        ServiceFromXml sfx = new ServiceFromXml(fileName);
        sfx.addVariableChangeQueryListener(new VariableChangeQueryListener() {

            public boolean isAccepted(VariableAttribute currentVariable, String newValue) {
                System.out.println(currentVariable.getName() +" : "+ newValue);
                return true;
            }

        });
        sfx.findVariable("writable").addListenerChange(new VariableChangeListener() {

            public void variableChanged(VariableAttribute var) {
                System.out.println("changed "+var.getValueStr());
            }

        });

        sfx.startServer(0);
        sfx.startProcessMessagesThread();
    }
}
