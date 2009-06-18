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

package fr.prima.omiscid.dnssd.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

import fr.prima.omiscid.dnssd.interf.DNSSDFactory;
import fr.prima.omiscid.dnssd.interf.ServiceBrowser;
import fr.prima.omiscid.dnssd.interf.ServiceEvent;
import fr.prima.omiscid.dnssd.interf.ServiceEventListener;
import fr.prima.omiscid.dnssd.interf.ServiceRegistration;
import fr.prima.omiscid.dnssd.interf.ServiceRegistration.ServiceNameProducer;

/**
 * Contains informations about a client connected to the {@link OmiscidDnssdServer}.
 * Each client has a socket and knows the shared object to use for registration and unregistration.
 * Each client is listening for a particular kind of dnssd services (registration type). This kind can be null.
 * 
 * Communications between the client and the {@link OmiscidDnssdServer} are done (in both way) through serialisation of {@link ServiceInformation} instances.
 *  
 * @author emonet
 *
 */
public class ClientInformation implements ServiceEventListener {

    /**
     * 
     */
    private static final long serialVersionUID = -1493964811588759239L;

    private DNSSDFactory dnssdFactory;

    //private ObjectOutputStream objectOutputStream;
    private OutputStream outputStream;

    private Hashtable<String, ServiceRegistration> serviceRegistrations = new Hashtable<String, ServiceRegistration>();

    private String registrationType;

    private ServiceBrowser serviceBrowser;

    private Socket socket;

    public ClientInformation(DNSSDFactory dnssdFactory, Socket socket, String registrationType) {
        this.dnssdFactory = dnssdFactory;
        this.registrationType = registrationType;
        this.socket = socket;
        try {
            outputStream = socket.getOutputStream();
            //objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (registrationType != null) {
            this.serviceBrowser = dnssdFactory.createServiceBrowser(registrationType);
            this.serviceBrowser.addListener(this);
            this.serviceBrowser.start();
        }
    }


    public void serviceEventReceived(ServiceEvent e) {
        try {
            if (e.isFound()) {
                serviceAdded(serviceEventToServiceInformation(e));
            } else {
                serviceRemoved(serviceEventToServiceInformation(e));
            }
        } catch (IOException ee) {
            fatalException(ee);
        }
    }

    private synchronized void send(ServiceInformation serviceInformation)
            throws IOException {
        //objectOutputStream.writeObject(serviceInformation);
        serviceInformation.crosslanguageWrite(outputStream);
    }

    public void serviceAdded(ServiceInformation serviceInformation)
            throws IOException {
        assert serviceInformation.isConnecting();
//        System.out.println(registrationType);
//        System.out.println(serviceInformation.getRegistrationType());
        if (registrationType != null && (registrationType+".local.").equals(serviceInformation.getRegistrationType())) {
            send(serviceInformation);
        }
    }

    public synchronized void serviceRemoved(
            ServiceInformation serviceInformation) throws IOException {
        assert serviceInformation.isDisconnecting();
        if (registrationType != null && (registrationType+".local.").equals(serviceInformation.getRegistrationType())) {
            send(serviceInformation);
        }
    }
    
    public void registerService(ServiceInformation serviceInformation) throws IOException {
        assert serviceInformation.isRegistering();
        ServiceRegistration serviceRegistration = dnssdFactory.createServiceRegistration(serviceInformation.getFullName(), serviceInformation.getRegistrationType());
        String hostName = serviceInformation.getHostName();
        if (hostName == null && !socket.getInetAddress().isLoopbackAddress()) {
            hostName = socket.getInetAddress().getCanonicalHostName() + ".";
        }
        serviceRegistration.setHostName(hostName);
        for (String key : serviceInformation.getPropertyKeys()) {
            serviceRegistration.addProperty(key, serviceInformation.getStringProperty(key));
        }
        boolean registered;
        if (serviceInformation.isShouldChooseAmongNamesList()) {
            final Queue<String> names = new LinkedList<String>();
            names.addAll(serviceInformation.getNamesList());
            ServiceNameProducer serviceNameProducer = new ServiceNameProducer() {
                public String getServiceName() {
                    return names.isEmpty() ? null : names.remove();
                }
            };
            registered = serviceRegistration.register(serviceInformation.getPort(), serviceNameProducer);
        } else {
            registered = serviceRegistration.register(serviceInformation.getPort());
        }
        if (registered) {
            serviceInformation.setNotifying(serviceRegistration.getRegisteredName());
            send(serviceInformation);
            serviceRegistrations.put(serviceInformation.getQualifiedName(), serviceRegistration);
            System.out.println("registered: "+serviceInformation.getQualifiedName());
        } else {
            serviceInformation.setNotifying(null);
            send(serviceInformation);
            System.out.println("!!! registration failed: "+serviceInformation.getFullName());
        }
    }

    public void unregisterService( ServiceInformation serviceInformation) throws IOException {
        assert serviceInformation.isUnregistering();
        ServiceRegistration serviceRegistration = serviceRegistrations.get(serviceInformation.getQualifiedName());
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
            serviceRegistrations.remove(serviceInformation.getQualifiedName());
            serviceInformation.setNotifying(serviceInformation.getQualifiedName());
            send(serviceInformation);
            System.out.println("unregistered: "+serviceInformation.getQualifiedName());
        } else {
            System.out.println("wrong unregister attempt: "+serviceInformation.getQualifiedName());
        }
    }

    private int counter;
    private synchronized void decrementCounter() {
        counter--;
        this.notifyAll();
    }
    private synchronized void setCounter(int n) {
        counter = n;
    }
    private synchronized boolean isCounterNull() {
        return counter==0;
    }
    
    public synchronized void unregisterAllServices() {
        if (serviceRegistrations.size() != 0) {
            Hashtable<String, ServiceRegistration> old = serviceRegistrations;
            serviceRegistrations = new Hashtable<String, ServiceRegistration>();
            setCounter(old.size());
            final ClientInformation that = this;
            for (final ServiceRegistration serviceRegistration : old.values()) {
                new Thread(new Runnable() {
                    public void run() {
                        serviceRegistration.unregister();
                        System.out.print("-");
                        System.out.flush();
                        that.decrementCounter();
                    }
                }).start();
            }
            while (!isCounterNull()) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            System.out.println(" unregistered all (" + old.size() + ")");
        }
    }
    
    public static ServiceInformation serviceEventToServiceInformation(ServiceEvent event) {
        fr.prima.omiscid.dnssd.interf.ServiceInformation serviceInformation = event.getServiceInformation();
        if (event.isFound()) {
            Hashtable<String, byte[]> properties = new Hashtable<String, byte[]>();
            for (String key : serviceInformation.getPropertyKeys()) {
                properties.put(key, serviceInformation.getProperty(key));
            }
            return new ServiceInformation( serviceInformation.getRegType(),serviceInformation.getFullName(), serviceInformation.getHostName(), serviceInformation.getPort(), properties, ServiceInformation.statusConnecting);
        } else {
            return new ServiceInformation( serviceInformation.getRegType(),serviceInformation.getFullName(), ServiceInformation.statusDisconnecting);
        }
    }

    public void fatalException(Exception e) {
//        e.printStackTrace();
        unregisterAllServices();
        if (serviceBrowser != null) {
            serviceBrowser.stop();
            serviceBrowser = null;
        }
    }



}
