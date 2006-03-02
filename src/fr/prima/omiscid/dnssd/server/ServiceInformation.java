/*
 * Created on Feb 15, 2006
 *
 */
package fr.prima.omiscid.dnssd.server;

import java.io.EOFException;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * This class is a pure copy of the corresponding class in the BipDnssdServer project.
 * 
 * @author emonet
 * 
 */
public class ServiceInformation implements Externalizable {

    private static final long serialVersionUID = -7947660767854019970L;
    
    private String registrationType = null;
    private String fullName = null;
    private String hostName = null;
    private int port;
    private Hashtable<String, byte[]> properties = new Hashtable<String, byte[]>();
    private int status;
    public final static int statusConnecting = 1000;
    public final static int statusDisconnecting = 1001;
    public final static int statusRegistering = 2000; 
    public final static int statusUnregistering = 2001;
    public final static int statusNotifyingRegistered = 1002;
    public final static String crosslanguageCharset ="UTF-8";
    
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

    private static int readInt(InputStream in) throws IOException {
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
        int i = Integer.parseInt(new String(bytes,0,size,crosslanguageCharset));
        return i;
    }


    
    public static void writeString(OutputStream out, String s) throws IOException {
        writeByteArray(out, s.getBytes(crosslanguageCharset));
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
        writeString(out,hostName);
        writeInt(out,port);
        writeInt(out,status);
        writeInt(out,properties.size());
        for (String key : properties.keySet()) {
            writeString(out, key);
            writeByteArray(out, properties.get(key));
        }
        out.flush();
    }
    
    public void crosslanguageRead(InputStream in) throws IOException {
        registrationType = readString(in);
        fullName = readString(in);
        hostName = readString(in);
        port = readInt(in);
        status = readInt(in);
        int size = readInt(in);
        for (int i = 0; i < size; i++) {
            properties.put(readString(in), readByteArray(in));
        }
    }


    public void writeExternal(ObjectOutput out) throws IOException {
        if(true) throw new RuntimeException("writeExternal should not be called: use crosslanguage protocol instead");
        out.writeObject(registrationType);
        out.writeObject(fullName);
        out.writeObject(hostName);
        out.writeInt(port);
        out.writeObject(properties);
        out.writeInt(status);
    }
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        if(true) throw new RuntimeException("readExternal should not be called: use crosslanguage protocol instead");
        registrationType = (String) in.readObject();
        fullName = (String) in.readObject();
        hostName = (String) in.readObject();
        port = in.readInt();
        properties = (Hashtable<String, byte[]>) in.readObject();
        status = in.readInt();
    }
    
    public void setNotifying(String registeredName) {
        assert status == statusRegistering || status == statusUnregistering;
        status = statusNotifyingRegistered;
        fullName = registeredName;
    }
    public String getQualifiedName() {
        return registrationType+fullName;        
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
        this.fullName = fullName;
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

}
