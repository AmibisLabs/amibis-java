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

import fr.prima.omiscid.dnssd.interf.DNSSDFactory;
import fr.prima.omiscid.user.connector.ConnectorListener;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.util.Utility;
import fr.prima.omiscid.user.variable.LocalVariableListener;
import fr.prima.omiscid.user.variable.VariableAccessType;
import java.io.IOException;
import java.util.Vector;
import org.junit.Test;

public class I0046_RemoveConnectorListenerOnDisconnect_Test {

    // At the time of writting (1.3.1), removing a listener from a disconnected call
    // causes a concurrent modification (when there is more than one listener).
    // There are 2 cases: TCPClient cases (initiated locally) and TCPServer ones.
    // There are also multiple cases depending on which side closes the connection.
    //  Local connect  + local disconnect  -> ok
    //  Local connect  + remote disconnect -> ok
    //  Remote connect + local disconnect -> ok
    //  Remote connect + remote disconnect -> ok
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws IOException, InterruptedException {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0046Server");
            server.addVariable("target", "string", "", VariableAccessType.READ_WRITE);
            server.addConnector("rec", "", ConnectorType.INOUTPUT);
            server.addLocalVariableListener("target", new LocalVariableListener() {

                public void variableChanged(final Service service, String variableName, String value) {
                }

                public boolean isValid(final Service service, String variableName, String newValue) {
                    // Should not do long processing here (so we do it in a separate thread)
                    // jivedns even fails to answer in time to pass the test if we do search here
                    new Thread(new Runnable(){
                        public void run() {
                            final ServiceProxy proxy = service.findService(ServiceFilters.nameIs("I0046Client"));
                            service.connectTo("rec", proxy, "bug");
                            System.out.println("Server received connection order");
                        }
                    }).start();
                    return false;
                }
            });
            server.addConnectorListener("rec", new ConnectorListener() {

                public void messageReceived(Service service, String localConnectorName, Message message) {
                    System.out.println("Server received disconnection order");
                    service.closeAllConnections();
                }

                public void disconnected(Service service, String localConnectorName, int peerId) {
                }

                public void connected(Service service, String localConnectorName, int peerId) {
                }
            });
            server.start();
            System.err.println(server.getPeerIdAsString());
            new Thread(new Runnable() { // pre-fetch client informations
                public void run() {
                    final ServiceProxy proxy = server.findService(ServiceFilters.nameIs("I0046Client"));
                    System.err.println("Client "+proxy.getPeerIdAsString()+" prefetched");
                }
            }).start();
        }
        {
            final Vector<String> received = new Vector<String>();
            class RemoveListenerOnDisconnect implements ConnectorListener {
                public void messageReceived(Service service, String localConnectorName, Message message) {
                }

                public void disconnected(Service service, String localConnectorName, int peerId) {
                    service.removeConnectorListener(localConnectorName, this);
                    received.add(service.getPeerIdAsString());
                }

                public void connected(Service service, String localConnectorName, int peerId) {
                }
            }
            Service client = factory.create("I0046Client");
            client.addConnector("bug", "", ConnectorType.INOUTPUT);
            client.start();
            System.err.println(client.getPeerIdAsString());
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0046Server"));
            // Local connect + local disconnect -> ok
            client.addConnectorListener("bug", new RemoveListenerOnDisconnect());
            client.connectTo("bug", proxy, "rec");
            client.addConnectorListener("bug", new RemoveListenerOnDisconnect());
            client.closeAllConnections();
            Thread.sleep(1000);
            if (received.size() != 2) {
                FactoryFactory.failed("Wrong received count (local/local): "+received.size());
                System.exit(1);
            }
            client.removeAllConnectorListeners();
            received.clear();
            
            // Local connect + remote disconnect -> ok
            client.addConnectorListener("bug", new RemoveListenerOnDisconnect());
            client.connectTo("bug", proxy, "rec");
            client.addConnectorListener("bug", new RemoveListenerOnDisconnect());
            client.sendToAllClients("bug", Utility.message("disco"));
            Thread.sleep(1000);
            if (received.size() != 2) {
                FactoryFactory.failed("Wrong received count (local/remote): "+received.size());
            }
            client.removeAllConnectorListeners();
            received.clear();
            
            // Remote connect + local disconnect -> ok
            client.addConnectorListener("bug", new RemoveListenerOnDisconnect());
            proxy.setVariableValue("target", "connect");
            client.addConnectorListener("bug", new RemoveListenerOnDisconnect());
            Thread.sleep(10000);
            client.closeAllConnections();
            Thread.sleep(10000);
            if (received.size() != 2) {
                FactoryFactory.failed("Wrong received count (remote/local): "+received.size());
            }
            client.removeAllConnectorListeners();
            received.clear();

            // Remote connect + remote disconnect -> ok
            client.addConnectorListener("bug", new RemoveListenerOnDisconnect());
            proxy.setVariableValue("target", "connect");
            client.addConnectorListener("bug", new RemoveListenerOnDisconnect());
            Thread.sleep(1000);
            client.sendToAllClients("bug", Utility.message("disco"));
            Thread.sleep(1000);
            if (received.size() != 2) {
                FactoryFactory.failed("Wrong received count (remote/remote): "+received.size());
            }
            client.removeAllConnectorListeners();
            received.clear();

            {
                FactoryFactory.passed("Properly passed all test cases");
            }
        }
    }
}
