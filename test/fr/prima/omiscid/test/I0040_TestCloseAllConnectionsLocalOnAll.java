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
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

public class I0040_TestCloseAllConnectionsLocalOnAll {
    /*
     * This tests the new disconnection feature on all connectors.
     * It tests effective-disconnection (remote listener does not receive messages)
     * for a all locally-closed locally-initiated connections.
     */
    public static void main(String[] args) throws IOException {
        final Vector<String> events = new Vector<String>();
        ServiceFactory factory = FactoryFactory.factory();
        Vector<Service> servers = new Vector<Service>();
        for (int i = 0; i < 4; i++) {
            final Service server = factory.create("I0040Server-"+i);
            servers.add(server);
            server.addConnector("bug1", "", ConnectorType.INPUT);
            server.addConnector("bug2", "", ConnectorType.INOUTPUT);
            ConnectorListener l = new ConnectorListener() {
                boolean passed = false;
                public void messageReceived(Service service,
                                            String localConnectorName,
                                            Message message) {
                    events.add("SV REC "+passed);
                    System.out.println(Arrays.toString(events.toArray()));
                    FactoryFactory.failed("Second message received while connection should have been closed");
                    System.exit(1);
                }

                public void disconnected(Service service, String localConnectorName, int peerId) {
                    events.add("SV DIS "+passed);
                }

                public void connected(final Service service, final String localConnectorName, final int peerId) {
                    events.add("SV CON "+passed);
                }
            };
            server.addConnectorListener("bug1", l);
            server.addConnectorListener("bug2", l);
            server.start();
        }
        {
            final Service client = factory.create("I0040Client");
            client.addConnector("ug1", "", ConnectorType.OUTPUT);
            client.addConnector("ug2", "", ConnectorType.INOUTPUT);
            //client.start();
            for (int i = 0; i < 4; i++) {
                final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0040Server-"+i));
                client.connectTo("ug1", proxy, "bug1");
                client.connectTo("ug2", proxy, "bug2");
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
            client.closeAllConnections();
            client.sendToAllClients("ug1", new byte[0]);
            client.sendToAllClients("ug2", new byte[0]);
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {}
        System.out.println(Arrays.toString(events.toArray()));
        FactoryFactory.passed("All messages sent, none received");
        System.exit(0);
    }

}
