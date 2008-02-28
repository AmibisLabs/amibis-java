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

import fr.prima.omiscid.user.exception.InvalidDescriptionException;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.variable.VariableAccessType;


import org.junit.Test;
import static org.junit.Assert.*;

public class I0022_VariableListContainsPeerIdWhenNonFull_Test {
    
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws InvalidDescriptionException, IOException {
        ServiceFactory factory = FactoryFactory.factory();
        final Service server = factory.create("I0022Server");
        {
            for (int i = 0; i < 3000; i++) {
                server.addVariable("V"+i, "Plop", "Plip", VariableAccessType.CONSTANT);
                server.setVariableValue("V"+i, ""+i);
            }
            server.start();
        }
        {
            Service client = factory.create("I0022Client");
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0022Server"));
            if (proxy.getVariables().contains("peerId")) {
                if (proxy.getVariableValue("peerId").equals(server.getPeerIdAsString())) {
                    FactoryFactory.passed("Service variables does have a peerId value with the right value");
                } else {
                    FactoryFactory.failed("Service variables does have a peerId value but its value is wrong ("+proxy.getVariableValue("peerId")+")");
                }
            } else {
                FactoryFactory.failed("Service variables doesn't have a peerId value");
            }
        }
    }

}
