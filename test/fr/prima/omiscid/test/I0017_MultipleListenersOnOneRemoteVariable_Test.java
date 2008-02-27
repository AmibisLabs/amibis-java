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

public class I0017_MultipleListenersOnOneRemoteVariable_Test {
    
    public static void main(String[] args) {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0017Server");
            server.addVariable("bug1", "", "an allway moving variable", VariableAccessType.READ);
            server.addVariable("bug2", "", "an allway moving variable", VariableAccessType.READ);
            server.start();
            new Thread() {
                @Override
                public void run() {
                    int i = 0;
                    while (true) {
                        i++;
                        try {
                            Thread.sleep(10);
                            server.setVariableValue("bug1", ""+i);
                            server.setVariableValue("bug2", ""+(1000000+i));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
        final Vector<String> passed = new Vector<String>();
        {
            Service client = factory.create("I0017Client");
            client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0017Server"));
            proxy.addRemoteVariableChangeListener("bug2",new RemoteVariableChangeListener() {
                private Vector<String> values = new Vector<String>();
                public void variableChanged(ServiceProxy serviceProxy, String variableName, String value) {
                    if (values.contains(value)) {
                        FactoryFactory.failed("duplicate value received for first bug2: "+value+" isIn "+Arrays.toString(values.toArray()));
                        System.exit(1);
                    }
                    values.add(value);
                    if (!variableName.equals("bug2")) {
                        FactoryFactory.failed("modification of a non-bug2 variable received by first listener: "+variableName);
                        System.exit(1);
                    }
                    int v = Integer.valueOf(value);
                    if (v < 1000000) {
                        FactoryFactory.failed("modification of bug1 received by first listener: "+v);
                        System.exit(1);
                    }
                    if (values.size() == 100) {
                        passed.add("first");
                    }
                }
            });
            proxy.addRemoteVariableChangeListener("bug2",new RemoteVariableChangeListener() {
                private Vector<String> values = new Vector<String>();
                public void variableChanged(ServiceProxy serviceProxy, String variableName, String value) {
                    if (values.contains(value)) {
                        FactoryFactory.failed("duplicate value received for second bug2: "+value+" isIn "+Arrays.toString(values.toArray()));
                        System.exit(1);
                    }
                    values.add(value);
                    if (!variableName.equals("bug2")) {
                        FactoryFactory.failed("modification of a non-bug2 variable received by second listener: "+variableName);
                        System.exit(1);
                    }
                    int v = Integer.valueOf(value);
                    if (v < 1000000) {
                        FactoryFactory.failed("modification of bug1 received by second bug2 listener: "+v);
                        System.exit(1);
                    }
                    if (values.size() == 100) {
                        passed.add("second");
                    }
                }
            });//*/
        }
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (passed.size() == 2) {
                FactoryFactory.passed("All properly received notifications: "+Arrays.toString(passed.toArray()));
                System.exit(0);
            }
        }
        FactoryFactory.failed("Timed out while waiting for change notifications: "+Arrays.toString(passed.toArray()));
        System.exit(1);
    }

}
