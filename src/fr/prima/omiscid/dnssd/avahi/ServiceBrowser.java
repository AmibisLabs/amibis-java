package fr.prima.omiscid.dnssd.avahi;

import java.util.List;
import java.util.Vector;

import fr.prima.omiscid.dnssd.interf.ServiceEvent;
import fr.prima.omiscid.dnssd.interf.ServiceEventListener;
import fr.prima.omiscid.dnssd.interf.ServiceInformation;

/*package*/ class ServiceBrowser implements fr.prima.omiscid.dnssd.interf.ServiceBrowser, AvahiConnection.AvahiBrowserListener {

    private List<ServiceEventListener> listeners = new Vector<ServiceEventListener>();

    private String registrationType;
    
    private AvahiConnection avahiConnection;

    /* package */ServiceBrowser(AvahiConnection avahiConnection, String registrationType) {
        this.registrationType = registrationType;
        this.avahiConnection =avahiConnection;
    }

    public synchronized void addListener(ServiceEventListener l) {
        listeners.add(l);
    }

    public synchronized void removeListener(ServiceEventListener l) {
        listeners.remove(l);
    }

    public void start() {
        avahiConnection.startBrowse(registrationType, this);
    }
    
    public void stop() {
        avahiConnection.stopBrowse(this);
    }

    public void notify(ServiceEvent e) {
        for(ServiceEventListener listener : listeners) {
            listener.serviceEventReceived(e);
        }
    }

    public void serviceFound(ServiceInformation i) {
        notify(new ServiceEvent(i, ServiceEvent.FOUND));
    }

    public void serviceLost(ServiceInformation i) {
        notify(new ServiceEvent(i, ServiceEvent.LOST));
    }
}
