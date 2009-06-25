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

import fr.prima.omiscid.user.connector.ConnectorType;
import java.io.IOException;

import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import java.util.HashMap;
import java.util.Map;
import static fr.prima.omiscid.user.service.ServiceFilters.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

public class I0052_GetConnectorClientCount_Test {

    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws IOException, InterruptedException {
        ServiceFactory factory = FactoryFactory.factory();
        final Service server = factory.create("I0052Server");
        final String descriptionPrefix = "description tralala ";
        server.addConnector("c1", "", ConnectorType.INPUT);
        server.addConnector("c2", "", ConnectorType.OUTPUT);
        server.addConnector("c3", "", ConnectorType.INOUTPUT);
        server.start();
        final Map<Service, ServiceProxy> clients = new HashMap<Service, ServiceProxy>();
        for (int i = 0; i < 3; i++) {
            Service client = factory.create("I0052Client");
            client.addConnector("c1", "", ConnectorType.OUTPUT);
            client.addConnector("c2", "", ConnectorType.INPUT);
            client.addConnector("c3", "", ConnectorType.INOUTPUT);
            ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0052Server"));
            client.connectTo("c1", proxy, "c1");
            client.closeAllConnections();
            clients.put(client, proxy);
        }
        int serviceIndex = 0;
        final Object sync = new Object();
        for (final Service client : clients.keySet()) {
            serviceIndex++;
            final int si = serviceIndex;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        synchronized (sync) {
                            sync.wait();
                        }
                        At at = new At();
                        at.sleep(500 * si);
                        for (int i = 1; i <= 3; i++) {
                            //System.err.print("... ");
                            client.connectTo("c" + i, clients.get(client), "c" + i);
                            //System.err.println("c"+i);
                            at.sleep(300);
                        }
                        at.sleep(200);
                        client.closeAllConnections();
                        //System.err.println("... -c1 -c2 -c3");
                    } catch (InterruptedException ex) {
                        Logger.getLogger(I0052_GetConnectorClientCount_Test.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
        }
        try {
            sleep(100);
            synchronized (sync) {
                sync.notifyAll();
            }
            At at = new At();
            at.sleep(50);
            System.err.println("-----");
            equals(0, server.getConnectorClientCount("c1"));
            equals(0, server.getConnectorClientCount("c2"));
            equals(0, server.getConnectorClientCount("c3"));
            at.sleep(500);
            System.err.println("-----");
            equals(1, server.getConnectorClientCount("c1"));
            equals(0, server.getConnectorClientCount("c2"));
            equals(0, server.getConnectorClientCount("c3"));
            at.sleep(600);
            System.err.println("-----");
            equals(2, server.getConnectorClientCount("c1"));
            equals(1, server.getConnectorClientCount("c2"));
            equals(1, server.getConnectorClientCount("c3"));
            at.sleep(400);
            System.err.println("-----");
            equals(3, server.getConnectorClientCount("c1"));
            equals(2, server.getConnectorClientCount("c2"));
            equals(1, server.getConnectorClientCount("c3"));
            at.sleep(100);
            System.err.println("-----");
            equals(2, server.getConnectorClientCount("c1"));
            equals(1, server.getConnectorClientCount("c2"));
            equals(1, server.getConnectorClientCount("c3"));
            at.sleep(200);
            System.err.println("-----");
            equals(2, server.getConnectorClientCount("c1"));
            equals(2, server.getConnectorClientCount("c2"));
            equals(1, server.getConnectorClientCount("c3"));
            server.closeAllConnections();
            System.err.println("/////");
            equals(0, server.getConnectorClientCount("c1"));
            equals(0, server.getConnectorClientCount("c2"));
            equals(0, server.getConnectorClientCount("c3"));
            at.sleep(400);
            System.err.println("-----");
            equals(0, server.getConnectorClientCount("c1"));
            equals(0, server.getConnectorClientCount("c2"));
            equals(1, server.getConnectorClientCount("c3"));
            at.sleep(400);
            System.err.println("-----");
            equals(0, server.getConnectorClientCount("c1"));
            equals(0, server.getConnectorClientCount("c2"));
            equals(0, server.getConnectorClientCount("c3"));
        } catch (Exception e) {
            e.printStackTrace();
            FactoryFactory.failed("Assertion failed: "+e.getMessage());
        }

        FactoryFactory.passed("All is ok");
    }

    private static void equals(Object expected, Object value) {
        assertEquals(expected, value);
    }

    private static class At {

        long next;

        At() {
            this(System.currentTimeMillis());
        }
        At(long time) {
            next = time;
        }

        void sleep(long delay) {
            next += delay;
            long time;
            while ((time = System.currentTimeMillis()) - next < 0) {
                try {
                    Thread.sleep(next - time);
                } catch (InterruptedException ex) {
                    Logger.getLogger(I0052_GetConnectorClientCount_Test.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    private static void sleep(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException ex) {
            Logger.getLogger(I0052_GetConnectorClientCount_Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
