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

public class I0013_SendReplyToMessage {
    
    private static final int messagesToSend = 100;
    
    public static void main(String[] args) throws IOException, InterruptedException {
        final ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0013Server");
            server.addConnector("c", "messages", ConnectorType.INOUTPUT);
            server.addConnectorListener("c", new ConnectorListener() {
                int count = 0;
            
                public void messageReceived(final Service service, final String localConnectorName, final Message message) {
                    switch (count%4) {
                        case 0:
                            service.sendReplyToMessage(localConnectorName, new byte[1], message, false);
                            break;
                        case 1:
                            service.sendReplyToMessage(localConnectorName, new byte[1], message);
                            break;
                        case 2:
                            service.sendReplyToMessage(new byte[1], message, false);
                            break;
                        case 3:
                            service.sendReplyToMessage(new byte[1], message);
                            break;
                    }
                    count++;
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
            Service clientReceiving = factory.create("I0013Client");
            clientReceiving.addConnector("c", "this is c", ConnectorType.INOUTPUT);
            clientReceiving.addConnectorListener("c", new ConnectorListener() {
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
            clientReceiving.start();
            {
                Service clientQuiet = factory.create("I0013Client");
                clientQuiet.addConnector("c", "this is c", ConnectorType.INOUTPUT);
                clientQuiet.addConnectorListener("c", new ConnectorListener() {
                    public void messageReceived(final Service service, String localConnectorName, Message message) {
                        FactoryFactory.failed("Quiet client received something");
                        System.exit(1);
                    }
                    public void disconnected(Service service, String localConnectorName, int peerId) {
                    }
                    public void connected(Service service, String localConnectorName, int peerId) {
                    }
                });
                clientQuiet.start();
                final ServiceProxy quietProxy = clientQuiet.findService(ServiceFilters.nameIs("I0013Server"));
                clientQuiet.connectTo("c", quietProxy, "c");
            }
            final ServiceProxy proxy = clientReceiving.findService(ServiceFilters.nameIs("I0013Server"));
            clientReceiving.connectTo("c", proxy, "c");
            clientReceiving.sendToAllClients("c", new byte[1]);
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
