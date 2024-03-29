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

import java.awt.Dimension;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;

import org.freedesktop.Avahi.NTuple11;
import org.freedesktop.Avahi.Server;
import org.freedesktop.Avahi.ServiceBrowser;
import org.freedesktop.Avahi.ServiceResolver;
import org.freedesktop.Avahi.ServiceResolver.Found;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.UInt16;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;

import fr.prima.omiscid.control.interf.GlobalConstants;

public class TestDBus {
    public static void main(String[] args) throws DBusException, InterruptedException, ClassNotFoundException {
        
//        Class c = Class.forName("java.util.List");
        Object[] object = (Object[]) Array.newInstance(List.class, 19);
        object[0] = wrap(new byte[] {10});
        object[0] = wrap(new Vector());
        
//        boolean owners = true;
//        boolean users = true;
        int connection = DBusConnection.SYSTEM;

        DBusConnection conn = DBusConnection.getConnection(connection);
        
//        ServiceBrowser browser = (ServiceBrowser) ;
//        DBus dbus = (DBus) conn.getRemoteObject("org.freedesktop.DBus", "/org/freedesktop/DBus", DBus.class);
        
        final Server avahi = (Server) conn.getRemoteObject("org.freedesktop.Avahi", "/", Server.class);
//        System.err.println(avahi.GetHostName());
//        System.err.println(avahi.GetDomainName());
//        System.err.println(avahi.GetState());
//        System.err.println(avahi.GetHostNameFqdn());
//        System.err.println(avahi.GetAlternativeHostName("prometheus"));
//        System.err.println(avahi.GetAPIVersion());
//        System.err.println(avahi.GetLocalServiceCookie());
        
        System.err.println(avahi.GetVersionString());
//        ServiceBrowser browser = (ServiceBrowser) avahi.ServiceBrowserNew(
//                -1, // -1 for AVAHI_IF_UNSPEC
//                -1, // -1 for AVAHI_PROTO_UNSPEC
//                GlobalConstants.dnssdDefaultWorkingDomain,
//                "local",
//                new UInt32(0)  // 2 for AVAHI_LOOKUP_USE_MULTICAST
//                );
        conn.addSigHandler(ServiceResolver.Found.class, new DBusSigHandler<ServiceResolver.Found>() {
            public void handle(Found a) {
                System.err.println("found "+a.name+" "+a.aprotocol);
            }
        });
        conn.addSigHandler(ServiceBrowser.ItemNew.class, new DBusSigHandler<ServiceBrowser.ItemNew>() {
            public void handle(ServiceBrowser.ItemNew a) {
                System.err.println("new "+a.name+" "+a.type);
                NTuple11<Integer, Integer, String, String, String, String, Integer, String, UInt16, List<List<Byte>>, UInt32> serviceInfo = 
                    avahi.ResolveService(a._interface, a.protocol, a.name, a.type, a.domain,
                        -1, // proto unspec
                        new UInt32(0));
                System.err.println("new "+a.name+" "+a.type);
                for(List<Byte> bs : serviceInfo.j) {
                    byte[] data = new byte[bs.size()];
                    int i = 0;
                    for (Byte b : bs) {
                        data[i++] = b;
                    }
                    System.out.println("..."+new String(data));
                }
            }
        });
        conn.addSigHandler(ServiceBrowser.ItemRemove.class, new DBusSigHandler<ServiceBrowser.ItemRemove>() {
        
            public void handle(ServiceBrowser.ItemRemove arg0) {
                System.err.println("rem "+arg0.name);
            }
        });
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(100,100));
        frame.pack();
        frame.setVisible(true);
        
//        conn.callMethodAsync(avahi, "ServiceBrowserNew",
        avahi.ServiceBrowserNew(
                -1, // -1 for AVAHI_IF_UNSPEC
                -1, // -1 for AVAHI_PROTO_UNSPEC
                GlobalConstants.dnssdDefaultWorkingDomain,
                "local",
                new UInt32(0)  // 2 for AVAHI_LOOKUP_USE_MULTICAST
        );

        
//        System.err.println(avahi.GetHostName());
////        System.err.println(browser);
//        System.err.println(avahi.GetHostName());

//        Thread.sleep(3000);
//        conn.disconnect();

    }

    private static Object wrap(Object o) {
        if (o.getClass().isArray()) {
            Vector<Object> res = new Vector<Object>();
            for (int i = 0; i < Array.getLength(o); i++) {
                res.add(wrap(Array.get(o, i)));
            }
            return res;
        } else {
            return o;
        }
    }
}
