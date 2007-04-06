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

import java.io.IOException;
import java.util.Vector;

import fr.prima.omiscid.user.connector.ConnectorListener;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;

public class I0003_ListenersNotPropagatedOnConnectTo {
    
    private static final int messagesToSend = 100;
    
    public static void main(String[] args) throws IOException, InterruptedException {
        final ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("BugI0003Server");
            server.addConnector("c", "messages", ConnectorType.INOUTPUT);
            server.addConnectorListener("c", new ConnectorListener() {
            
                public void messageReceived(final Service service, final String localConnectorName, final Message message) {
                    service.sendToOneClient(localConnectorName, new byte[1], message.getPeerId());
                }
            
                public void disconnected(Service service, String localConnectorName, int peerId) {
                    // TODO Auto-generated method stub
            
                }
            
                public void connected(Service service, String localConnectorName, int peerId) {
                    // TODO Auto-generated method stub
            
                }
            
            });
            server.start();
        }
        final Vector<Object> received = new Vector<Object>();
        {
            Service client = factory.create("BugI0003Client");
            client.addConnector("c", "this is c", ConnectorType.INOUTPUT);
            client.addConnectorListener("c", new ConnectorListener() {
                int count = 0;
            
                public void messageReceived(final Service service, String localConnectorName, Message message) {
                    service.sendToAllClients("c", new byte[1]);
                    count++;
                    received.add(count);
                    if (count >= messagesToSend) {
                        service.stop();
                    }
                }
            
                public void disconnected(Service service, String localConnectorName, int peerId) {
                    // TODO Auto-generated method stub
            
                }
            
                public void connected(Service service, String localConnectorName, int peerId) {
                    // TODO Auto-generated method stub
            
                }
            
            });
            client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("BugI0003Server"));
            client.connectTo("c", proxy, "c");
            client.sendToAllClients("c", new byte[1]);
        }
        Thread.sleep(2000);
        if (received.size() == messagesToSend) {
            FactoryFactory.passed(received.size()+" received ok");
        } else {
            FactoryFactory.failed(received.size()+" ended, "+messagesToSend+" expected");
        }
        System.exit(0);
    }

}
