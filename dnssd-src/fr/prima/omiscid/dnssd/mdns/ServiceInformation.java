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

package fr.prima.omiscid.dnssd.mdns;

import java.util.Vector;

import com.apple.dnssd.TXTRecord;
import java.util.Collections;

/**
 * @author emonet initial build from Service by pesnel
 */
public class ServiceInformation implements fr.prima.omiscid.dnssd.interf.ServiceInformation {

    /** name of the service */
    public String fullName = null;

    /** host name where the service is launched */
    public String hostName = null;

    /** port where the control server listens */
    public int port;

    /** records registers with the service */
    public TXTRecord txtRecord = null;

    /** register type */
    public String registrationType = null;

    /** domain name */
    public String domain = null;

    /* package */ServiceInformation(String registrationType, String serviceName) {
        this.registrationType = registrationType;
        this.fullName = serviceName+"."+registrationType;
    }

    /* package */ServiceInformation(String registrationType, String fullName, String hostName, int port, TXTRecord txtRecord) {
        this.fullName = fullName;
        this.registrationType = registrationType;
        this.hostName = hostName;
        this.port = port;
        this.txtRecord = txtRecord;
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

    public String getRegType() {
        return registrationType;
    }

    public byte[] getProperty(String key) {
        return txtRecord.getValue(key);
    }

    public String getStringProperty(String key) {
        byte[] property = getProperty(key);
        return property != null ? new String(property) : null;
    }

    public Iterable<String> getPropertyKeys() {
        if (txtRecord == null) {
            return Collections.emptyList();
        }
        Vector<String> res = new Vector<String>();
        for (int i = 0; i < txtRecord.size(); i++) {
            res.add(txtRecord.getKey(i));
        }
        return res;
    }

}
