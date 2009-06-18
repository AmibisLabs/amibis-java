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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import fr.prima.omiscid.control.OmiscidService;
import fr.prima.omiscid.dnssd.interf.DNSSDFactory;

/**
 * 
 * Server for dnssd access. Listens for client connections.
 * Each client is listening for a particular kind of dnssd services (registration type). This type is sent by the client just after the connection in a custom serialized version (see {@link ServiceInformation}).
 * After this initialisation of the protocol, the communications between the client and the {@link OmiscidDnssdServer} are done (in both way) through serialisation of {@link ServiceInformation} instances.
 * 
 * @author emonet
 *
 */
public class OmiscidDnssdServer implements Runnable {
    
    private static final int defaultPort = 12053;
    
    private int port;
    private ServerSocket serverSocket;
//    private final List<ClientInformation> clientInformations = new Vector<ClientInformation>();
//    private final Map<String,ServiceInformation> serviceInformations = new Hashtable<String, ServiceInformation>();
    private DNSSDFactory dnssdFactory;

    public OmiscidDnssdServer(int port, DNSSDFactory dnssdFactory) {
        System.out.println(">>> starting server on port "+port);
        this.port = port;
        this.dnssdFactory = dnssdFactory;
        try {
            this.serverSocket = new ServerSocket(this.port);
            new Thread(this).start();
            System.out.println(">>>>>> server on port "+port+" started");
        } catch (IllegalArgumentException e) {
            // port value out of range
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                prepareNewClientSocket(clientSocket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

//    public void serviceResolved(ServiceEvent event) {
//        System.out.println("service resolved " + event);
//        addNewService(event);
//    }
    
    private synchronized void prepareNewClientSocket(final Socket clientSocket) {
//        final OmiscidDnssdServer that = this;
        new Thread(new Runnable() {
            public void run() {
                ClientInformation clientInformation = null;
                try {
                    //ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    InputStream in = clientSocket.getInputStream();
                    //final String registrationType = (String) inputStream.readObject();
                    final String registrationType = ServiceInformation.readString(in);
                    clientInformation = new ClientInformation(dnssdFactory, clientSocket, registrationType);
                    System.out.println("listener for: "+registrationType);
                    if (registrationType!=null) {
                        // FIX that.jmdns.registerServiceType(registrationType);
                    }
//                    that.addNewClient(clientInformation);
                    while (true) {
                        final ClientInformation finalClientInformation = clientInformation;
                        final ServiceInformation serviceInformation = ServiceInformation.crosslanguageReadNew(in);
                        //System.out.println(serviceInformation);
                        if (serviceInformation.isRegistering()) {
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        finalClientInformation.registerService(serviceInformation);
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        } else {
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        finalClientInformation.unregisterService(serviceInformation);
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                } catch (SocketException e) {
                    assert(clientInformation!=null);
                    clientInformation.fatalException(e);
//                    that.removeClient(clientInformation);
                } catch (EOFException e) {
                    if (clientInformation!=null) {
                        clientInformation.fatalException(e);
//                        that.removeClient(clientInformation);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
//                } catch (ClassNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
                } finally {}
            }
        }).start();
    }
//    private synchronized void addNewClient(ClientInformation clientInformation) {
//        for (ServiceInformation serviceInformation : serviceInformations.values()) {
//            try {
//                clientInformation.serviceAdded(serviceInformation);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        clientInformations.add(clientInformation);
//    }
//    private synchronized void removeClient(ClientInformation clientInformation) {
//        //System.out.println("disconnected");
//        if (clientInformations.contains(clientInformation)) {
//            clientInformations.remove(clientInformation);
//            clientInformation.unregisterAllServices();
//        }
//    }
//    private synchronized void addNewService(ServiceEvent event) {
//        ServiceInformation serviceInformation = serviceEventToServiceInformation(event, true);
//        for (ClientInformation clientInformation : new Vector<ClientInformation>(clientInformations)) {
//            try {
//                clientInformation.serviceAdded(serviceInformation);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        serviceInformations.put(serviceInformation.getQualifiedName(),serviceInformation);
//        System.out.println("connected: "+serviceInformation.getQualifiedName());
//    }
//    private synchronized void removeService(ServiceEvent event) {
//        ServiceInformation serviceInformation = serviceEventToServiceInformation(event, false);
//        for (ClientInformation clientInformation : new Vector<ClientInformation>(clientInformations)) {
//            try {
//                clientInformation.serviceRemoved(serviceInformation);
//            } catch (IOException e) {
//                removeClient(clientInformation);
//            }
//        }
//        serviceInformations.remove(serviceInformation.getQualifiedName());
//        System.out.println("disconnected: "+serviceInformation.getQualifiedName());
//    }

    public static void main(String[] args) {
        // could expose the proxy
        if (args.length == 0) {
            new OmiscidDnssdServer(defaultPort, OmiscidService.dnssdFactory);
        } else {
            for (String port : args) {
                new OmiscidDnssdServer(Integer.parseInt(port), OmiscidService.dnssdFactory);
            }
        }
    }
}
