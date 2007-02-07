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
import fr.prima.omiscid.user.variable.LocalVariableListener;
import fr.prima.omiscid.user.variable.VariableAccessType;
import java.io.IOException;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.util.Utility;

public class SafeVariableListenerCalls_BugI0004 {
    
    public static void main(String[] args) throws IOException {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("BugI0004Server");
            server.addVariable("bug", "Bug", "plop", VariableAccessType.READ_WRITE);
            server.addLocalVariableListener("bug", new LocalVariableListener() {
                public boolean isValid(Service service, String currentValue, String newValue) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
                public void variableChanged(Service service, String name, String value) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            });
            server.addLocalVariableListener("bug", new LocalVariableListener() {
                boolean passedOnce = false;
                public boolean isValid(Service service, String currentValue, String newValue) {
                    return true;
                }
                public void variableChanged(Service service, String name, String value) {
                    if (passedOnce) {
                        FactoryFactory.passed("Two notifications received by the clean listener");
                        System.exit(0);
                    } else {
                        passedOnce = true;
                    }
                }
            });
            server.start();
        }{
            Service client = factory.create("BugI0004Client");
            client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("BugI0004Server"));
            proxy.setVariableValue("bug", "ga");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            proxy.setVariableValue("bug", "bu");
        }
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {}
        FactoryFactory.failed("Timeout logically due to unhandled exception");
        System.exit(1);
    }

}
