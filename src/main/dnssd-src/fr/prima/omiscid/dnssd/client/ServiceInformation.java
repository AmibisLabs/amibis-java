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
