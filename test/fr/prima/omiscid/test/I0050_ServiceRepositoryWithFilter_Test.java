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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.Assert;
import org.junit.Test;
import static fr.prima.omiscid.user.service.ServiceFilters.*;

public class I0050_ServiceRepositoryWithFilter_Test {

    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws IOException, InterruptedException {
        final ServiceFactory factory = FactoryFactory.factory();
        final ServiceRepository repository = factory.createServiceRepository();
        
        final Queue<String> events = new ConcurrentLinkedQueue<String>();
        
        ServiceRepositoryListener listener1 = new ServiceRepositoryListener() {
            public void serviceRemoved(ServiceProxy serviceProxy) {
                events.add("out1");
                events.add(serviceProxy.getPeerIdAsString());
            }
            public void serviceAdded(ServiceProxy serviceProxy) {
                events.add("in1");
                events.add(serviceProxy.getPeerIdAsString());
            }
        };
        ServiceRepositoryListener listener2 = new ServiceRepositoryListener() {
            public void serviceRemoved(ServiceProxy serviceProxy) {
                events.add("out2");
                events.add(serviceProxy.getPeerIdAsString());
            }
            public void serviceAdded(ServiceProxy serviceProxy) {
                events.add("in2");
                events.add(serviceProxy.getPeerIdAsString());
            }
        };
        ServiceRepositoryListener listener3 = new ServiceRepositoryListener() {
            public void serviceRemoved(ServiceProxy serviceProxy) {
                events.add("out3");
                events.add(serviceProxy.getPeerIdAsString());
            }
            public void serviceAdded(ServiceProxy serviceProxy) {
                events.add("in3");
                events.add(serviceProxy.getPeerIdAsString());
            }
        };
        Service service1 = factory.create("I0050Service1");
        service1.start();
        Thread.sleep(1500);
        
        repository.addListener(listener1, nameIs("I0050Service1"));
        repository.addListener(listener2, nameIs("I0050Service2"));
        repository.addListener(listener3, or(nameIs("I0050Service1"), nameIs("I0050Service2")));
        Thread.sleep(100);
        try {
            repository.addListener(listener1, or(nameIs("I0050Service1"), nameIs("I0050Service2")));
        } catch (RuntimeException e) {
            events.add("Exception");
        }
        
        Service service2 = factory.create("I0050Service2");
        service2.start();
        Thread.sleep(1000);

        service1.stop();
        Thread.sleep(1000);
        repository.removeListener(listener3, true);
        Thread.sleep(1000);
        service2.stop();
        Thread.sleep(1000);
        {
            System.err.println(events);
            String tmpSource1;
            String tmpSource2;
            equals("in1", events.poll());
            String s1 = notNull(events.poll(), "get s1");
            equals("in3", events.poll());
            equals(s1, events.poll());
            equals("Exception", events.poll());
            String s2;
            {
                tmpSource1 = notNull(events.poll(), "get tmpSource1");
                s2 = notNull(events.poll(), "get s2");
                tmpSource2 = notNull(events.poll(), "get tmpSource2");
                equals(s2, events.poll());
                final List<String> remain = new ArrayList<String>(Arrays.asList("in2", "in3"));
                remain.removeAll(Arrays.asList(tmpSource1, tmpSource2));
                equals("0", remain.size()+"");
            }
            {
                tmpSource1 = notNull(events.poll(), "get2 tmpSource1");
                equals(s1, events.poll());
                tmpSource2 = notNull(events.poll(), "get2 tmpSource2");
                equals(s1, events.poll());
                final List<String> remain = new ArrayList<String>(Arrays.asList("out1", "out3"));
                remain.removeAll(Arrays.asList(tmpSource1, tmpSource2));
                equals("0", remain.size()+"");
            }
            equals("out3", events.poll());
            equals(s2, events.poll());
            equals("out2", events.poll());
            equals(s2, events.poll());
            equals("0", events.size()+"");
            FactoryFactory.passed("All events checked with success");
        }
    }

    private static void equals(String expected, String value) {
        Assert.assertEquals(expected, value);
    }

    private static String notNull(String value, String message) {
        Assert.assertNotNull("non null expected: "+message, value);
        return value;
    }

}
