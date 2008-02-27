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


public class I0042_SADSendMessageInConnectedListener_Test {

    /**
     * C++ Microphone does speech detection on SAD connector.
     * When someone connects to it, it sends a message indicating the current
     * state. This message is not received by java gui.
     * This example mimic this behavior in pure java.
     * First runs seemed to work but it is not true anymore.
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        ServiceFactory factory = FactoryFactory.factory();
        final Service server = factory.create("I0042Server");
        {
            server.addConnector("SAD", "plop", ConnectorType.OUTPUT);
            server.addConnectorListener("SAD", new ConnectorListener() {
                public void messageReceived(Service service, String localConnectorName, Message message) {
                    System.out.println("Server: received");
                }
                public void disconnected(Service service, String localConnectorName, int peerId) {
                    System.out.println("Server: disconnected");
                }
                public void connected(Service service, String localConnectorName, int peerId) {
                    System.out.println("Server: connected");
                    /* This makes it work but should not be necessary
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(I0042_SADSendMessageInConnectedListener_Test.class.getName()).log(Level.SEVERE, null, ex);
                    }*/
                    service.sendToOneClient(localConnectorName, Utility.message("Hiiiiiii"), peerId);
                }
            });
            server.start();
        }
        {
            Service client = factory.create("I0042Client");
            client.addConnector("bug", "da", ConnectorType.INPUT);
            client.addConnectorListener("bug", new ConnectorListener() {
                public void messageReceived(Service service, String localConnectorName, Message message) {
                    if (message.getBuffer().length != 0) {
                        FactoryFactory.passed("First message received as expected");
                        System.exit(0);
                    } else {
                        FactoryFactory.failed("Second message received but not first");
                        System.exit(1);
                    }
                }
                public void disconnected(Service service, String localConnectorName, int peerId) {
                    System.out.println("Client: disconnected");
                }
                public void connected(Service service, String localConnectorName, int peerId) {
                    System.out.println("Client: connected");
                }
            });
            //client.start();
            ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0042Server"), 5000);
            client.connectTo("bug", proxy, "SAD");
        }
        Thread.sleep(1000);
        server.sendToAllClients("SAD", new byte[0]);
    }
}
