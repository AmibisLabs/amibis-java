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


import org.junit.Test;
import static org.junit.Assert.*;

public class I0011_StressTestManyBigMessages_Test {
    
    private static final int smallSize = 1;
    private static final int bigSize = 1024*1024;

    private static final int clientsToStart = 3;
    private static final int messagesToSend = 100;
    private static final int timeToWait = 5000;
    
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws IOException, InterruptedException {
        final ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0011Server");
            server.addConnector("c", "big messages", ConnectorType.INOUTPUT);
            server.addConnectorListener("c", new ConnectorListener() {
            
                public void messageReceived(final Service service, final String localConnectorName, final Message message) {
                    System.out.println("server received");
                    service.sendToAllClients(localConnectorName, new byte[bigSize]/*, message.getPeerId()*/);
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
        final Vector<Object> ended = new Vector<Object>();
        final Vector<Object> started = new Vector<Object>();
        for (int i = 0; i < clientsToStart; i++) {
            new Thread(new Runnable() {

                public void run() {
                    Service client = factory.create("I0011Client");
                    try {
                        client.addConnector("c", "big messages", ConnectorType.INOUTPUT);
                    } catch (IOException e) {
                        e.printStackTrace();
                        FactoryFactory.failed("Exception in addConnector");
                    }
                    client.addConnectorListener("c", new ConnectorListener() {

                        int count = 0;

                        public void messageReceived(final Service service, String localConnectorName, Message message) {
                            System.out.println("client received, count is " + count);
                            service.sendToAllClients("c", new byte[smallSize]);
                            System.out.println("client sent");
                            count++;
                            if (count >= messagesToSend) {
                                service.stop();
                                ended.add(service);
                            }
                        }

                        public void disconnected(Service service, String localConnectorName, int peerId) {
                        // TODO Auto-generated method stub

                        }

                        public void connected(Service service, String localConnectorName, int peerId) {
                        // TODO Auto-generated method stub

                        }
                    });
                    client.start();
                    final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0011Server"));
                    client.connectTo("c", proxy, "c");
                    client.sendToAllClients("c", new byte[smallSize]);
                    started.add(client);
                }
            }).start();
            Thread.sleep(1000);
        }
        Thread.sleep(timeToWait);
        int endedSize = ended.size();
        if (endedSize == started.size()) {
            FactoryFactory.passed("all "+started.size()+" ok");
        } else {
            FactoryFactory.failed("started is "+started.size()+" and only "+endedSize+" ended");
        }
    }

}
