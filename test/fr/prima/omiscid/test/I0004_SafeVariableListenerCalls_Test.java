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

import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.variable.LocalVariableListener;
import fr.prima.omiscid.user.variable.VariableAccessType;


import org.junit.Test;
import static org.junit.Assert.*;

public class I0004_SafeVariableListenerCalls_Test {
    
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws IOException, InterruptedException {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0004Server");
            server.addVariable("bug", "Bug", "plop", VariableAccessType.READ_WRITE);
            server.addVariable("bug2", "Bug", "plop", VariableAccessType.READ_WRITE);
            server.setVariableValue("bug", "12");
            server.addLocalVariableListener("bug", new LocalVariableListener() {
                public boolean isValid(Service service, String variableName, String newValue) {
                    if (!variableName.equals("bug")) {
                        FactoryFactory.failed("Wrong variable name received: "+variableName);
                    }
                    if (newValue.length() != 4) {
                        FactoryFactory.failed("Wrong variable new value received: "+newValue);
                    }
                    throw new UnsupportedOperationException("Not supported yet: isValid");
                }
                public void variableChanged(Service service, String variableName, String value) {
                    if (!variableName.equals("bug")) {
                        FactoryFactory.failed("Wrong variable name received, in changed: "+variableName);
                    }
                    if (value.length() != 4) {
                        FactoryFactory.failed("Wrong variable set value received, in changed: "+value);
                    }
                    throw new UnsupportedOperationException("Not supported yet: changed");
                }
            });
            server.addLocalVariableListener("bug", new LocalVariableListener() {
                boolean passedOnce = false;
                public boolean isValid(Service service, String variableName, String newValue) {
                    if (!variableName.equals("bug")) {
                        FactoryFactory.failed("Wrong variable name received (2): "+variableName);
                    }
                    return true;
                }
                public void variableChanged(Service service, String name, String value) {
                    if (passedOnce) {
                        FactoryFactory.passed("Two notifications received by the clean listener");
                    } else {
                        passedOnce = true;
                    }
                }
            });
            server.start();
        }{
            Service client = factory.create("I0004Client");
            client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0004Server"));
            proxy.setVariableValue("bug", "gaga");
            proxy.setVariableValue("bug2", "gogo");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            proxy.setVariableValue("bug", "bubu");
        }
        FactoryFactory.waitResult(1500);
    }

}
