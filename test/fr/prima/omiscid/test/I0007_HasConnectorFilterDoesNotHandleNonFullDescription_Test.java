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

import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.variable.VariableAccessType;

public class I0007_HasConnectorFilterDoesNotHandleNonFullDescription_Test {
    // This is not a bug in fact it already works (at the time of writing)
    public static void main(String[] args) throws IOException, InterruptedException {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0007Server");
            server.addConnector("bug", "", ConnectorType.INPUT);
            server.addVariable("thisIsAnObfuscatingVariable", "string", "plop", VariableAccessType.CONSTANT);
            server.setVariableValue("thisIsAnObfuscatingVariable", times("A",300));
            server.start();
        }
        {
            final Service client = factory.create("I0007Client");
            client.start();
            new Thread(new Runnable() {
                public void run() {
                    client.findService(ServiceFilters.hasConnector("bug", ConnectorType.INOUTPUT));
                    FactoryFactory.failed("server was wrongly found");
                    System.exit(1);
                }
            }).start();
            Thread.sleep(250);
            new Thread(new Runnable() {
                public void run() {
                    client.findService(ServiceFilters.hasConnector("bug"));
                    client.findService(ServiceFilters.hasConnector("bug", ConnectorType.INPUT));
                    FactoryFactory.passed("server was properly found each time");
                    System.exit(0);
                }
            }).start();
            Thread.sleep(3000);
            FactoryFactory.failed("Timeout logically due to wrong handling of incomplete dnssd description");
            System.exit(1);
        }
    }

    private static String times(String pattern, int count) {
        if (count == 0) return "";
        else return pattern+times(pattern, count-1);
    }

}
