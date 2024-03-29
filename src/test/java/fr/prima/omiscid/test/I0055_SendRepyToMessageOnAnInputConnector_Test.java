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
import fr.prima.omiscid.user.exception.IncorrectConnectorType;
import java.io.IOException;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.util.Utility;


import org.junit.Test;
import static org.junit.Assert.*;

public class I0055_SendRepyToMessageOnAnInputConnector_Test {
    
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws IOException, InterruptedException {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0055Server");
            server.addConnector("bug", "", ConnectorType.INPUT);
            server.addConnectorListener("bug", new ConnectorListener() {

                public void messageReceived(Service service, String localConnectorName, Message message) {
                    try {
                        service.sendReplyToMessage(Utility.message("hohoho!!!"), message);
                    } catch (IncorrectConnectorType e) {
                        FactoryFactory.passed("Exception properly thrown");
                    }
                }

                public void disconnected(Service service, String localConnectorName, int peerId) {
                }

                public void connected(Service service, String localConnectorName, int peerId) {
                }
            });
            server.start();
        }{
            Service client = factory.create("I0055lient");
            client.addConnector("bug", "", ConnectorType.OUTPUT);
            client.start();
            client.addConnectorListener("bug", new ConnectorListener() {
                public void messageReceived(Service service, String localConnectorName, Message message) {
                    FactoryFactory.failed("Message received but should not: "+message.getBufferAsStringUnchecked());
                }
                public void disconnected(Service service, String localConnectorName, int peerId) {
                }
                public void connected(Service service, String localConnectorName, int peerId) {
                }
            });
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0055Server"));
            client.connectTo("bug", proxy, "bug");
            client.sendToAllClients("bug", Utility.stringToByteArray("hiiiii"));
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {}
            client.stop();
        }
        FactoryFactory.waitResult(1500);
    }

}
