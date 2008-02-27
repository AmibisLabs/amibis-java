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


import org.junit.Test;
import static org.junit.Assert.*;

public class I0023_ServiceFilterHasVariableTruthOnPeerId_Test {
    
    @Test
    public void doIt() throws InvalidDescriptionException, IOException {
        ServiceFactory factory = FactoryFactory.factory();
        final Service server = factory.create("I0023Server");
        server.start();
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    FactoryFactory.failed("Timeout probably due to peerId variable not being seen by hasVariable service filter");
                }
                
            }
        }).start();
        {
            Service client = factory.create("I0023Client");
            final ServiceProxy proxy = client.findService(ServiceFilters.and(ServiceFilters.hasVariable("peerId"), ServiceFilters.nameIs("I0023Server")));
            if (proxy != null) {
                FactoryFactory.passed("Service hasVariable filter properly found the peerId");
            }
        }
    }

}
