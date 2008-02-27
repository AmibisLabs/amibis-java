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
import fr.prima.omiscid.user.variable.LocalVariableListener;
import fr.prima.omiscid.user.variable.VariableAccessType;
import java.util.Vector;
import org.junit.Test;
import static org.junit.Assert.*;

public class I0053_LocalVariableValidationOnLocalModification_Test {
    
    @Test
    public void doIt() throws IOException, InterruptedException {
        ServiceFactory factory = FactoryFactory.factory();
        final Vector<String> events = new Vector<String>();
        {
            final Service server = factory.create("I0053Server");
            server.addVariable("bug", "Bug", "plop", VariableAccessType.READ_WRITE);
            server.setVariableValue("bug", "0");
            
            server.addLocalVariableListener("bug", new LocalVariableListener() {
                public boolean isValid(Service service, String variableName, String newValue) {
                    if (!variableName.equals("bug")) {
                        FactoryFactory.failed("Wrong variable name received: "+variableName);
                    }
                    events.add("Valid:"+newValue);
                    return true;
                }
                public void variableChanged(Service service, String variableName, String value) {
                    if (!variableName.equals("bug")) {
                        FactoryFactory.failed("Wrong variable name received, in changed: "+variableName);
                    }
                    events.add("Changed:"+value);
                }
            });
            server.setVariableValue("bug", "1");
            server.setVariableValue("bug", "2", false);
            server.setVariableValue("bug", "3", true);
            server.setVariableValue("bug", "4", false);
            server.setVariableValue("bug", "4");
        }
        
        Thread.sleep(500);
        equals("Valid:1", events.remove(0));
        equals("Changed:1", events.remove(0));
        equals("Valid:2", events.remove(0));
        equals("Changed:2", events.remove(0));
        equals("Changed:3", events.remove(0));
        equals("Valid:4", events.remove(0));
        equals("Changed:4", events.remove(0));
        equals(0, events.size());
        FactoryFactory.passed("All is ok");
    }

    private static void equals(Object expected, Object value) {
        assertEquals(expected, value);
    }

}
