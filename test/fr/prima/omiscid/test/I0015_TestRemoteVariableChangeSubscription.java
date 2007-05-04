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
import fr.prima.omiscid.user.variable.RemoteVariableChangeListener;
import fr.prima.omiscid.user.variable.VariableAccessType;

public class I0015_TestRemoteVariableChangeSubscription {
    
    public static void main(String[] args) {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0015Server");
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
            Service client = factory.create("I0015Client");
            client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0015Server"));
            proxy.addRemoteVariableChangeListener("bug",new RemoteVariableChangeListener() {
                private Vector<String> values = new Vector<String>();
                public void variableChanged(ServiceProxy serviceProxy, String variableName, String value) {
                    values.add(value);
                    if (values.size() > 10) {
                        FactoryFactory.passed(Arrays.toString(values.toArray()));
                        System.exit(0);
                    }
                }
            });
        }
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FactoryFactory.failed("Timed out while waiting for change notifications");
        System.exit(1);
    }

}
