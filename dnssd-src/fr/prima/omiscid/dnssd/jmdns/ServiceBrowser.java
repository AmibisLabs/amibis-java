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

package fr.prima.omiscid.dnssd.jmdns;

import java.util.List;
import java.util.Vector;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceListener;

import fr.prima.omiscid.dnssd.interf.ServiceEvent;
import fr.prima.omiscid.dnssd.interf.ServiceEventListener;
import fr.prima.omiscid.dnssd.interf.ServiceInformation;

public class ServiceBrowser implements fr.prima.omiscid.dnssd.interf.ServiceBrowser, ServiceListener {

    private List<ServiceEventListener> listeners = new Vector<ServiceEventListener>();

    private JmDNS jmdns;

    private String registrationType;

    /* package */ServiceBrowser(JmDNS jmdns, String registrationType) {
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
        return new fr.prima.omiscid.dnssd.jmdns.ServiceInformation(event.getType(), event.getName());
    }

    private ServiceInformation fullInfoOf(javax.jmdns.ServiceEvent event) {
        return new fr.prima.omiscid.dnssd.jmdns.ServiceInformation(event.getInfo());
    }

    public void serviceAdded(javax.jmdns.ServiceEvent event) {
        // System.out.println("s added: "+event.getName());
        // jmdns.requestServiceInfo(event.getType(), event.getName(),1);
    }

    public void serviceRemoved(javax.jmdns.ServiceEvent event) {
        // System.out.println("s removed: "+event.getName());
        ServiceEvent ev = new ServiceEvent(infoOf(event), ServiceEvent.LOST);
        for (ServiceEventListener listener : listeners) {
            listener.serviceEventReceived(ev);
        }
    }

    public void serviceResolved(javax.jmdns.ServiceEvent event) {
        // System.out.println("s registered: "+event.getName());
        ServiceEvent ev = new ServiceEvent(fullInfoOf(event), ServiceEvent.FOUND);
        for (ServiceEventListener listener : listeners) {
            listener.serviceEventReceived(ev);
        }
    }

}
