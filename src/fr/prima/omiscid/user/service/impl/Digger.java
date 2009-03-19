
package fr.prima.omiscid.user.service.impl;

import fr.prima.omiscid.dnssd.interf.ServiceInformation;
import fr.prima.omiscid.user.service.ServiceProxy;
import java.util.HashMap;
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

}
