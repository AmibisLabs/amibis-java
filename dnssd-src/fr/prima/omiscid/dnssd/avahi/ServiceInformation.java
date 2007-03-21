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

import org.freedesktop.Avahi.NTuple11;
import org.freedesktop.dbus.UInt16;
import org.freedesktop.dbus.UInt32;

public class ServiceInformation implements fr.prima.omiscid.dnssd.interf.ServiceInformation {

    private NTuple11<Integer, Integer, String, String, String, String, Integer, String, UInt16, List<List<Byte>>, UInt32> tuple;
    private Hashtable<String, byte[]> properties = new Hashtable<String, byte[]>();

    public ServiceInformation(NTuple11<Integer, Integer, String, String, String, String, Integer, String, UInt16, List<List<Byte>>, UInt32> tuple) {
        assert tuple != null;
        this.tuple = tuple;
        for (List<Byte> entry : tuple.j) {
            StringBuffer key = new StringBuffer();
             byte[] value = null;
            {
                int i = 0;
                for (; i < entry.size(); i++) {
                    byte b = entry.get(i);
                    if (b == '=') {
                        break;
                    }
                    key.appendCodePoint(b);
                }
                if (i != entry.size()) {
                    // we got an equal and so got a value
                    i++;
                    value = new byte[entry.size()-i];
                }
                for (; i < entry.size(); i++) {
                    value[value.length - (entry.size() - i)] = entry.get(i);
                }
            }
            properties.put(key.toString(), value);
        }
//        for(Entry<String,byte[]> entry : properties.entrySet()) {
//            System.out.println("properties["+entry.getKey()+"] = '"+new String(entry.getValue())+"'");
//        }
    }

    public String getFullName() {
        return fullName(tuple.c, tuple.d, tuple.e);
    }

    /*package*/ static String fullName(String name, String type, String domain) {
        return name+"."+type+"."+domain+".";
    }

    public String getHostName() {
        return tuple.f+".";
    }

    public int getPort() {
        return tuple.i.intValue();
    }

    public byte[] getProperty(String key) {
        return properties.get(key);
    }

    public Iterable<String> getPropertyKeys() {
        return properties.keySet();
    }

    public String getRegType() {
        return tuple.d+"."+tuple.e+".";
    }

    public String getStringProperty(String key) {
        return getProperty(key) == null ? null : new String(getProperty(key));
    }

    
//  method ResolveService
//
//  * in i interface 
//  * in i protocol 
//  * in s name 
//  * in s type 
//  * in s domain 
//  * in i aprotocol 
//  * in u flags 
//  a out i interface 
//  b out i protocol 
//  c out s name 
//  d out s type 
//  e out s domain 
//  f out s host 
//  g out i aprotocol 
//  h out s address 
//  i out q port 
//  j out aay txt 
//  k out u flags
}
