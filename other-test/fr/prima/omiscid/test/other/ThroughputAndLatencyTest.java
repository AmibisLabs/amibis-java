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

package fr.prima.omiscid.test.other;

import fr.prima.omiscid.user.connector.ConnectorListener;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.service.impl.ServiceFactoryImpl;
import java.io.IOException;

/**
 *
 */
public class ThroughputAndLatencyTest {

    public static void main(String[] args) throws IOException {
        ServiceFactory f = new ServiceFactoryImpl();
        final Service client = f.create("ThroughputClient");
        client.addConnector("c", "...", ConnectorType.INPUT);
        client.addConnectorListener("c", new ConnectorListener() {
            public void messageReceived(Service service, String localConnectorName, Message message) {
                ThroughputAndLatencyServerTest.handleMessage("service", message.getBuffer());
            }

            public void disconnected(Service service, String localConnectorName, int peerId) {
                service.closeAllConnections();
                System.exit(0);
            }

            public void connected(Service service, String localConnectorName, int peerId) {
            }
        });
        ServiceProxy server = client.findService(ServiceFilters.nameIs(ThroughputAndLatencyServerTest.serviceName));
        client.connectTo("c", server, ThroughputAndLatencyServerTest.connectorName);
    }
}
