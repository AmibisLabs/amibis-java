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

package fr.prima.omiscid.test;


import fr.prima.omiscid.user.connector.ConnectorListener;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.exception.MessageInterpretationException;
import java.io.IOException;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.util.Utility;

public class I0025_SimpleXMLMessage {
    
    /*
     * This test tries to interprete a message as an xml one.
     * This has been added because Remi B. is having problems with xml message receptions.
     * This seems to work well.
     * The problem seems to have occured under new omiscidgui.
     */
    public static void main(String[] args) throws IOException {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0025Server");
            server.addConnector("bug", "", ConnectorType.INPUT);
            server.addConnectorListener("bug", new ConnectorListener() {
                public void messageReceived(Service service,
                                            String localConnectorName,
                                            Message message) {
                    try {
                        FactoryFactory.passed("XML message properly interpreted: "+message.getBufferAsXML().getTagName());
                        FactoryFactory.passed("XML message properly interpreted: "+message.getBufferAsXMLUnchecked().getTagName());
                        System.exit(0);
                    } catch (MessageInterpretationException e) {
                        throw new RuntimeException(e);
                    }
                }

                public void disconnected(Service service,
                                         String localConnectorName, int peerId) {
                }

                public void connected(Service service, String localConnectorName, int peerId) {
                }
            });
            server.start();
        }
        {
            Service client = factory.create("I0025Client");
            client.addConnector("bug", "", ConnectorType.OUTPUT);
            client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0025Server"));
            client.connectTo("bug", proxy, "bug");
            client.sendToAllClients("bug", Utility.stringToByteArray("<plop/>"));
            client.stop();
        }
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {}
        FactoryFactory.failed("Timeout logically due to a problem in XML message interpretation");
        System.exit(1);
    }

}
