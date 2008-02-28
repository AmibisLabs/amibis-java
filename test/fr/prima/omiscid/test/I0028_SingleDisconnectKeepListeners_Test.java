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


import org.junit.Test;
import static org.junit.Assert.*;

public class I0028_SingleDisconnectKeepListeners_Test {
    
    /*
     * This tests the new disconnection feature.
     * It tests remote-reconnection (remote listener receive (connection) notifications after reconnection)
     * for a single remotely-closed locally-initiated connections.
     */
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws IOException, InterruptedException {
        ServiceFactory factory = FactoryFactory.factory();
        final Vector<String> events = new Vector<String>();
        {
            final Service server = factory.create("I0028Server");
            server.addConnector("bug", "", ConnectorType.INPUT);
            server.addConnectorListener("bug", new ConnectorListener() {
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
                    service.closeConnection(localConnectorName, message.getPeerId());
                }

                public void disconnected(Service service, String localConnectorName, int peerId) {
                    events.add("SV DIS "+passed);
                }

                public void connected(final Service service, final String localConnectorName, final int peerId) {
                    events.add("SV CON "+passed);
                    if (passed) {
                        System.out.println(Arrays.toString(events.toArray()));
                        FactoryFactory.passed("Second connection occured");
                    }
                }
            });
            server.start();
        }
        {
            final Service client = factory.create("I0028Client");
            client.addConnector("bug", "", ConnectorType.OUTPUT);
            //client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0028Server"));
            client.connectTo("bug", proxy, "bug");
            FactoryFactory.waitResult(200);
            client.sendToAllClients("bug", Utility.stringToByteArray("<plop/>"));
            FactoryFactory.waitResult(200);
            client.connectTo("bug", proxy, "bug");
            FactoryFactory.waitResult(200);
            System.out.println(Arrays.toString(events.toArray()));
            FactoryFactory.failed("Timeout logically due to problematic reconnection");
        }
    }

}
