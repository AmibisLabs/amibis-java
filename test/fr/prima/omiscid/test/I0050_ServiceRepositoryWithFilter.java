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
import static fr.prima.omiscid.user.service.ServiceFilters.*;

public class I0050_ServiceRepositoryWithFilter {

    public static void main(String[] args) throws IOException, InterruptedException {
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
        /*ServiceRepositoryListener listener3 = new ServiceRepositoryListener() {
            public void serviceRemoved(ServiceProxy serviceProxy) {
                events.add("out3");
                events.add(serviceProxy.getPeerIdAsString());
            }
            public void serviceAdded(ServiceProxy serviceProxy) {
                events.add("in3");
                events.add(serviceProxy.getPeerIdAsString());
            }
        };*/
        Service service1 = factory.create("I0050Service1");
        service1.start();
        Thread.sleep(1000);
        
        repository.addListener(nameIs("I0050Service1"), listener1);
        repository.addListener(nameIs("I0050Service2"),listener2);
        repository.addListener(or(nameIs("I0050Service1"), nameIs("I0050Service2")),listener1);
        
        Service service2 = factory.create("I0050Service2");
        service2.start();
        Thread.sleep(1000);

        service1.stop();
        Thread.sleep(1000);
        service2.stop();
        Thread.sleep(1000);
        
        try {
            System.err.println(events);
            String tmpSource1;
            String tmpSource2;
            equals("in1", events.poll());
            String s1 = notNull(events.poll(), "get s1");
            equals("in1", events.poll());
            equals(s1, events.poll());
            tmpSource1 = notNull(events.poll(), "get tmpSource1");
            String s2 = notNull(events.poll(), "get s2");
            tmpSource2 = notNull(events.poll(), "get tmpSource2");
            equals(s2, events.poll());
            {
                final List<String> remain = new ArrayList<String>(Arrays.asList("in1", "in2"));
                remain.removeAll(Arrays.asList(tmpSource1, tmpSource2));
                equals("0", remain.size()+"");
            }
            equals("out1", events.poll());
            equals(s1, events.poll());
            equals("out1", events.poll());
            equals(s1, events.poll());
            {
                tmpSource1 = notNull(events.poll(), "get2 tmpSource1");
                equals(s2, events.poll());
                tmpSource2 = notNull(events.poll(), "get2 tmpSource2");
                equals(s2, events.poll());
                final List<String> remain = new ArrayList<String>(Arrays.asList("out1", "out2"));
                remain.removeAll(Arrays.asList(tmpSource1, tmpSource2));
                equals("0", remain.size()+"");
            }
            equals("0", events.size()+"");
            FactoryFactory.passed("All events checked with success");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            FactoryFactory.failed("Wrong events received: "+e.getMessage());
            System.exit(1);
        }
    }

    private static void equals(String expected, String value) {
        if (! expected.equals(value)) {
            throw new RuntimeException("expected '"+expected+"', got '"+value+"'");
        }
    }

    private static String notNull(String value, String message) {
        if (value == null) {
            throw new RuntimeException("non null expected: "+message);
        }
        return value;
    }

}
