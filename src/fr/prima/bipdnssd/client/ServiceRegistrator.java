/*
 * Created on Feb 16, 2006
 *
 */
package fr.prima.bipdnssd.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

import fr.prima.bipdnssd.server.ServiceInformation;

public class ServiceRegistrator implements Runnable  {

    private Socket notificationSocket;
    private ObjectOutputStream notificationSocketOut;
    private ObjectInputStream notificationSocketIn;
    private Hashtable<String, ServiceInformation> notifications = new Hashtable<String, ServiceInformation>();

    /*package*/ ServiceRegistrator(String serverName, int port) throws UnknownHostException, IOException {
        notificationSocket = new Socket(serverName,port);
        notificationSocketOut = new ObjectOutputStream(notificationSocket.getOutputStream());
        notificationSocketOut.writeObject(null);
        notificationSocketIn = new ObjectInputStream(notificationSocket.getInputStream());
        new Thread(this).start();
    }
    
    public void run() {
        try {
            while(true) {
                ServiceInformation serviceInformation = (ServiceInformation) notificationSocketIn.readObject();
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
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private synchronized void send(ServiceInformation serviceInformation) throws IOException {
        notificationSocketOut.writeObject(serviceInformation);
    }

    public boolean register(ServiceRegistration serviceRegistration) {
        try {
            ServiceInformation i = new ServiceInformation(serviceRegistration.getRegistrationType(),serviceRegistration.getServiceName(),InetAddress.getLocalHost().getHostName(), serviceRegistration.getPort(), serviceRegistration.getProperties(), ServiceInformation.statusRegistering);
            String id = Integer.toString(i.getPort());
            notifications.put(id, i);
            synchronized (i) {
                send(i);
                i.wait();
            }
            i = notifications.get(id);
            notifications.remove(id);
            assert i.isNotifying();
            assert i.getPort()==serviceRegistration.getPort();
            String registeredServiceName = i.getFullName();
            serviceRegistration.setRegisteredServiceName(registeredServiceName);
            return true;
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
        return false;
    }

    public void unregister(ServiceRegistration serviceRegistration) {
        try {
            ServiceInformation i = new ServiceInformation(serviceRegistration.getRegistrationType(),serviceRegistration.getRegisteredName(),InetAddress.getLocalHost().getHostName(), 0, serviceRegistration.getProperties(), ServiceInformation.statusUnregistering);
            String id = Integer.toString(i.getPort());
            notifications.put(id, i);
            synchronized (i) {
                send(i);
                i.wait();
            }
            i = notifications.get(id);
            notifications.remove(id);
            assert i.isNotifying();
            assert i.getPort()==serviceRegistration.getPort();
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
