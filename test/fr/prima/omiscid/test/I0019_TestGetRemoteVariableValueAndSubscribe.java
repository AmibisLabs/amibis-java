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

/*
 * Variable queries using getVariableValue causes some variable modifications
 * to be received on the client side. This is manifesting by having the client
 * side listener being notified twice with a same variable value.
 * 
 * This could probably be done with a simpler test case. It has been tried.
 * However it seems it is not: I0020 is passing at current time whereas I0019
 * is not.
 */
public class I0019_TestGetRemoteVariableValueAndSubscribe {
    
    public static void main(String[] args) {
        ServiceFactory factory = FactoryFactory.factory();
        {
            final Service server = factory.create("I0019Server");
            server.addVariable("bug", "", "an allway moving variable", VariableAccessType.READ);
            server.start();
            new Thread() {
                @Override
                public void run() {
                    int i = 0;
                    while (true) {
                        i++;
                        try {
                            Thread.sleep(10);
                            server.setVariableValue("bug", ""+i);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
        {
            Service client = factory.create("I0019Client");
            client.start();
            final ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0019Server"));
            proxy.getVariableValue("bug");
            proxy.addRemoteVariableChangeListener("bug",new RemoteVariableChangeListener() {
                private Vector<String> values = new Vector<String>();
                public void variableChanged(ServiceProxy serviceProxy, String variableName, String value) {
                    if (values.contains(value)) {
                        FactoryFactory.failed("Duplicate notification received: "+value+" is already in "+Arrays.toString(values.toArray()));
                        System.exit(1);
                    }
                    values.add(value);
                    if (values.size() > 100) {
                        FactoryFactory.passed(Arrays.toString(values.toArray()));
                        System.exit(0);
                    }
                }
            });
            for (int i = 0; i < 10; i++) {
                proxy.getVariableValue("bug");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        FactoryFactory.failed("Timed out while waiting for change notifications");
        System.exit(1);
    }

}
