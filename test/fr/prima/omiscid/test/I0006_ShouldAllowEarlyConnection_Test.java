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

import java.io.IOException;
import java.util.Vector;

import fr.prima.omiscid.user.connector.ConnectorListener;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.util.Utility;


import org.junit.Test;
import static org.junit.Assert.*;

public class I0006_ShouldAllowEarlyConnection_Test {
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws IOException, InterruptedException {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0006Server");
            server.addConnector("bug", "", ConnectorType.INPUT);
            server.addConnectorListener("bug", new ConnectorListener() {
                
                public void messageReceived(final Service service, final String localConnectorName, final Message message) {
                    System.out.println("server received");
                    service.sendToOneClient(localConnectorName, new byte[10], message.getPeerId());
                    System.out.println("server sent");
                }
            
                public void disconnected(Service service, String localConnectorName, int peerId) {
                    // TODO Auto-generated method stub
            
                }
            
                public void connected(Service service, String localConnectorName, int peerId) {
                    // TODO Auto-generated method stub
            
                }
            });
            server.start();
        }
        {
            final Vector<Object> ended = new Vector<Object>();
            final Vector<Object> started = new Vector<Object>();
            for (int i = 0; i < 3; i++) {
                Service client = factory.create("I0006Client");
                client.addConnector("bug", "", ConnectorType.OUTPUT);
                client.addConnectorListener("bug", new ConnectorListener() {
                    int count = 0;

                    public void messageReceived(final Service service, String localConnectorName, Message message) {
                        count++;
                        if (count >= 20) {
                            ended.add(service);
                        } else {
                            service.sendToAllClients(localConnectorName, new byte[1]);
                        }
                    }

                    public void disconnected(Service service, String localConnectorName, int peerId) {
                    }

                    public void connected(Service service, String localConnectorName, int peerId) {
                    }
                });
                final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0006Server"));
                client.connectTo("bug", proxy, "bug");
                client.sendToAllClients("bug", Utility.stringToByteArray("hiiiii"));
                started.add(client);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            int endedSize = ended.size();
            if (endedSize == started.size()) {
                FactoryFactory.passed("all "+started.size()+" ok");
            } else {
                FactoryFactory.failed("started is "+started.size()+" and only "+endedSize+" ended");
            }
        }
    }

}
