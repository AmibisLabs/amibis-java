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

package fr.prima.omiscid.dnssd.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * Contains informations about a dnssd service.
 * This can be used to represent various things: a service, a service registration/unregistration request, a service connection/disconnection notification, ... 
 * 
 * Instance of this class are serialized to do the communication between the server and its clients.
 * The serialization is based on:
 *  - the serialization of integer, in US locale ASCII, alone in one line
 *  - the serialization of byte array, that is the serialization of its size follow by the raw bytes
 *  - the serialization of strings, that is the serialization of the byte array representing the string in UTF-8 encoding
 *  - the serialization of the null string is the serialization of the integer -1
 * 
 * An instance is serialized as follow (pseudo code):
 *  - serialize registrationType as string
 *  - serialize fullName as string
 *  - serialize hostName as string
 *  - serialize port as integer
 *  - serialize status as integer (see below)
 *  - serialize properties size (N) as integer
 *  - foreach N properties
 *    - serialize property name as string
 *    - serialize property value as byte array
 *    
 * The status describe the interpretation to be given to the service information and can take the following values:
 *  - 1000 to notify service connection
 *  - 1001 to notify service disconnection
 *  - 2000 to request service registration
 *  - 2001 to request service unregistration
 *  - 1002 to notify the registration/unregistration request has been done
 *  
 * Statuses in 1??? are from server to client, the ones in 2??? are in the reverse way.
 * The 1002 notification exists because the client ask for registering a service but:
 *  - the client may not be listening to the registration type of his request
 *  - the requested service name can be decorated by dnssd to ensure uniqueness (the service information in the 1002 response contains the decorated name)  
 * 
 * @author emonet
 *
 */
public class ServiceInformation {

    private static final long serialVersionUID = -7947660767854019970L;
    
    private String registrationType = null;
    private String fullName = null;
    private boolean shouldChooseAmongNamesList = false;
    private Vector<String> namesList = new Vector<String>();
    private String hostName = null;
    private int port;
    private Hashtable<String, byte[]> properties = new Hashtable<String, byte[]>();
    private int status;
    public final static int statusConnecting = 1000;
    public final static int statusDisconnecting = 1001;
    public final static int statusRegistering = 2000; 
    public final static int statusUnregistering = 2001;
    public final static int statusNotifyingRegistered = 1002;
    public final static String crosslanguageCharset = "UTF-8";
    
    public ServiceInformation() {
    }


    public static ServiceInformation crosslanguageReadNew(InputStream in) throws IOException {
        ServiceInformation serviceInformation = new ServiceInformation();
        serviceInformation.crosslanguageRead(in);
        return serviceInformation;
    }

    private static void writeInt(OutputStream out, int i) throws IOException {
        String value = Integer.toString(i) + "\n";
        out.write(value.getBytes(crosslanguageCharset));
    }
    
    private static void writeBoolean(OutputStream out, boolean b) throws IOException {
        String value = (b?"true":"false") + "\n";
        out.write(value.getBytes(crosslanguageCharset));
    }

    private static void writeByteArray(OutputStream out, byte[] bytes) throws IOException {
        writeInt(out, bytes.length);
        out.write(bytes);
    }

    private static byte[] readByteArray(InputStream in) throws IOException {
        int size = readInt(in);
        if (size == -1) {
            return null;
        } else {
            byte[] bytes = new byte[size];
            for (int i = 0; i < bytes.length; i++) {
                int read = in.read();
                if (read == -1) {
                    throw new EOFException("in readByteArray");
                }
                bytes[i] = (byte)read;
            }
            return bytes;
        }
    }

    private static boolean readBoolean(InputStream in) throws IOException {
        String line = readLine(in);
        if ("true".equals(line)) {
            return true;
        } else if ("false".equals(line)) {
            return false;
        } else {
            System.err.println("neither true nor false when reading boolean in service information");
            return false;
        }
    }

    private static int readInt(InputStream in) throws IOException {
        String line = readLine(in);
        int i = Integer.parseInt(line);
        return i;
    }

    private static String readLine(InputStream in) throws IOException, EOFException, UnsupportedEncodingException {
        byte[] bytes = new byte[1024];
        int size;
        for (size = 0; ; size++) {
            int read = in.read();
            if (read == -1) {
                throw new EOFException("EOF in readByteArray");
            }
            if ((byte)read == (byte)'\n') {
                break;
            }
            bytes[size] = (byte)read;
        }
        return new String(bytes,0,size,crosslanguageCharset);
    }

    
    public static void writeString(OutputStream out, String s) throws IOException {
        if (s==null) {
            writeNullString(out);
        } else {
            writeByteArray(out, s.getBytes(crosslanguageCharset));
        }
    }
    
    public static void writeNullString(OutputStream out) throws IOException {
        writeInt(out, -1);
    }

    public static String readString(InputStream in) throws IOException {
        byte[] bs = readByteArray(in);
        return bs==null ? null : new String(bs);
    }

    
    public void crosslanguageWrite(OutputStream out) throws IOException {
        writeString(out,registrationType);
        writeString(out,fullName);
        writeBoolean(out, shouldChooseAmongNamesList);
        writeInt(out, namesList.size());
        for (String name : namesList) {
            writeString(out, name);
        }
        writeString(out,hostName);
        writeInt(out,port);
        writeInt(out,status);
        if (properties!=null) {
            writeInt(out,properties.size());
            for (String key : properties.keySet()) {
                writeString(out, key);
                writeByteArray(out, properties.get(key));
            }
        } else {
            writeInt(out, 0);
        }
        out.flush();
    }
    
    public void crosslanguageRead(InputStream in) throws IOException {
        registrationType = readString(in);
        fullName = readString(in);
        shouldChooseAmongNamesList = readBoolean(in);
        {
            int size = readInt(in);
            namesList.clear();
            for (int i = 0; i < size; i++) {
                namesList.add(readString(in));
            }
        }
        hostName = readString(in);
        port = readInt(in);
        status = readInt(in);
        {
            int size = readInt(in);
            for (int i = 0; i < size; i++) {
                properties.put(readString(in), readByteArray(in));
            }
        }
    }


    public void setNotifying(String registeredName) {
        assert status == statusRegistering || status == statusUnregistering;
        status = statusNotifyingRegistered;
        fullName = registeredName;
    }
    public String getQualifiedName() {
        return fullName;        
    }
    public String getFullName() {
        return fullName;
    }
    public String getHostName() {
        return hostName;
    }
    public int getPort() {
        return port;
    }
    public String getRegistrationType() {
        return registrationType;
    }
    public String getRegType() {
        return registrationType;
    }
    public byte[] getProperty(String key) {
        return properties.get(key);
    }
    public String getStringProperty(String key) {
        return getProperty(key)==null ? null : new String(getProperty(key));
    }
    public Iterable<String> getPropertyKeys() {
        return properties.keySet();
    }
    public ServiceInformation(String registrationType, String fullName, String hostName, int port, Hashtable<String, byte[]> properties, int status) {
        this.registrationType = registrationType;
        this.shouldChooseAmongNamesList = false;
        this.fullName = fullName;
        this.hostName = hostName;
        this.port = port;
        this.properties = properties;
        this.status = status;
    }
    public ServiceInformation(String registrationType, List<String> names, String hostName, int port, Hashtable<String, byte[]> properties, int status) {
        this.registrationType = registrationType;
        this.shouldChooseAmongNamesList = true;
        this.namesList.addAll(names);
        this.hostName = hostName;
        this.port = port;
        this.properties = properties;
        this.status = status;
    }
    public ServiceInformation(String registrationType, String fullName, int status) {
        this.registrationType = registrationType;
        this.fullName = fullName;
        this.hostName = null;
        this.port = 0;
        this.properties = null;
        this.status = status;
    }

    public boolean isRegistering() {
        return status == statusRegistering;
    }

    public boolean isUnregistering() {
        return status == statusUnregistering;
    }

    public boolean isConnecting() {
        return status == statusConnecting;
    }

    public boolean isDisconnecting() {
        return status == statusDisconnecting;
    }

    public boolean isNotifying() {
        return status == statusNotifyingRegistered;
    }

    public int getStatus() {
        return status;
    }


    public boolean isShouldChooseAmongNamesList() {
        return shouldChooseAmongNamesList;
    }


    public List<String> getNamesList() {
        return Collections.unmodifiableList(namesList);
    }

}
