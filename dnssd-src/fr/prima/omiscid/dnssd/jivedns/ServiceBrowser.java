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

package fr.prima.omiscid.dnssd.jivedns;

import java.util.List;
import java.util.Vector;

import fr.prima.omiscid.dnssd.interf.ServiceEvent;
import fr.prima.omiscid.dnssd.interf.ServiceEventListener;
import fr.prima.omiscid.dnssd.interf.ServiceInformation;
import java.util.ArrayList;
import org.jivedns.JiveDNS;
import org.jivedns.ServiceListener;

public class ServiceBrowser implements fr.prima.omiscid.dnssd.interf.ServiceBrowser, ServiceListener {

    private final List<ServiceEventListener> listeners = new Vector<ServiceEventListener>();

    private JiveDNS JiveDNS;

    private String registrationType;

    /* package */ServiceBrowser(JiveDNS JiveDNS, String registrationType) {
        this.JiveDNS = JiveDNS;
        this.registrationType = registrationType;
    }

    public void addListener(ServiceEventListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public void removeListener(ServiceEventListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    public void start() {
        JiveDNS.addServiceListener(registrationType, this);
            JiveDNS.list(registrationType);
        }

    public void stop() {
        JiveDNS.removeServiceListener(registrationType, this);
    }

    private ServiceInformation infoOf(org.jivedns.ServiceEvent event) {
        return new fr.prima.omiscid.dnssd.jivedns.ServiceInformation(event.getType(), event.getName());
    }

    private ServiceInformation fullInfoOf(org.jivedns.ServiceEvent event) {
        return new fr.prima.omiscid.dnssd.jivedns.ServiceInformation(event.getInfo());
    }

    public void serviceAdded(org.jivedns.ServiceEvent event) {
        // Required to force serviceResolved to be called again (after the first search)
        JiveDNS.requestServiceInfo(event.getType(), event.getName(), 1);
    }

    public void serviceRemoved(org.jivedns.ServiceEvent event) {
        // System.out.println("s removed: "+event.getName());
        ServiceEvent ev = new ServiceEvent(infoOf(event), ServiceEvent.LOST);
        ArrayList<ServiceEventListener> toNotify = new ArrayList();
        synchronized (listeners) {
            toNotify.addAll(listeners);
        }
        for (ServiceEventListener listener : toNotify) {
            listener.serviceEventReceived(ev);
        }
    }

    public void serviceResolved(org.jivedns.ServiceEvent event) {
        // System.out.println("s registered: "+event.getName());
        ServiceEvent ev = new ServiceEvent(fullInfoOf(event), ServiceEvent.FOUND);
        ArrayList<ServiceEventListener> toNotify = new ArrayList();
        synchronized (listeners) {
            toNotify.addAll(listeners);
        }
        for (ServiceEventListener listener : toNotify) {
            listener.serviceEventReceived(ev);
        }
    }

}
