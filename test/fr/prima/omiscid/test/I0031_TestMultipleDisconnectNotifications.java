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
import java.io.IOException;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.util.Utility;
import java.util.Arrays;
import java.util.Vector;

public class I0031_TestMultipleDisconnectNotifications {
    
    /*
     * This tests the new disconnection feature.
     * It tests disconnection notification
     * for repeated in-connect remotely-closed locally-initiated connections.
     */
    public static void main(String[] args) throws IOException {
        final Vector<String> events = new Vector<String>();
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0031Server");
            server.addConnector("bug", "", ConnectorType.INPUT);
            server.addConnectorListener("bug", new ConnectorListener() {
                public void messageReceived(Service service,
                                            String localConnectorName,
                                            Message message) {
                    events.add("SV REC"+Utility.intTo8HexString(message.getPeerId()));
                }

                public void disconnected(Service service,
                                         String localConnectorName, int peerId) {
                    events.add("SV DIS "+Utility.intTo8HexString(peerId));
                }

                public void connected(final Service service, final String localConnectorName, final int peerId) {
                    events.add("SV CON "+Utility.intTo8HexString(peerId));
                    // sleep to let the client receive a connection notification
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {}
                    service.closeConnection(localConnectorName, peerId);
                }
            });
            server.start();
        }
        {
            final Service client = factory.create("I0031Client");
            client.addConnector("bug", "", ConnectorType.OUTPUT);
            //client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0031Server"));
            client.addConnectorListener("bug", new ConnectorListener() {
                int tries = 0;
                public void messageReceived(Service service,
                                            String localConnectorName,
                                            Message message) {
                    events.add("CL REC "+Utility.intTo8HexString(message.getPeerId()));
                }
                public void disconnected(Service service, String localConnectorName, int peerId) {
                    events.add("CL DIS "+Utility.intTo8HexString(peerId));
                    tries++;
                    if (tries >= 5) {
                        System.out.println(Arrays.toString(events.toArray()));
                        FactoryFactory.passed("Limit number of tries reached, "+Arrays.toString(events.toArray()));
                        System.exit(0);
                    }
                    client.connectTo("bug", proxy, "bug");
                }
                public void connected(Service service, String localConnectorName, int peerId) {
                    events.add("CL CON "+Utility.intTo8HexString(peerId));
                }
            });
            client.connectTo("bug", proxy, "bug");
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() - time < 3000) {
                client.sendToAllClients("bug", Utility.stringToByteArray("<plop/>"));
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {}
            }
        }
        System.out.println(Arrays.toString(events.toArray()));
        FactoryFactory.failed("Timeout logically due to a problem in connection/disconnection handling: ");
        System.exit(1);
    }

}
