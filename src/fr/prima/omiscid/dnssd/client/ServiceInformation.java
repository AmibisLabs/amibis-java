/*
 * Created on Feb 15, 2006
 *
 */
package fr.prima.omiscid.dnssd.client;

/**
 * @author emonet
 */
public class ServiceInformation implements fr.prima.omiscid.dnssd.interf.ServiceInformation {

    private fr.prima.omiscid.dnssd.server.ServiceInformation delegate;

    public ServiceInformation(fr.prima.omiscid.dnssd.server.ServiceInformation delegate) {
        this.delegate = delegate;
    }

    public String getFullName() {
        return delegate.getFullName();
    }

    public String getHostName() {
        return delegate.getHostName();
    }

    public int getPort() {
        return delegate.getPort();
    }

    public byte[] getProperty(String key) {
        return delegate.getProperty(key);
    }

    public Iterable<String> getPropertyKeys() {
        return delegate.getPropertyKeys();
    }

    public String getQualifiedName() {
        return delegate.getQualifiedName();
    }

    public String getRegistrationType() {
        return delegate.getRegistrationType();
    }

    public String getRegType() {
        return delegate.getRegType();
    }

    public String getStringProperty(String key) {
        return delegate.getStringProperty(key);
    }

    public boolean isConnecting() {
        return delegate.isConnecting();
    }

    public boolean isDisconnecting() {
        return delegate.isDisconnecting();
    }

    public boolean isNotifying() {
        return delegate.isNotifying();
    }

    public int getStatus() {
        return delegate.getStatus();
    }

}
