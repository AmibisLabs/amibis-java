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

package fr.prima.omiscid.test.other;

import fr.prima.omiscid.user.connector.ConnectorListener;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.impl.ServiceFactoryImpl;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author emonet
 */
public class ThroughputAndLatencyServerTest {

    public static final String serviceName = "ThroughtputServer";
    public static final String connectorName = "output";

    
    private static boolean processBuffer = false;


    public static void main(String[] args) throws IOException {


        final int packetSize = Integer.parseInt(args[0]);
        final int packetCount = Integer.parseInt(args[1]);
        final long interval = Long.parseLong(args[2]);
        
        final ServiceFactory f = new ServiceFactoryImpl();
        final Service server = f.create(serviceName);
        server.addConnector(connectorName, "...", ConnectorType.OUTPUT);
        final Producer p = new Producer(packetSize, packetCount, interval, new Runnable() {
            public void run() {
                server.closeAllConnections();
            }
        });
        final Consumer c = new Consumer();
        server.addConnectorListener(connectorName, new ConnectorListener() {
            public void messageReceived(Service service, String localConnectorName, Message message) {
            }

            public void disconnected(Service service, String localConnectorName, int peerId) {
                System.exit(0);
            }

            public void connected(Service service, String localConnectorName, int peerId) {
                try {
                    p.startSendingMessages(c, service, localConnectorName);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ThroughputAndLatencyServerTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
        server.start();
    }

    static class Producer {
        int counter = 0;
        int size;
        int maxCount;
        long interval;
        Runnable atEnd;

        public Producer(int size, int maxCount, long interval, Runnable atEnd) {
            this.size = size;
            this.maxCount = maxCount;
            this.interval = interval;
            this.atEnd = atEnd;
        }

        private void startSendingMessages(final Consumer c, final Service service, String localConnectorName) throws InterruptedException {
            new Timer().scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    try {
                        byte[] msg = new byte[size];
                        if (counter % 2 == 0) {
                            c.give(msg);
                            service.sendToAllClients(connectorName, msg);
                        } else {
                            service.sendToAllClients(connectorName, msg);
                            c.give(msg);
                        }
                        counter++;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ThroughputAndLatencyServerTest.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (counter == maxCount) {
                        this.cancel();
                        atEnd.run();
                    }
                }
            }, 0, interval);
        }

    }

    static class Consumer {
        BlockingQueue<byte[]> q = new ArrayBlockingQueue<byte[]>(10);
        public Consumer() {
            new Thread(new Runnable() {
                public void run() {
                    while (!Thread.interrupted()) {
                        try {
                            handleMessage("direct", q.take());
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ThroughputAndLatencyServerTest.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }).start();
        }
        
        void give(byte[] msg) throws InterruptedException {
            q.put(msg);
        }

    }
    public static void handleMessage(String who, byte[] buffer) {
        if (processBuffer) {
            byte maxb = Byte.MIN_VALUE;
            for (byte b : buffer) {
                if (b > maxb) {
                    b = maxb;
                }
            }
        }
        System.out.println(buffer.length+" "+System.nanoTime());
    }

}
