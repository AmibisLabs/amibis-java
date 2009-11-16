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


/*- IGNORE -*/
import fr.prima.omiscid.dnssd.interf.DNSSDFactory;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.service.ServiceRepositoryListener;
import fr.prima.omiscid.user.service.impl.ServiceFactoryImpl;
import java.awt.Dimension;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.junit.Test;

public class NetworklessDiscovery {
    
   @Test
    public void callMain() throws Exception {
        main(new String[0]);
    }
    public static void main(String[] args) throws Exception {
        /*
        System.setProperty("java.net.preferIPv4Stack", "true");
        {
            InetAddress addr = InetAddress.getByName(null);
            InetAddress group = InetAddress.getByName("224.0.0.251");
            MulticastSocket socket = new MulticastSocket(5354);
            NetworkInterface net = NetworkInterface.getByInetAddress(addr);
            System.err.println(net);
            System.err.println(net.isUp());
            System.err.println(net.supportsMulticast());

            socket.setNetworkInterface(net);
            socket.setTimeToLive(255);
            socket.joinGroup(group);

            DatagramPacket packet = new DatagramPacket("hi there".getBytes(), "hi there".getBytes().length, group, 5354);
            socket.send(packet);

            Thread.sleep(2000);
            if (true) return;
        }
         */

        DNSSDFactory.DefaultFactory.factoryToTryFirst = "jivedns";
        DNSSDFactory.DefaultFactory.verboseMode = true;
        DNSSDFactory.DefaultFactory.verboseModeMore = true;

        new ServiceFactoryImpl().createServiceRepository().addListener(new ServiceRepositoryListener() {
            public void serviceAdded(ServiceProxy serviceProxy) {
                System.err.println("ADD " + serviceProxy.getName());
            }
            public void serviceRemoved(ServiceProxy serviceProxy) {
                System.err.println("REM " + serviceProxy.getName());
            }
        });

        try {
            JFrame f = new JFrame("hi there");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(new Dimension(400, 300));
            f.setVisible(true);
            Service s = new ServiceFactoryImpl().create("Networkless");
            s.start();
            FactoryFactory.waitResult(3000);
            s.stop();
            FactoryFactory.waitResult(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(NetworklessDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
