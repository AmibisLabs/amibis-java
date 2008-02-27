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

import fr.prima.omiscid.user.connector.ConnectorType;
import java.io.IOException;

import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.variable.VariableAccessType;
import static fr.prima.omiscid.user.service.ServiceFilters.*;
import org.junit.Test;

public class I0051_RemoteServiceDescriptions_Test {

    @Test
    public void doIt() throws IOException, InterruptedException {
        ServiceFactory factory = FactoryFactory.factory();
        final Service server = factory.create("I0051Server");
        final String descriptionPrefix = "description tralala ";
        server.addConnector("c1", descriptionPrefix+"c1", ConnectorType.INPUT);
        server.addConnector("c2", descriptionPrefix+"c2", ConnectorType.OUTPUT);
        server.addConnector("c3", descriptionPrefix+"c3", ConnectorType.INOUTPUT);
        server.addVariable("v1", descriptionPrefix + "v1" + "Type", descriptionPrefix + "v1", VariableAccessType.CONSTANT);
        server.addVariable("v2", descriptionPrefix + "v2" + "Type", descriptionPrefix + "v2", VariableAccessType.READ);
        server.addVariable("v3", descriptionPrefix + "v3" + "Type", descriptionPrefix + "v3", VariableAccessType.READ_WRITE);
        server.start();
        {
            Service client = factory.create("I0051Client");
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0051Server"));
            for (String c : new String[]{"c1","c2","c3"}) {
                if (!(descriptionPrefix + c).equals(proxy.getConnectorDescription(c))) {
                    FactoryFactory.failed("Wrong description for connector '"+c+"': "+proxy.getConnectorDescription(c));
                }
            }
            for (String v : new String[]{"v1","v2","v3"}) {
                if (!(descriptionPrefix + v).equals(proxy.getVariableDescription(v))) {
                    FactoryFactory.failed("Wrong description for variable '"+v+"': "+proxy.getVariableDescription(v));
                }
                if (!(descriptionPrefix + v + "Type").equals(proxy.getVariableType(v))) {
                    FactoryFactory.failed("Wrong type for variable '"+v+"': "+proxy.getVariableType(v));
                }
            }
            /*
            for (String v : proxy.getVariables()) {
                System.out.println("Variable "+v);
                System.out.println("--- "+proxy.getVariableDescription(v));
                System.out.println("--- "+proxy.getVariableType(v));
            }
             */
            FactoryFactory.passed("All remote descriptions are ok");
        }
    }
}
