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
import fr.prima.omiscid.user.exception.ConnectionRefused;
import java.io.IOException;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.util.Utility;
import java.util.Arrays;
import java.util.Vector;


import org.junit.Test;
import static org.junit.Assert.*;

public class I0032_MultipleConnectionRefusalImmediateDisconnect_Test {
    
    /*
     * This tests the new disconnection feature.
     * It tests disconnection notification
     * for repeated in-connect remotely-closed locally-initiated connections.
     * The Exception that happened on fast disconnections has been removed
     * so this only test a simple disconnection feature.
     */
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws IOException {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0032Server");
            server.addConnector("bug", "", ConnectorType.INPUT);
            server.addConnectorListener("bug", new ConnectorListener() {
                public void messageReceived(Service service,
                                            String localConnectorName,
                                            Message message) {
                    events.add("SV REC "+Utility.intTo8HexString(message.getPeerId()));
                }

                public void disconnected(Service service,
                                         String localConnectorName, int peerId) {
                    events.add("SV DIS "+Utility.intTo8HexString(peerId));
                }

                public void connected(final Service service, final String localConnectorName, final int peerId) {
                    events.add("SV CON "+Utility.intTo8HexString(peerId));
                    // close connection as soon as possible
                    // this can result in ConnectionRefused for client
                    // or in a connection followed by a deconnection notification
                    if (Math.random() < .5) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {}
                    }
                    service.closeConnection(localConnectorName, peerId);
                }
            });
            server.start();
        }
        {
            final Service client = factory.create("I0032Client");
            client.addConnector("bug", "", ConnectorType.OUTPUT);
            //client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0032Server"));
            client.addConnectorListener("bug", new ConnectorListener() {
                public void messageReceived(Service service,
                                            String localConnectorName,
                                            Message message) {
                    events.add("CL REC "+Utility.intTo8HexString(message.getPeerId()));
                }
                public void disconnected(Service service, String localConnectorName, int peerId) {
                    events.add("CL DIS "+Utility.intTo8HexString(peerId));
                    tries++;
                    connectTo(client,"bug", proxy, "bug");
                }
                public void connected(Service service, String localConnectorName, int peerId) {
                    events.add("CL CON "+Utility.intTo8HexString(peerId));
                }

            });
            connectTo(client,"bug", proxy, "bug");
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() - time < 3000) {
                client.sendToAllClients("bug", Utility.stringToByteArray("<plop/>"));
                try {
                    FactoryFactory.waitResult(30);
                } catch (InterruptedException e) {}
            }
        }
        System.out.println(Arrays.toString(events.toArray()));
        FactoryFactory.failed("Timeout logically due to a problem in connection/disconnection handling: ");
    }
    
    static int tries = 0;
    static final Vector<String> events = new Vector<String>();
    private static void connectTo(Service client, String string, ServiceProxy proxy, String string0) {
        tries ++;
        if (tries >= 10) {
            //System.out.println(Arrays.toString(events.toArray()));
            FactoryFactory.passed("Limit number of tries reached, "+Arrays.toString(events.toArray()));
        }
        try {
            client.connectTo(string, proxy, string0);
            events.add("ACCEPTED");
        } catch(ConnectionRefused e) {
            events.add("REFUSED");
            connectTo(client, string, proxy, string0);
        }
    }

}
