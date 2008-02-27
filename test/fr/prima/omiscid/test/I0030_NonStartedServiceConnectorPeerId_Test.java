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
import java.util.Arrays;
import java.util.Vector;


import org.junit.Test;
import static org.junit.Assert.*;

public class I0030_NonStartedServiceConnectorPeerId_Test {
    
    /*
     * This tests unstarted services clients.
     * PeerId where wrongly affected to connectors.
     */
    @Test
    public void doIt() throws IOException {
        final Vector<Integer> connections = new Vector<Integer>();
        ServiceFactory factory = FactoryFactory.factory();
        final int numberOfConnectors = 10;
        {
            final Service server = factory.create("I0030Server");
            server.addConnector("bug", "", ConnectorType.INPUT);
            server.addConnectorListener("bug", new ConnectorListener() {
                public void messageReceived(Service service,
                                            String localConnectorName,
                                            Message message) {
                }

                public void disconnected(Service service,
                                         String localConnectorName, int peerId) {
                }

                public void connected(Service service, String localConnectorName, int peerId) {
                    if (!connections.isEmpty() && peerId != 1+connections.lastElement()) {
                        connections.add(peerId);
                        FactoryFactory.failed("Not a regular sequence of peerIds found, "+Arrays.toString(connections.toArray()));
                    }
                    connections.add(peerId);
                    if (connections.size() >= numberOfConnectors) {
                        FactoryFactory.passed("All connections received, "+Arrays.toString(connections.toArray()));
                    }
                }
            });
            server.start();
        }
        {
            final Service client = factory.create("I0030Client");
            for (int i = 0; i < numberOfConnectors; i++) {
                client.addConnector("bug"+i, "", ConnectorType.OUTPUT);
            }
            //client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0030Server"));
            for (int i = 0; i < numberOfConnectors/2; i++) {
                client.connectTo("bug"+i, proxy, "bug");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
            }
            client.start(); // starting does not cause connector peerids to be changed
            for (int i = 0; i < numberOfConnectors/2; i++) {
                client.connectTo("bug"+(numberOfConnectors/2+i), proxy, "bug");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
            }
        }
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {}
        FactoryFactory.failed("Timeout logically due to a problem in connection/disconnections");
    }
}
