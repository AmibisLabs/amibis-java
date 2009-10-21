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

import fr.prima.omiscid.user.connector.ConnectorListener;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.connector.Message;
import java.io.IOException;

import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;


import fr.prima.omiscid.user.variable.LocalVariableListener;
import fr.prima.omiscid.user.variable.VariableAccessType;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

public class I0061_CheckAllThreadAreReleasedTest {

    @Test(expected = TestPassedPseudoException.class)
    public void doIt() throws IOException {
        final ServiceFactory factory = FactoryFactory.factory();
        final Timer t = new Timer();
        final int[] minMax = {Integer.MAX_VALUE, Integer.MIN_VALUE};
        new TimerTask() {
            Service sv1 = factory.create("test");
            Service sv2 = factory.create("test");
            {
                sv1.start();
                sv2.start();
                t.schedule(this, 2000, 2000);
            }
            int nStagnant = 0;
            @Override
            public void run() {
                try {
                    int threadCount = Thread.getAllStackTraces().size();
                    System.err.println("Thread count: " + threadCount);
                    if (threadCount == minMax[1]) {
                        // does not increase anymore
                        nStagnant++;
                        if (nStagnant > 1) {
                            FactoryFactory.passed("Thread count converged (for "+nStagnant+" steps)");
                        }
                    } else {
                        nStagnant = 0;
                    }
                    minMax[0] = Math.min(minMax[0], threadCount);
                    minMax[1] = Math.max(minMax[1], threadCount);
                    sv1.stop();
                    sv1 = sv2;
                    sv2 = factory.create("I0061Service");
                    sv2.addVariable("v", "var", "...", VariableAccessType.READ_WRITE);
                    sv2.addLocalVariableListener("v", new LocalVariableListener() {

                        public void variableChanged(Service service, String variableName, String value) {
                        }

                        public boolean isValid(Service service, String variableName, String newValue) {
                            return true;
                        }
                    });
                    sv2.addConnector("bla", "bla", ConnectorType.INOUTPUT);
                    sv2.addConnectorListener("bla", new ConnectorListener() {

                        public void messageReceived(Service service, String localConnectorName, Message message) {
                        }

                        public void disconnected(Service service, String localConnectorName, int peerId) {
                        }

                        public void connected(Service service, String localConnectorName, int peerId) {
                        }
                    });
                    sv2.start();
                } catch (IOException ex) {
                    Logger.getLogger(AudioServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        try {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 20000) {
                if (minMax[1] > 0) {
                    FactoryFactory.assertTrue("min: " + minMax[0] + " and max: " + minMax[0], minMax[1] - minMax[0] <= 5);
                }
                FactoryFactory.waitResult(100);
            }
            FactoryFactory.passed("Thread count stayed in a constant range");
            FactoryFactory.waitResult(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(I0061_CheckAllThreadAreReleasedTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            t.cancel();
        }
    }

}
