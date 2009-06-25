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
import fr.prima.omiscid.user.service.ServiceRepository;


import org.junit.Test;
import static org.junit.Assert.*;

public class I0056_SharedBrowserShouldProperlyRemoveServices_Test {
    
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws IOException, InterruptedException {
        final ServiceFactory factory = FactoryFactory.factory();
        final ServiceRepository first = factory.createServiceRepository();
        assertNotNull(first);
        //assertTrue("Factory should be Shared", first instanceof SharedWhat);
        final Service server = factory.create("I0056Server");
        server.start();
        final Service client = factory.create("I0056lient");
        client.findService(ServiceFilters.nameIs("I0056Server"));
        server.stop();
        new Thread( new Runnable() {
            public void run() {
                //final Service client = factory.create("I0056lient2");
                /*
                while (client.findService(ServiceFilters.nameIs("I0056Server"), 100) != null) {
                }*/
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    FactoryFactory.failed("Interrupted");
                    Thread.interrupted();
                    return;
                }
                if (first.getAllServices().size() == factory.createServiceRepository().getAllServices().size()) {
                    FactoryFactory.passed("New factory count is ok");
                } else {
                    FactoryFactory.failed("Wrong new factory count");
                }
            }
        }).start();
        FactoryFactory.waitResult(5000);
        FactoryFactory.failed("Time out due to service not disappearing");
    }

}
