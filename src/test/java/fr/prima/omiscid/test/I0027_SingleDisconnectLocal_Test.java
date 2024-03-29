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

public class I0027_SingleDisconnectLocal_Test {
    
    /*
     * This tests the new disconnection feature.
     * It tests remote-disconnection (remote listener does not receive messages)
     * for a single locally-closed locally-initiated connections.
     */
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws IOException {
        final Vector<String> events = new Vector<String>();
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0027Server");
            server.addConnector("bug", "", ConnectorType.INOUTPUT);
            server.addConnectorListener("bug", new ConnectorListener() {
                boolean passed = false;
                public void messageReceived(Service service, String localConnectorName, Message message) {
                    events.add("SV REC "+passed);
                    if (passed) {
                        FactoryFactory.failed("Second message received while connection should have been closed");
                    }
                    passed = true;
                    service.sendReplyToMessage(Utility.stringToByteArray("<plop/>"), message);
                }

                public void disconnected(Service service, String localConnectorName, int peerId) {
                    events.add("SV DIS "+passed);
                }

                public void connected(final Service service, final String localConnectorName, final int peerId) {
                    events.add("SV CON "+passed);
                }
            });
            server.start();
        }
        {
            final Service client = factory.create("I0027Client");
            client.addConnector("bug", "", ConnectorType.INOUTPUT);
            client.addConnectorListener("bug", new ConnectorListener(){

                boolean passed = false;
                public void messageReceived(Service service, String localConnectorName, Message message) {
                    events.add("CL REC "+passed);
                    if (passed) {
                        FactoryFactory.failed("Second message received while connection should have been closed");
                    }
                    passed = true;
                    service.closeConnection(localConnectorName, message.getPeerId());
                }

                public void connected(Service service, String localConnectorName, int peerId) {
                    events.add("CL CON "+passed);
                }

                public void disconnected(Service service, String localConnectorName, int peerId) {
                    events.add("CL DIS "+passed);
                }
                
            });
            //client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0027Server"));
            client.connectTo("bug", proxy, "bug");
            long time = System.currentTimeMillis();
            int msg = 0;
            while (System.currentTimeMillis() - time < 700) {
                System.out.println(Arrays.toString(events.toArray()));
                client.sendToAllClients("bug", Utility.stringToByteArray("<plop/>"));
                msg++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {}
            FactoryFactory.passed("All messages sent: "+msg);
        }
    }

}
