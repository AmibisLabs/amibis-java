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
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.util.Utility;
import java.io.IOException;
import java.util.Vector;

public class I0045_RemoveConnectorListenerOnNewMessage {
    
    // At the time of writting, removing a listener from a messageReceived call
    // causes a concurrent modification (when there is more than one listener)
    public static void main(String[] args) throws IOException, InterruptedException {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0045Server");
            server.addConnector("rec", "", ConnectorType.INOUTPUT);
            server.addConnectorListener("rec", new ConnectorListener() {
                public void messageReceived(Service service,
                                            String localConnectorName,
                                            Message message) {
                    System.out.println("sv received: "+message.getBufferAsStringUnchecked());
                    service.sendReplyToMessage(message.getBuffer(), message);
                }

                public void disconnected(Service service,
                                         String localConnectorName, int peerId) {
                }

                public void connected(Service service, String localConnectorName,
                                      int peerId) {
                }
            });
            server.start();
        }
        {
            final Vector<String> received = new Vector<String>();
            class DisconnectOnReceive implements ConnectorListener {

                String expect;

                public DisconnectOnReceive(String expect) {
                    this.expect = expect;
                }
                
                public void messageReceived(Service service, String localConnectorName, Message message) {
                    System.out.println("cl expected: "+expect+" and received: "+message.getBufferAsStringUnchecked());
                    if (expect.equals(message.getBufferAsStringUnchecked())) {
                        service.removeConnectorListener(localConnectorName, this);
                        received.add(expect);
                    }
                }

                public void disconnected(Service service, String localConnectorName, int peerId) {
                }

                public void connected(Service service, String localConnectorName, int peerId) {
                }
            }
            Service client = factory.create("I0045Client");
            client.addConnector("bug", "", ConnectorType.INOUTPUT);
            client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0045Server"));
            client.connectTo("bug", proxy, "rec");
            client.addConnectorListener("bug", new DisconnectOnReceive("hiiiii"));
            client.addConnectorListener("bug", new DisconnectOnReceive("bla"));
            client.sendToOneClient("bug", Utility.stringToByteArray("hiiiii"), proxy);
            client.sendToOneClient("bug", Utility.stringToByteArray("bla"), proxy);
            Thread.sleep(1000);
            if (received.size() != 2) {
                FactoryFactory.failed("Wrong received count: "+received.size());
                System.exit(1);
            } else {
                FactoryFactory.passed("Properly received answers, count is: "+received.size());
                System.exit(0);
            }
        }
    }
}
