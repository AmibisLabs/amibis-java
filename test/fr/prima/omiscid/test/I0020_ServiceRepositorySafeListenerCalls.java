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

public class I0020_ServiceRepositorySafeListenerCalls {
    
    public static void main(String[] args) throws IOException, InterruptedException {
        final ServiceFactory factory = FactoryFactory.factory();
        final Service service = factory.create("I0020Repository");
        final ServiceRepository repository = factory.createServiceRepository(service);
        ServiceRepositoryListener listener1 = new ServiceRepositoryListener() {
            public void serviceRemoved(ServiceProxy serviceProxy) {
                throw new UnsupportedOperationException("Not supported yet: serviceRemoved");
            }
            public void serviceAdded(ServiceProxy serviceProxy) {
                throw new UnsupportedOperationException("Not supported yet: serviceAdded");
            }
        };
        ServiceRepositoryListener listener2 = new ServiceRepositoryListener() {
            int count = 0;
            public void serviceAdded(ServiceProxy serviceProxy) {
                if (serviceProxy.getName().equals("I0020Service")) {
                    count += 1000;
                }
            }
            public void serviceRemoved(ServiceProxy serviceProxy) {
                if (serviceProxy.getName().equals("I0020Service")) {
                    count --;
                    System.err.println("removed: "+count);
                    if (count == 1998) {
                        FactoryFactory.passed("Two additions and removal received by the clean listener");
                        System.exit(0);
                    }
                }
            }
        };
        Service service1 = factory.create("I0020Service");
        service1.start();
        Thread.sleep(1000);
        
        repository.addListener(listener1);
        repository.addListener(listener2);
        
        Service service2 = factory.create("I0020Service");
        service2.start();
        Thread.sleep(1000);

        service2.stop();
        service1.stop();
        Thread.sleep(5000);
        FactoryFactory.failed("Timeout logically due to unhandled exception or application is unclosable (or to an already present I0020Service)");
        System.exit(1);
    }

}
