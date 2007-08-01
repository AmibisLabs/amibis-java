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

public class I0033_TestRemoveAllConnectorListeners {
    
    /*
     * This tests the new removeAllConnectorListeners feature.
     * It tests addition and removal of connector listeners.
     */
    public static void main(String[] args) throws IOException {
        final Vector<String> events = new Vector<String>();
        ServiceFactory factory = FactoryFactory.factory();
        final Service server = factory.create("I0033Server");
        {
            server.addConnector("bug", "", ConnectorType.INPUT);
            for (int i = 0; i < 5; i++)
            server.addConnectorListener("bug", new ConnectorListener() {
                public void messageReceived(Service service,
                                            String localConnectorName,
                                            Message message) {
                    events.add("SV REC");
                    System.out.println(Arrays.toString(events.toArray()));
                    FactoryFactory.failed("Listener called");
                    System.exit(1);
                }

                public void disconnected(Service service, String localConnectorName, int peerId) {
                    events.add("SV DIS");
                    System.out.println(Arrays.toString(events.toArray()));
                    FactoryFactory.failed("Listener called");
                    System.exit(1);
                }

                public void connected(final Service service, final String localConnectorName, final int peerId) {
                    events.add("SV CON");
                    System.out.println(Arrays.toString(events.toArray()));
                    FactoryFactory.failed("Listener called");
                    System.exit(1);
                }
            });
            server.start();
        }
        server.removeAllConnectorListeners("bug");
        {
            final Service client = factory.create("I0033Client");
            client.addConnector("bug", "", ConnectorType.OUTPUT);
            //client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0033Server"));
            client.connectTo("bug", proxy, "bug");
            client.sendToAllClients("bug", new byte[0]);
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}
        System.out.println(Arrays.toString(events.toArray()));
        FactoryFactory.passed("All messages sent, none received");
        System.exit(0);
    }

}
