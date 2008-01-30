/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.omiscid.test;

import fr.prima.omiscid.user.connector.ConnectorListener;
import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.exception.MessageInterpretationException;
import fr.prima.omiscid.user.service.Service;

/*- IGNORE -*/
import java.io.IOException;
public class RadioFactory {

    public static void main(String[] args) throws IOException {
        Service service = FactoryFactory.factory().create("RadioFactory");
        service.addVariable("knowledge", "UFL-1.0", "knowledge", fr.prima.omiscid.user.variable.VariableAccessType.CONSTANT);
        service.setVariableValue("knowledge",
                "namespace is http://emonet@prima/\n" +
                " composing grounding \"C(create)\" format \"{?url}\"\n" +
                " gives a Radio with url = ?url");
        service.addConnector("create", "instantiation connector", fr.prima.omiscid.user.connector.ConnectorType.INPUT);
        service.addConnectorListener("create", new ConnectorListener() {
            public void messageReceived(Service service, String localConnectorName, final Message message) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            new Radio(new String[]{message.getBufferAsString()});
                        } catch (MessageInterpretationException ex) {
                            System.err.println("Wrong message received");
                        }
                    }
                }).start();
            }
            public void disconnected(Service service, String localConnectorName, int peerId) {
            }
            public void connected(Service service, String localConnectorName, int peerId) {
            }
        });
        service.start();

    }
}
