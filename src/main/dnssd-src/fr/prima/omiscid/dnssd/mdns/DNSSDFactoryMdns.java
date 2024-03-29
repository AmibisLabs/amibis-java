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

package fr.prima.omiscid.dnssd.mdns;

import com.apple.dnssd.DNSSD;

import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.RegisterListener;
import fr.prima.omiscid.dnssd.interf.DNSSDFactory;
import fr.prima.omiscid.dnssd.interf.ServiceBrowser;
import fr.prima.omiscid.dnssd.interf.ServiceRegistration;

/**
 * @author emonet
 */
public class DNSSDFactoryMdns implements DNSSDFactory {

    // This static code block is called on class initialisation.
    // This code tests for availability of the mdnsresponder subsystem.
    // When the mdnsresponder subsystem is not available, it throws a RuntimeException.
    // This class is basically loaded by a smart factory that catches the produced exception and then tries another factory.
    static {
        // There is no static code that can quickly test for the present of a mdns daemon.
        // So we suppose the presence of mdns java wrapper is a desire to use mdns.
        //final long time = System.currentTimeMillis();
        try {
            // We just ensure the DNSSD class is loaded and found.
            DNSSD.getNameForIfIndex(0);
            // Testing for real daemon availability is too time consuming in case of absence (it is timeout based)
            /*
            System.err.println("start");
            DNSSDRegistration reg = DNSSD.register(DNSSD.NO_AUTO_RENAME, -1, "___TEST___", "_dummy._tcp", null, null, 0, null, new RegisterListener() {
                public void serviceRegistered(DNSSDRegistration arg0, int arg1, String arg2, String arg3, String arg4) {
                    System.err.println("reg " + (System.currentTimeMillis() - time));
                }
                public void operationFailed(DNSSDService arg0, int arg1) {
                    System.err.println("failed " + (System.currentTimeMillis() - time));
                }
            });
            reg.stop();
            System.err.println("ok "+(System.currentTimeMillis()-time));
             */
        } catch (Exception e) {
            //System.err.println("ex "+(System.currentTimeMillis()-time));
            throw new RuntimeException(e);
        }
    }

    public ServiceBrowser createServiceBrowser(String registrationType) {
        return new fr.prima.omiscid.dnssd.mdns.ServiceBrowser(registrationType);
    }

    public ServiceRegistration createServiceRegistration(String serviceName, String registrationType) {
        return new fr.prima.omiscid.dnssd.mdns.ServiceRegistration(serviceName, registrationType);
    }

    // public ServiceInformation createServiceInformation() {
    // // TODO Auto-generated method stub
    // return null;
    // }

}
