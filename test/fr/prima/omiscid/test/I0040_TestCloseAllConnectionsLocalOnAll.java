/*
 * I0040_TestCloseAllConnectionsLocalOnAll.java
 * 
 * Created on 30 août 2007, 16:46:12
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.omiscid.test;

import fr.prima.omiscid.user.connector.ConnectorListener;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

public class I0040_TestCloseAllConnectionsLocalOnAll {
    /*
     * This tests the new disconnection feature on all connectors.
     * It tests effective-disconnection (remote listener does not receive messages)
     * for a all locally-closed locally-initiated connections.
     */
    public static void main(String[] args) throws IOException {
        final Vector<String> events = new Vector<String>();
        ServiceFactory factory = FactoryFactory.factory();
        Vector<Service> servers = new Vector<Service>();
        for (int i = 0; i < 4; i++) {
            final Service server = factory.create("I0040Server-"+i);
            servers.add(server);
            server.addConnector("bug1", "", ConnectorType.INPUT);
            server.addConnector("bug2", "", ConnectorType.INOUTPUT);
            ConnectorListener l = new ConnectorListener() {
                boolean passed = false;
                public void messageReceived(Service service,
                                            String localConnectorName,
                                            Message message) {
                    events.add("SV REC "+passed);
                    System.out.println(Arrays.toString(events.toArray()));
                    FactoryFactory.failed("Second message received while connection should have been closed");
                    System.exit(1);
                }

                public void disconnected(Service service, String localConnectorName, int peerId) {
                    events.add("SV DIS "+passed);
                }

                public void connected(final Service service, final String localConnectorName, final int peerId) {
                    events.add("SV CON "+passed);
                }
            };
            server.addConnectorListener("bug1", l);
            server.addConnectorListener("bug2", l);
            server.start();
        }
        {
            final Service client = factory.create("I0040Client");
            client.addConnector("ug1", "", ConnectorType.OUTPUT);
            client.addConnector("ug2", "", ConnectorType.INOUTPUT);
            //client.start();
            for (int i = 0; i < 4; i++) {
                final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0040Server-"+i));
                client.connectTo("ug1", proxy, "bug1");
                client.connectTo("ug2", proxy, "bug2");
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
            client.closeAllConnections();
            client.sendToAllClients("ug1", new byte[0]);
            client.sendToAllClients("ug2", new byte[0]);
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {}
        System.out.println(Arrays.toString(events.toArray()));
        FactoryFactory.passed("All messages sent, none received");
        System.exit(0);
    }

}
