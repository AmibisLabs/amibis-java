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

package fr.prima.omiscid.user.service.impl;

import fr.prima.omiscid.dnssd.interf.ServiceInformation;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.service.ServiceRepository;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public final class Digger {

    private static ServiceInformation info(ServiceProxy serviceProxy) {
        return ((ServiceProxyImpl) serviceProxy).omiscidService.getServiceInformation();
    }

    private Digger() {
    }

    public static int getTcpPort(ServiceProxy serviceProxy, String connectorName) {
        return ((ServiceProxyImpl)serviceProxy).omiscidService.findConnector(connectorName).getTcpPort();
    }

    public static String getTXTRecordHostName(ServiceProxy serviceProxy) {
        return info(serviceProxy).getHostName();
    }
    public static String getTXTRecordFullName(ServiceProxy serviceProxy) {
        return info(serviceProxy).getFullName();
    }
    public static int getTXTRecordPort(ServiceProxy serviceProxy) {
        return info(serviceProxy).getPort();
    }

    public static Map<String, byte[]> getTXTRecordProperties(ServiceProxy serviceProxy) {
        ServiceInformation info = info(serviceProxy);
        SortedMap<String, byte[]> res = new TreeMap();
        for (String k : info.getPropertyKeys()) {
            res.put(k, info.getProperty(k));
        }
        return res;
    }

    public static ServiceRepository createServiceRepository(ServiceFactoryImpl factory, Service service, String regType) {
        return factory.createServiceRepository(service, regType);
    }

}
