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
import java.util.Timer;
import java.util.TimerTask;

public class I0048_TestConnectorWithDash {
    
    /*
     * Following a problem encountered by Jean Pascal where
     * only adding a sync-in connector to a service makes its
     * imageStream connector not connectable by gui.
     */
    public static void main(String[] args) throws IOException {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0048Server");
            server.addConnector("bug", "", ConnectorType.OUTPUT);
            server.addConnector("dash-bug", "", ConnectorType.OUTPUT);
            server.addConnector("more-bug_!/\\`\"~", "", ConnectorType.OUTPUT);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    server.sendToAllClients("bug", Utility.message("ok"));
                }
            }, 100, 100);
            server.start();
        }
        {
            Service client = factory.create("I0048Client");
            client.addConnector("bug", "", ConnectorType.OUTPUT);
            client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0048Server"));
            try {
                client.connectTo("bug", proxy, "bug");
            } catch (Exception e) {
                FactoryFactory.failed("Exception in connectTo probably due to dash-bug connector");
                System.exit(1);
            }
            client.addConnectorListener("bug", new ConnectorListener() {

                public void messageReceived(Service service, String localConnectorName, Message message) {
                    FactoryFactory.passed("Message received");
                    System.exit(0);
                }

                public void disconnected(Service service, String localConnectorName, int peerId) {
                }

                public void connected(Service service, String localConnectorName, int peerId) {
                }
            });
        }
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }
        FactoryFactory.failed("Timeout logically due to problem with dash-bug connector");
        System.exit(1);
    }

}
