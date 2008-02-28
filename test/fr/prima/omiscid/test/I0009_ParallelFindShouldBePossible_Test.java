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
import java.util.Vector;

import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;


import org.junit.Test;
import static org.junit.Assert.*;

public class I0009_ParallelFindShouldBePossible_Test {
    
    // This is not a bug at the time of writing.
    // This test ensures that searchs can be done in parallel.
    // A bunch of parallel searchs are started.
    
    static int parallelCount = 50;
    static long eachTaskTimeOut = 100;
    static long overallTimeOut = 500;
    
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws IOException, InterruptedException {
        final ServiceFactory factory = FactoryFactory.factory();
        {
            final Service client = factory.create("I0009Client");
            client.addConnector("bug", "plop", ConnectorType.INPUT);
            client.start();
            client.findService(ServiceFilters.nameIs("I0009Client"));
            final Vector<String> done = new Vector<String>();
            final Vector<String> started = new Vector<String>();
            for (int i = 0; i < parallelCount; i++) {
                final int finalI = i;
                new Thread(new Runnable() {
                    public void run() {
                        String name = "NoN PrEsEnT SeRvIcE";
                        ServiceProxy proxy = client.findService(ServiceFilters.nameIs(name), eachTaskTimeOut);
                        if (proxy != null) {
                            FactoryFactory.failed("Service found but we supposed it won't. Name is "+name);
                        }
                        done.add(Integer.toBinaryString(finalI));
                    }
                }).start();
                started.add(Integer.toBinaryString(i));
            }
            Thread.sleep(overallTimeOut);
            if (done.size() == started.size()) {
                FactoryFactory.passed("Search done properly. All "+done.size()+" ok.");
            } else {
                FactoryFactory.failed("Some undesired exceptions have probably occured. Only "+done.size()+"/"+started.size()+" ended as expected.");
            }
            FactoryFactory.waitResult(0);
        }
    }

}
