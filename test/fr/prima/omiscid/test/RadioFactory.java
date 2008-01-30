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
