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
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Vector;

import fr.prima.omiscid.dnssd.interf.ServiceRegistration.ServiceNameProducer;
import fr.prima.omiscid.dnssd.server.ServiceInformation;

public class ServiceRegistrator implements Runnable {

    private Socket notificationSocket;

    // private ObjectOutputStream notificationSocketOut;
    // private ObjectInputStream notificationSocketIn;
    private OutputStream notificationSocketOut;

    private InputStream notificationSocketIn;

    private Hashtable<String, ServiceInformation> notifications = new Hashtable<String, ServiceInformation>();

    /* package */ServiceRegistrator(String serverName, int port) throws UnknownHostException, IOException {
        notificationSocket = new Socket(serverName, port);
        // notificationSocketOut = new
        // ObjectOutputStream(notificationSocket.getOutputStream());
        // notificationSocketOut.writeObject(null);
        notificationSocketOut = notificationSocket.getOutputStream();
        ServiceInformation.writeNullString(notificationSocketOut);
        // notificationSocketIn = new
        // ObjectInputStream(notificationSocket.getInputStream());
        notificationSocketIn = notificationSocket.getInputStream();
        new Thread(this).start();
    }

    public void run() {
        try {
            while (true) {
                // ServiceInformation serviceInformation = (ServiceInformation)
                // notificationSocketIn.readObject();
                ServiceInformation serviceInformation = ServiceInformation.crosslanguageReadNew(notificationSocketIn);
                String id = Integer.toString(serviceInformation.getPort());
                assert notifications.containsKey(id);
                ServiceInformation i = notifications.get(id);
                notifications.put(id, serviceInformation);
                synchronized (i) {
                    i.notify();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // } catch (ClassNotFoundException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
        }
    }

    private synchronized void send(ServiceInformation serviceInformation) throws IOException {
        // notificationSocketOut.writeObject(serviceInformation);
        serviceInformation.crosslanguageWrite(notificationSocketOut);
    }

    private boolean register(ServiceRegistration serviceRegistration, ServiceInformation i) {
        try {
            String id = Integer.toString(i.getPort());
            notifications.put(id, i);
            synchronized (i) {
                send(i);
                i.wait();
            }
            i = notifications.get(id);
            notifications.remove(id);
            assert i.isNotifying();
            assert i.getPort() == serviceRegistration.getPort();
            String registeredServiceName = i.getFullName();
            if (registeredServiceName != null) {
                serviceRegistration.setRegisteredServiceName(registeredServiceName);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public boolean register(ServiceRegistration serviceRegistration) {
        try {
//            String hostName = InetAddress.getLocalHost().getHostName() + ".local.";
            String hostName = serviceRegistration.getOverrideHostName();
            return register(
                    serviceRegistration,
                    new ServiceInformation(serviceRegistration.getRegistrationType(), serviceRegistration.getServiceName(), hostName, serviceRegistration.getPort(), serviceRegistration.getProperties(), ServiceInformation.statusRegistering));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public boolean register(ServiceRegistration serviceRegistration, ServiceNameProducer serviceNameProducer) {
        try {
            Vector<String> names = new Vector<String>();
            {
                String name;
                while ((name=serviceNameProducer.getServiceName()) != null && names.size()<25) {
                    names.add(name);
                }
            }
//            String hostName = InetAddress.getLocalHost().getHostName() + ".local.";
            String hostName = serviceRegistration.getOverrideHostName();
            return register(
                    serviceRegistration,
                    new ServiceInformation(serviceRegistration.getRegistrationType(), names , hostName, serviceRegistration.getPort(), serviceRegistration.getProperties(), ServiceInformation.statusRegistering));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public void unregister(ServiceRegistration serviceRegistration) {
        try {
            ServiceInformation i = new ServiceInformation(serviceRegistration.getRegistrationType(), serviceRegistration.getRegisteredName(), InetAddress
                    .getLocalHost().getHostName(), 0, serviceRegistration.getProperties(), ServiceInformation.statusUnregistering);
            String id = Integer.toString(i.getPort());
            notifications.put(id, i);
            synchronized (i) {
                send(i);
                i.wait();
            }
            i = notifications.get(id);
            notifications.remove(id);
            assert i.isNotifying();
            assert i.getPort() == serviceRegistration.getPort();
            String registeredServiceName = i.getFullName();
            serviceRegistration.setRegisteredServiceName(registeredServiceName);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
