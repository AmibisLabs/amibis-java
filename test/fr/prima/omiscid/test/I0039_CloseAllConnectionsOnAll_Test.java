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



import org.junit.Test;
import static org.junit.Assert.*;

public class I0039_CloseAllConnectionsOnAll_Test {
    /*
     * This tests the new disconnection feature for all connectors.
     * It tests effective-disconnection (remote listener does not receive messages)
     * for a all remotely-closed locally-initiated connections.
     */
    @Test
    public void doIt() throws IOException {
        final Vector<String> events = new Vector<String>();
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0039Server");
            ConnectorListener l = new ConnectorListener() {
                boolean passed = false;
                public void messageReceived(Service service,
                                            String localConnectorName,
                                            Message message) {
                    events.add("SV REC "+passed);
                    if (passed) {
                        System.out.println(Arrays.toString(events.toArray()));
                        FactoryFactory.failed("Second message received while connection should have been closed");
                    }
                    passed = true;
                    service.closeAllConnections();
                }

                public void disconnected(Service service, String localConnectorName, int peerId) {
                    events.add("SV DIS "+passed);
                }

                public void connected(final Service service, final String localConnectorName, final int peerId) {
                    events.add("SV CON "+passed);
                }
            };
            server.addConnector("bug1", "", ConnectorType.INPUT);
            server.addConnectorListener("bug1", l);
            server.addConnector("bug2", "", ConnectorType.INOUTPUT);
            server.addConnectorListener("bug2", l);
            server.start();
        }
        Vector<Service> clients = new Vector<Service>();
        for (int i = 0; i < 4; i++) {
            final Service client = factory.create("I0039Client");
            clients.add(client);
            client.addConnector("ug1", "", ConnectorType.OUTPUT);
            client.addConnector("ug2", "", ConnectorType.INOUTPUT);
            //client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0039Server"));
            client.connectTo("ug1", proxy, "bug1");
            client.connectTo("ug2", proxy, "bug2");
        }
        clients.firstElement().sendToAllClients("ug1", new byte[0]);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}
        for (Service client : clients) {
            client.sendToAllClients("ug1", new byte[0]);
            client.sendToAllClients("ug2", new byte[0]);
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {}
        System.out.println(Arrays.toString(events.toArray()));
        FactoryFactory.passed("All messages sent, none received");
    }

}
