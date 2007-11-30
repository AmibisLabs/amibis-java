/*
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
import fr.prima.omiscid.user.variable.LocalVariableListener;
import fr.prima.omiscid.user.variable.VariableAccessType;
import java.io.IOException;
import java.util.Vector;

/**
 *
 * @author emonet
 */
public class I0046_RemoveConnectorListenerOnDisconnect {
    
    // At the time of writting, removing a listener from a disconnected call
    // causes a concurrent modification (when there is more than one listener).
    // There are 2 cases: TCPClient cases (initiated locally) and TCPServer ones.
    // There are also multiple cases depending on which side closes the connection.
    //  Local connect  + local disconnect  -> ok
    //  Local connect  + remote disconnect -> ok
    //  Remote connect + local disconnect -> ok
    //  Remote connect + remote disconnect -> ok
    public static void main(String[] args) throws IOException, InterruptedException {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0046Server");
            server.addVariable("target", "string", "", VariableAccessType.READ_WRITE);
            server.addConnector("rec", "", ConnectorType.INOUTPUT);
            server.addLocalVariableListener("target", new LocalVariableListener() {

                public void variableChanged(Service service, String variableName, String value) {
                }

                public boolean isValid(Service service, String variableName, String newValue) {
                    final ServiceProxy proxy = service.findService(ServiceFilters.nameIs("I0046Client"));
                    service.connectTo("rec", proxy, "bug");
                    System.out.println("Server received connection order");
                    return false;
                }
            });
            server.addConnectorListener("rec", new ConnectorListener() {

                public void messageReceived(Service service, String localConnectorName, Message message) {
                    System.out.println("Server received disconnection order");
                    service.closeAllConnections();
                }

                public void disconnected(Service service, String localConnectorName, int peerId) {
                }

                public void connected(Service service, String localConnectorName, int peerId) {
                }
            });
            server.start();
        }
        {
            final Vector<String> received = new Vector<String>();
            class RemoveListenerOnDisconnect implements ConnectorListener {
                public void messageReceived(Service service, String localConnectorName, Message message) {
                }

                public void disconnected(Service service, String localConnectorName, int peerId) {
                    service.removeConnectorListener(localConnectorName, this);
                    received.add(service.getPeerIdAsString());
                }

                public void connected(Service service, String localConnectorName, int peerId) {
                }
            }
            Service client = factory.create("I0046Client");
            client.addConnector("bug", "", ConnectorType.INOUTPUT);
            client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0046Server"));
            // Local connect + local disconnect -> ok
            client.connectTo("bug", proxy, "rec");
            client.addConnectorListener("bug", new RemoveListenerOnDisconnect());
            client.addConnectorListener("bug", new RemoveListenerOnDisconnect());
            client.closeAllConnections();
            Thread.sleep(1000);
            if (received.size() != 2) {
                FactoryFactory.failed("Wrong received count (local/local): "+received.size());
                System.exit(1);
            }
            client.removeAllConnectorListeners();
            received.clear();
            
            // Local connect + remote disconnect -> ok
            client.connectTo("bug", proxy, "rec");
            client.addConnectorListener("bug", new RemoveListenerOnDisconnect());
            client.addConnectorListener("bug", new RemoveListenerOnDisconnect());
            client.sendToAllClients("bug", Utility.message("disco"));
            Thread.sleep(1000);
            if (received.size() != 2) {
                FactoryFactory.failed("Wrong received count (local/remote): "+received.size());
                System.exit(1);
            }
            client.removeAllConnectorListeners();
            received.clear();
            
            // Remote connect + local disconnect -> ok
            proxy.setVariableValue("target", "connect");
            client.addConnectorListener("bug", new RemoveListenerOnDisconnect());
            client.addConnectorListener("bug", new RemoveListenerOnDisconnect());
            client.closeAllConnections();
            Thread.sleep(1000);
            if (received.size() != 2) {
                FactoryFactory.failed("Wrong received count (remote/local): "+received.size());
                System.exit(1);
            }
            client.removeAllConnectorListeners();
            received.clear();

            // Remote connect + remote disconnect -> ok
            proxy.setVariableValue("target", "connect");
            client.addConnectorListener("bug", new RemoveListenerOnDisconnect());
            client.addConnectorListener("bug", new RemoveListenerOnDisconnect());
            client.sendToAllClients("bug", Utility.message("disco"));
            Thread.sleep(1000);
            if (received.size() != 2) {
                FactoryFactory.failed("Wrong received count (remote/remote): "+received.size());
                System.exit(1);
            }
            client.removeAllConnectorListeners();
            received.clear();

            {
                FactoryFactory.passed("Properly passed all test cases");
                System.exit(0);
            }
        }
    }
}
