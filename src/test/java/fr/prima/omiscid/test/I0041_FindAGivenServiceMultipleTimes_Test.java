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



import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.junit.Test;
import static org.junit.Assert.*;

public class I0041_FindAGivenServiceMultipleTimes_Test {

    /**
     * This test is here because it seems that, when using jmdns, the 
     * second (or third) findService (by peerId) fails.
     * However it seems to be ok, even with jmdns in this case ...
     */
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws InterruptedException {
        ServiceFactory factory = FactoryFactory.factory();
        int peerId;
        {
            final Service server = factory.create("I0041Server");
            server.start();
            peerId = server.getPeerId();
        }
        {
            Service client = factory.create("I0041Client");
            //client.start();
            ServiceProxy proxy = null;
            long time = System.currentTimeMillis();
            long current;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10 * 300 + 1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(I0041_FindAGivenServiceMultipleTimes_Test.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    FactoryFactory.failed("Timeout!!!");
                }
            });
            for (int i = 0; i < 10; i++) {
                proxy = client.findService(ServiceFilters.peerIdIs(peerId), 1000);
                if (proxy == null) {
                    FactoryFactory.failed("A given search timed out: "+i);
                }
                System.out.println((current = System.currentTimeMillis()) - time);
                time = current;
                if (i == 3) {
                    Thread.sleep(1000);
                }
            }
            FactoryFactory.passed("All findService passed in time");
        }
    }

}
