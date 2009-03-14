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

public class I0059_VariableTestingCDataSafety_Test {
    
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws InvalidDescriptionException, IOException {
        ServiceFactory factory = FactoryFactory.factory();
        final Service server = factory.create("I0059Server");
        String var = "haha ]]> ho & &amp; <[CDATA[ &lt &lt;\\ho";
        server.addVariable("cdata", "raw", "raw", VariableAccessType.READ);
        server.setVariableValue("cdata", var);
        server.start();
        {
            Service client = factory.create("I0059Client");
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0059Server"));
            if (var.equals(proxy.getVariableValue("cdata"))) {
                FactoryFactory.passed("Service variable with tricky content for custom xml escaping has the right value");
            } else {
                FactoryFactory.failed("Service variable is wrongly escaped in cdata (" + proxy.getVariableValue("cdata") + ") instead of (" + var + ")");
            }
        }
    }

}
