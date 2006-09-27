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

package fr.prima.omiscid.dnssd.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import fr.prima.omiscid.dnssd.interf.ServiceEvent;
import fr.prima.omiscid.dnssd.interf.ServiceEventListener;

public class ServiceBrowser implements fr.prima.omiscid.dnssd.interf.ServiceBrowser {

    private List<ServiceEventListener> listeners = new Vector<ServiceEventListener>();

    private String registrationType;

    private String host;

    private int port;

    private Socket socket;

    private boolean shouldStop = false;

    /* package */ServiceBrowser(String registrationType, String host, int port) {
        this.registrationType = registrationType;
        this.host = host;
        this.port = port;
    }

    public void addListener(ServiceEventListener l) {
        listeners.add(l);
    }

    public void removeListener(ServiceEventListener l) {
        listeners.remove(l);
    }

    public void start() {
        try {
            socket = new Socket(host, port);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            public void run() {
                try {
                    // ObjectOutputStream outputSream = new
                    // ObjectOutputStream(socket.getOutputStream());
                    // outputSream.writeObject(registrationType);
                    OutputStream outputSream = socket.getOutputStream();
                    fr.prima.omiscid.dnssd.server.ServiceInformation.writeString(outputSream, registrationType);
                    // ObjectInputStream inputStream = new
                    // ObjectInputStream(socket.getInputStream());
                    InputStream inputStream = socket.getInputStream();
                    while (!shouldStop) {
                        // ServiceInformation serviceInformation = new
                        // ServiceInformation((fr.prima.omiscid.dnssd.server.ServiceInformation)
                        // inputStream.readObject());
                        ServiceInformation serviceInformation = new ServiceInformation(fr.prima.omiscid.dnssd.server.ServiceInformation
                                .crosslanguageReadNew(inputStream));
                        assert serviceInformation.isConnecting() || serviceInformation.isDisconnecting();
                        ServiceEvent ev = new ServiceEvent(serviceInformation, serviceInformation.isConnecting() ? ServiceEvent.FOUND : ServiceEvent.LOST);
                        for (ServiceEventListener listener : listeners) {
                            listener.serviceEventReceived(ev);
                        }
                        // System.out.println(serviceInformation.getFullName()+"
                        // with status "+serviceInformation.getStatus());
                    }
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stop() {
        shouldStop = true;
    }

}
