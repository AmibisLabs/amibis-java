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
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import java.io.IOException;


/*- IGNORE -*/
public class I0043_SADSendMessageInConnectedListenerWithReal {

    /**
     * C++ Microphone does speech detection on SAD connector.
     * When someone connects to it, it sends a message indicating the current
     * state. This message is not received by java gui.
     * This example mimic this behavior in pure java.
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        ServiceFactory factory = FactoryFactory.factory();
        {
            Service client = factory.create("I0043Client");
            client.addConnector("bug", "da", ConnectorType.INPUT);
            client.addConnectorListener("bug", new ConnectorListener() {
                public void messageReceived(Service service, String localConnectorName, Message message) {
                    System.out.println("\"\"\""+message.getBufferAsStringUnchecked()+"\"\"\"");
                    if (!"bip...".equals(message.getBufferAsStringUnchecked().replaceAll("[\\r\\n]", ""))) {
                        FactoryFactory.passed("First message received as expected");
                    } else {
                        FactoryFactory.failed("Second message received but not first");
                    }
                }
                public void disconnected(Service service, String localConnectorName, int peerId) {
                    FactoryFactory.failed("Received a Disconnected notification");
                }
                public void connected(Service service, String localConnectorName, int peerId) {
                    FactoryFactory.failed("Received a Connected notification");
                }
            });
            client.start();
            ServiceProxy proxy = client.findService(ServiceFilters.nameIs("Microphone"), 5000);
            client.connectTo("bug", proxy, "SAD");
        }
        Thread.sleep(2000);
        FactoryFactory.failed("Timed out");
    }

}
