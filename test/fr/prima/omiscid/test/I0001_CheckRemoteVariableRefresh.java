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


import java.util.Arrays;
import java.util.Vector;

import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.variable.VariableAccessType;

public class I0001_CheckRemoteVariableRefresh {
    
    public static void main(String[] args) {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0001Server");
            server.addVariable("bug", "", "an allway moving variable", VariableAccessType.READ);
            server.start();
            new Thread() {
                @Override
                public void run() {
                    int i = 0;
                    while (true) {
                        i++;
                        try {
                            Thread.sleep(100);
                            server.setVariableValue("bug", ""+i);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
        {
            Service client = factory.create("I0001Client");
            client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0001Server"));
            new Thread() {
                @Override
                public void run() {
                    String val = proxy.getVariableValue("bug");
                    Vector<String> res = new Vector<String>();
                    while (res.size() < 5) {
                        try {
                            Thread.sleep(200);
                            String newVal = proxy.getVariableValue("bug");
                            if (newVal.equals(val)) {
                                FactoryFactory.failed("value constant at "+val);
                                System.exit(1);
                            }
                            res.add(val);
                            val = newVal;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    FactoryFactory.passed(Arrays.toString(res.toArray()));
                    System.exit(0);
                }
            }.start();
        }

    }

}
