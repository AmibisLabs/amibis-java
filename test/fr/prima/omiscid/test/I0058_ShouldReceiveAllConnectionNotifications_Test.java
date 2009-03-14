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
import fr.prima.omiscid.user.service.ServiceProxy;
import java.io.IOException;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;


import org.junit.Test;

public class I0058_ShouldReceiveAllConnectionNotifications_Test {
    
    // This test also tests the deadlock in race conditions with the cameraman (and many other things)
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws IOException, InterruptedException {
        final ServiceFactory factory = FactoryFactory.factory();
        final Service server = factory.create("I0058Server");
        server.addConnector("ci", "...", ConnectorType.INPUT);
        server.addConnector("co", "...", ConnectorType.OUTPUT);
        server.addConnector("cio", "...", ConnectorType.INOUTPUT);
        server.start();
        final Service client = factory.create("I0058Client");
        ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0058Server"));
        ConnectorListener l = new ConnectorListener() {
            int count = 0;
            public void messageReceived(Service service, String localConnectorName, Message message) {
            }

            public void disconnected(Service service, String localConnectorName, int peerId) {
            }

            public void connected(Service service, String localConnectorName, int peerId) {
                count ++;
                // This also tests the problem with the cameraman
                // A deadlock was caused by this call to synchronized methods from "service" from the listener
                String name = service.getVariableValue("name");
                System.err.println("count is " + count + " after connection on " + name + ":" + localConnectorName);
                if (count == 6) {
                    FactoryFactory.passed("all 6 connected received");
                }
            }
        };
        client.addConnector("ci", "...", ConnectorType.INPUT);
        client.addConnector("co", "...", ConnectorType.OUTPUT);
        client.addConnector("cio", "...", ConnectorType.INOUTPUT);
        client.addConnectorListener("ci", l);
        client.addConnectorListener("co", l);
        client.addConnectorListener("cio", l);
        client.start();
        client.connectTo("ci", proxy, "co");
        client.connectTo("co", proxy, "ci");
        client.connectTo("cio", proxy, "cio");
        proxy = server.findService(ServiceFilters.nameIs("I0058Client"));
        server.connectTo("ci", proxy, "co");
        server.connectTo("co", proxy, "ci");
        server.connectTo("cio", proxy, "cio");
        FactoryFactory.waitResult(1000);
        FactoryFactory.failed("Time out due to missing connection notifications");
    }

}
