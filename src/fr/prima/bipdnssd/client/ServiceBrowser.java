/*
 * Created on Feb 15, 2006
 *
 */
package fr.prima.bipdnssd.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import fr.prima.bipdnssd.interf.ServiceEvent;
import fr.prima.bipdnssd.interf.ServiceEventListener;
import fr.prima.bipdnssd.client.ServiceInformation;

public class ServiceBrowser implements fr.prima.bipdnssd.interf.ServiceBrowser {
    
    private List<ServiceEventListener> listeners = new Vector<ServiceEventListener>();
    private String registrationType;
    private String host;
    private int port;
    private Socket socket;
    
    /*package*/ ServiceBrowser(String registrationType, String host, int port) {
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
                socket = new Socket(host,port);
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
                        ObjectOutputStream outputSream = new ObjectOutputStream(socket.getOutputStream());
                        outputSream.writeObject(registrationType);
                        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                        while (true) {
                            ServiceInformation serviceInformation = new ServiceInformation((fr.prima.bipdnssd.server.ServiceInformation) inputStream.readObject());
                            assert serviceInformation.isConnecting() || serviceInformation.isDisconnecting();
                            ServiceEvent ev = new ServiceEvent( serviceInformation,
                                    serviceInformation.isConnecting() ? ServiceEvent.FOUND : ServiceEvent.LOST);
                            for (ServiceEventListener listener : listeners) {
                                listener.serviceEventReceived(ev);
                            }
                            //System.out.println(serviceInformation.getFullName()+" with status "+serviceInformation.getStatus());
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();            
    }

    public void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
