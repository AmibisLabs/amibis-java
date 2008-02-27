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

/*
 * This test tries to ensure that basic accessors (such as getPeerId, getName, ...)
 * are non-blocking when a control query is in progress.
 * This basically control that not too much synchronization is put on service proxy.
 */
public class I0024_ServiceProxyAccessorsAvailableDuringQuery_Test {
    
    public static void main(String[] args) throws InvalidDescriptionException, IOException, InterruptedException {
        ServiceFactory factory = FactoryFactory.factory();
        final Service server = factory.create("I0024Server");
        {
            for (int i = 0; i < 3000; i++) {
                server.addVariable("V"+i, "Plop", "Plip", VariableAccessType.CONSTANT);
                server.setVariableValue("V"+i, ""+i);
            }
            server.start();
        }
        Service client = factory.create("I0024Client");
        final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0024Server"));
        new Thread(new Runnable() {
            public void run() {
                proxy.getVariables();
                FactoryFactory.failed("ServiceProxy#getVariables ended before getPeerId did");
                System.exit(1);
            }
        }).start();
        Thread.sleep(200);
        System.out.println("getHostName");
        System.out.println(proxy.getHostName());
        System.out.println("getName");
        System.out.println(proxy.getName());
        System.out.println("getPeerId");
        System.out.println(proxy.getPeerId());
        System.out.println("getPeerIdAsString");
        System.out.println(proxy.getPeerIdAsString());
        System.out.println("getVariableValue(owner)");
        System.out.println(proxy.getVariableValue("owner"));
        Thread.sleep(200);
        FactoryFactory.passed("ServiceProxy accessors have not been blocked by ServiceProxy#getVariables");
        System.exit(0);
    }

}
