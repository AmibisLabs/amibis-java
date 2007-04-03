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

package fr.prima.omiscid.dnssd.avahi;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.freedesktop.Avahi.EntryGroup;
import org.freedesktop.Avahi.NTuple11;
import org.freedesktop.Avahi.Server;
import org.freedesktop.Avahi.ServiceBrowser;
import org.freedesktop.Avahi.EntryGroup.StateChanged;
import org.freedesktop.Avahi.ServiceBrowser.ItemRemove;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.UInt16;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;

import fr.prima.omiscid.dnssd.interf.ServiceInformation;

/*package*/ class AvahiConnection {

    public static interface AvahiBrowserListener {
        void serviceFound(ServiceInformation i);
        void serviceLost(ServiceInformation i);
    }
    
    private Map<String, ServiceInformation> services = new Hashtable<String, ServiceInformation>();
    private AvahiBrowserListener avahiBrowserListener;
    private DBusConnection dbus;
    private Server avahi;
    private EntryGroup entryGroup;
    private boolean registrationDone = false;
    private String registeredName = null;
    
    public AvahiConnection() {
        try {
            dbus = DBusConnection.getConnection(DBusConnection.SYSTEM);
            avahi = (Server) dbus.getRemoteObject("org.freedesktop.Avahi", "/", Server.class);
        } catch (DBusException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public synchronized void startBrowse(String registrationType, final AvahiBrowserListener avahiBrowserListener) {
        assert this.avahiBrowserListener==null;
        this.avahiBrowserListener = avahiBrowserListener;
        try {
            dbus.addSigHandler(ServiceBrowser.ItemNew.class, new DBusSigHandler<ServiceBrowser.ItemNew>() {
                public void handle(ServiceBrowser.ItemNew a) {
                    try {
                        NTuple11<Integer, Integer, String, String, String, String, Integer, String, UInt16, List<List<Byte>>, UInt32> serviceInfo = 
                            avahi.ResolveService(a._interface, a.protocol, a.name, a.type, a.domain,
                                    -1, // proto unspec
                                    new UInt32(0));
                        notifyServiceFound(serviceInfo);
                    } catch (DBusExecutionException e) {
                        // This could be a timeout probably meaning the service is not here anymore.
                        // In other words, if we cannot resolve a service, we do not notify its presence.
                    }
                }
            });
            dbus.addSigHandler(ServiceBrowser.ItemRemove.class, new DBusSigHandler<ServiceBrowser.ItemRemove>() {
                public void handle(ServiceBrowser.ItemRemove a) {
                    notifyServiceLost(a);
                }
            });
            avahi.ServiceBrowserNew(
                    -1, // -1 for AVAHI_IF_UNSPEC
                    -1, // -1 for AVAHI_PROTO_UNSPEC
                    registrationType,
                    "local",
                    new UInt32(0)
            );
        } catch (DBusException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        dbus.disconnect();
        super.finalize();
    }
    
    public synchronized void stopBrowse(final AvahiBrowserListener avahiBrowserListener) {
        assert this.avahiBrowserListener==avahiBrowserListener;
        this.avahiBrowserListener = null;
        dbus.disconnect();
    }
    
    private synchronized void notifyServiceFound(NTuple11<Integer, Integer, String, String, String, String, Integer, String, UInt16, List<List<Byte>>, UInt32> serviceInfo) {
        ServiceInformation serviceInformation = new fr.prima.omiscid.dnssd.avahi.ServiceInformation(serviceInfo); 
//        System.err.println("new "+serviceInformation.getFullName());
        assert !services.containsKey(serviceInformation.getFullName()) : serviceInformation.getFullName();
        services.put(serviceInformation.getFullName(), serviceInformation);
        if (avahiBrowserListener!=null) {
            avahiBrowserListener.serviceFound(serviceInformation);
        }
    }
    private synchronized void notifyServiceLost(ItemRemove a) {
        String fullName = fr.prima.omiscid.dnssd.avahi.ServiceInformation.fullName(a.name, a.type, a.domain);
//        System.err.println("rem "+fullName);
        ServiceInformation serviceInformation = services.remove(fullName);
        assert serviceInformation != null;
        if (avahiBrowserListener!=null) {
            avahiBrowserListener.serviceLost(serviceInformation);
        }
//        assert services.containsKey(a.name+a.domain);
    }

    public synchronized String register(final ServiceRegistration registration) {
        assert entryGroup == null;
        registeredName = null;
        registrationDone = false;
        try {
            dbus.addSigHandler(EntryGroup.StateChanged.class, new DBusSigHandler<EntryGroup.StateChanged>() {
                public void handle(StateChanged a) {
                    if (a.state == 1) {
                        // AVAHI_ENTRY_GROUP_ESTABLISHED
                        registrationDone(registration.getName());
                    } else {
                        registrationDone(null);
                    }
                }
            });
            entryGroup = avahi.EntryGroupNew();
            List<List<Byte>> txt = new Vector<List<Byte>>();
            for(Entry<String,byte[]> entry : registration.getProperties().entrySet()) {
                Vector<Byte> list = new Vector<Byte>();
                for (byte b : entry.getKey().getBytes()) {
                    list.add(b);
                }
                if (entry.getValue() != null) {
                    list.add("=".getBytes()[0]);
                    for (byte b : entry.getValue()) {
                        list.add(b);
                    }
                }
                txt.add(list);
            }
            String hostName = registration.getHostName();
            entryGroup.AddService(
                    -1,
                    -1, 
                    new UInt32(0),
                    registration.getName(),
                    registration.getRegistrationType(), 
                    "local",
                    registration.getHostName() == null ? "" : hostName,
                    new UInt16(registration.getPort()),
                    txt);
            entryGroup.Commit();
            while (!this.registrationDone) {
                this.wait();
            }
        } catch (DBusException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DBusExecutionException e) {
            // This happens when the txt record is invalid (too big)
            // Should log?
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (registeredName == null) {
            entryGroup.Free();
            entryGroup = null;
        }
        return registeredName;
    }

    private synchronized void registrationDone(String registeredName) {
        this.registeredName = registeredName;
        this.registrationDone = true;
        this.notify();
    }

    public synchronized void unregister(ServiceRegistration registration) {
        assert entryGroup != null;
        entryGroup.Free();
        entryGroup = null;
    }
    
}

// The running user must be in one of the following groups: dialout cdrom floppy plugdev
// It is usually the case but not with nis (yellow pages)

//@NoReply on the used Server#ServiceBrowserNew
// EntryGroup return type for Server#EntryGroupNew
