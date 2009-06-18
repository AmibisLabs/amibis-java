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

import org.freedesktop.Avahi.EntryGroup;
import org.freedesktop.Avahi.Server;
import org.freedesktop.dbus.DBusConnection;

import fr.prima.omiscid.dnssd.interf.DNSSDFactory;
import fr.prima.omiscid.dnssd.interf.ServiceBrowser;
import fr.prima.omiscid.dnssd.interf.ServiceRegistration;

public class DNSSDFactoryAvahi implements DNSSDFactory {

//    private static AvahiConnection avahiConnection = null;
//    /*package*/ static synchronized AvahiConnection avahiConnection() {
//        if (avahiConnection == null) {
//            avahiConnection = new AvahiConnection(); 
//        }
//        return avahiConnection;
//    }

    static {
        // This static code block is called on class initialisation.
        // This code tests for availability of the avahi subsystem.
        // When the avahi subsystem is not available, it throws a RuntimeException.
        // This class is basically loaded by a smart factory that catches the produced exception and then tries another factory.
        try {
            System.loadLibrary("unix-java");
            DBusConnection dbus = DBusConnection.getConnection(DBusConnection.SYSTEM);
            try {
                Server avahi = (Server) dbus.getRemoteObject("org.freedesktop.Avahi", "/", Server.class);
                EntryGroup entryGroup = avahi.EntryGroupNew();
                // This is only when the entry group is created that we really know that avahi is here (or not).
                entryGroup.Free();
            } catch (Exception e) {
                throw e;
            } finally {
                //dbus.disconnect();
                // We want to disconnect from dbus but this causes problems afterwards so we don't do it.
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public ServiceBrowser createServiceBrowser(String registrationType) {
        return new fr.prima.omiscid.dnssd.avahi.ServiceBrowser(new AvahiConnection(), registrationType);
    }

    public ServiceRegistration createServiceRegistration(String serviceName, String registrationType) {
        return new fr.prima.omiscid.dnssd.avahi.ServiceRegistration(new AvahiConnection(), serviceName, registrationType);
    }

}
