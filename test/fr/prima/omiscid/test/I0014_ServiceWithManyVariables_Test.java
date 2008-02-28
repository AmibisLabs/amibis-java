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
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.service.ServiceRepository;
import fr.prima.omiscid.user.service.ServiceRepositoryListener;
import fr.prima.omiscid.user.variable.VariableAccessType;

/*
 * This test aims at improving support for services with many variable
 * (and probably more generally with bigger txt records targeted).
 * The mdns based implementation seems to be working well at the time of writing.
 * Implementations based on jmdns and avahi seems to allow services to have bigger
 * txt records but does not handle very well cases with too many variables (count == 200/300).
 * 
 * One variable should use 8 bytes (size,V,9,9,9,9,=,w).
 * 
 * Failure with avahi:
 *  - 161: visible after declaration
 *  - 162: not visible after (successful) declaration
 *  -- 161 * 8 = 1288
 *  -- ~+ avahi cookie (39), name (18), class (14), owner (12), lock (7), desc (10)
 *  -- ~= 1388
 *  - fixed by ensuring a maximum size for properties in avahi dnssd implementation
 *  -- taken some safety margin on size
 *  -- now works correctly with full service description until 113 variables (was 161)
 *  -- with partial service description after (was crashing)
 * 
 * Failure with jmdns:
 *  - 160: visible after declaration
 *  - 161: java.io.IOException: buffer full during declaration on a jmdns thread
 *  - about the same size ... use a similar solution
 *  -- now works correctly with full service description until 119 variables (was 160)
 *  -- with partial service description after (was crashing)
 */

import org.junit.Test;
import static org.junit.Assert.*;

public class I0014_ServiceWithManyVariables_Test {
    
    private static int customVariableCount = 1000;
    private static String[] chars = new String[]{"V","v","a","b","c","d","e","f","g","h"}; // ten digits (at ten-thousands level)
    
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws IOException, InterruptedException {
        final ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0014Server");
            for (int i = 0; i < customVariableCount; i++) {
                server.addVariable(chars[i/10000]+(i%10000 / 1000)+(i%1000 / 100)+(i%100 / 10)+(i%10), "void", "non", VariableAccessType.READ_WRITE);
            }
            server.start();
        }{
            ServiceRepository serviceRepository = factory.createServiceRepository();
            serviceRepository.addListener(new ServiceRepositoryListener() {
                public void serviceAdded(ServiceProxy serviceProxy) {
                    if (serviceProxy.getName().equals("I0014Server") && serviceProxy.getVariables().size() > customVariableCount) {
                        FactoryFactory.passed("service with many variables ("+serviceProxy.getVariables().size()+" is more than "+customVariableCount+") was found");
                    }
                }
                public void serviceRemoved(ServiceProxy serviceProxy) {
                }
            });
        }
        FactoryFactory.waitResult(10000);
        FactoryFactory.failed("service with many variables was not found by service repository");
    }

}
