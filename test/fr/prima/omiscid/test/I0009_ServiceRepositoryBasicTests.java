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
import fr.prima.omiscid.user.util.Utility;

public class I0009_ServiceRepositoryBasicTests {
    
    public static void main(String[] args) throws IOException, InterruptedException {
        final ServiceFactory factory = FactoryFactory.factory();
        final Service service = factory.create("I0009Repository");
        final ServiceRepository repository = factory.createServiceRepositoy(service);
        ServiceRepositoryListener listener = new ServiceRepositoryListener() {
            int count = 0;
            public void serviceRemoved(ServiceProxy serviceProxy) {
                count--;
                System.out.println("(-)\t"+Utility.intTo8HexString(serviceProxy.getPeerId())+" \t"+count);
            }
            public void serviceAdded(ServiceProxy serviceProxy) {
                count++;
                System.out.println("(+)\t"+Utility.intTo8HexString(serviceProxy.getPeerId())+" \t"+count);
            }
        };
        Thread.sleep(1000);
        repository.addListener(listener);
        repository.removeListener(listener);
        System.out.println("");
        repository.addListener(listener, true);
        repository.removeListener(listener, true);
        System.out.println("");
        Thread.sleep(1000);
        repository.addListener(listener);
    }

}
