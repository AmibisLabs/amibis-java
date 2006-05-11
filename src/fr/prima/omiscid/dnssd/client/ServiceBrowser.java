/*
 * Created on Feb 15, 2006
 *
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
