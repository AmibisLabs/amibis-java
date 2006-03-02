/*
 * Created on Feb 13, 2006
 *
 */
package fr.prima.omiscid.dnssd.jmdns;

import java.util.List;
import java.util.Vector;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceListener;

import fr.prima.omiscid.dnssd.interf.ServiceEvent;
import fr.prima.omiscid.dnssd.interf.ServiceEventListener;
import fr.prima.omiscid.dnssd.interf.ServiceInformation;

public class ServiceBrowser
implements fr.prima.omiscid.dnssd.interf.ServiceBrowser, ServiceListener {
    
    private List<ServiceEventListener> listeners = new Vector<ServiceEventListener>();
    
    private JmDNS jmdns;
    private String registrationType;
    
    /*package*/ ServiceBrowser(JmDNS jmdns, String registrationType) {
        this.jmdns = jmdns;
        this.registrationType = registrationType;
    }

    public void addListener(ServiceEventListener l) {
        listeners.add(l);
    }
    
    public void removeListener(ServiceEventListener l) {
        listeners.remove(l);
    }
    
    public void start() {
        jmdns.addServiceListener(registrationType, this);
        jmdns.list(registrationType);
    }

    public void stop() {
        jmdns.removeServiceListener(registrationType, this);
    }

    private ServiceInformation infoOf(javax.jmdns.ServiceEvent event) {
        return new fr.prima.omiscid.dnssd.jmdns.ServiceInformation(
                event.getType(),
                event.getName());
    }

    private ServiceInformation fullInfoOf(javax.jmdns.ServiceEvent event) {
        return new fr.prima.omiscid.dnssd.jmdns.ServiceInformation(event.getInfo());
//        return new fr.prima.bipcontrol.bipdnssd.jmdns.ServiceInformation(
//                event.getType(),
//                event.getName(),
//                event.getInfo().getPort(),
//                event.getInfo().getWeight(),
//                event.getInfo().getPriority(),
//                event.getInfo().getTextBytes());
    }

    public void serviceAdded(javax.jmdns.ServiceEvent event) {
        //System.out.println("s added: "+event.getName());
        //jmdns.requestServiceInfo(event.getType(), event.getName(),1);
    }

    public void serviceRemoved(javax.jmdns.ServiceEvent event) {
        //System.out.println("s removed: "+event.getName());
        ServiceEvent ev = new ServiceEvent(infoOf(event),ServiceEvent.LOST);        
        for (ServiceEventListener listener : listeners) {
            listener.serviceEventReceived(ev);
        }
    }

    public void serviceResolved(javax.jmdns.ServiceEvent event) {
        //System.out.println("s registered: "+event.getName());
        ServiceEvent ev = new ServiceEvent(fullInfoOf(event),ServiceEvent.FOUND);
        for (ServiceEventListener listener : listeners) {
            listener.serviceEventReceived(ev);
        }
    }
    
}
