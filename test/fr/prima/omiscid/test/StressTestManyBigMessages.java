/*
 * Created on 14 déc. 06
 *
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

public class StressTestManyBigMessages {
    
    public static void main(String[] args) throws IOException, InterruptedException {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("Server");
            server.addConnector("c", "big messages", ConnectorType.INOUTPUT);
            server.addConnectorListener("c", new ConnectorListener() {
            
                public void messageReceived(Service service, String localConnectorName, Message message) {
                    System.out.println("server received");
                    service.sendToOneClient(localConnectorName, new byte[1024*5], message.getPeerId());
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
        int started = 3;
        for (int i = 0; i < started; i++) {
            Service client = factory.create("Client");
            client.addConnector("c", "", ConnectorType.INOUTPUT);
            client.addConnectorListener("c", new ConnectorListener() {
                int count = 0;
            
                public void messageReceived(Service service, String localConnectorName, Message message) {
                    System.out.println("client received, count is "+count);
                    service.sendToAllClients("c", new byte[0]);
                    count++;
                    if (count == 5) {
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
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("Server"));
            client.connectTo("c", proxy, "c");
            client.sendToAllClients("c", new byte[0]);
            Thread.sleep(2000);
        }
        Thread.sleep(2000);
        int endedSize = ended.size();
        if (endedSize == started) {
            FactoryFactory.passed("all "+started+" ok");
        } else {
            FactoryFactory.failed("started is "+started+" and only "+endedSize+" ended");
        }
        System.exit(0);
    }

}
