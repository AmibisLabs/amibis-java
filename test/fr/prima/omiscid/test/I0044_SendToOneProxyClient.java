/*
 * I0045_SendToOneProxyClient.java
 * 
 * Created on Sep 27, 2007, 2:49:40 PM
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
import fr.prima.omiscid.user.util.Utility;
import java.io.IOException;

/**
 *
 * @author emonet
 */
public class I0044_SendToOneProxyClient {

    public static void main(String[] args) throws IOException {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0044Server");
            server.addConnector("rec", "", ConnectorType.INOUTPUT);
            server.addConnectorListener("rec", new ConnectorListener() {
                public void messageReceived(Service service,
                                            String localConnectorName,
                                            Message message) {
                        FactoryFactory.passed("Message properly received");
                        System.exit(0);
                }

                public void disconnected(Service service,
                                         String localConnectorName, int peerId) {
                }

                public void connected(Service service, String localConnectorName,
                                      int peerId) {
                }
            });
            server.start();
        }{
            Service client = factory.create("I0044Client");
            client.addConnector("bug", "", ConnectorType.OUTPUT);
            client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0044Server"));
            client.connectTo("bug", proxy, "rec");
            client.sendToOneClient("bug", Utility.stringToByteArray("hiiiii"), proxy);
        }
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {}
        FactoryFactory.failed("Timeout logically due to problem in sendToOneClient(..., proxy)");
        System.exit(1);
}
}
