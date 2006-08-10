/*
 * Created on Mar 29, 2006
 *
 */
package fr.prima.omiscid.control.filter;

import fr.prima.omiscid.com.BipUtils;
import fr.prima.omiscid.control.OmiscidService;
import fr.prima.omiscid.control.interf.ConnectorType;
import fr.prima.omiscid.control.interf.VariableAccessType;
import fr.prima.omiscid.user.service.ServiceProxy;

/**
 * Utility class. Provides some {@link OmiscidServiceFilter} creators for
 * classical requirements.
 */
public final class OmiscidServiceFilters {

    /**
     * Tests whether a service has the good owner name
     */
    private static final class Name implements OmiscidServiceFilter {
        private String nameRegexp = null;

        public Name(String nameRegexp) {
            this.nameRegexp = nameRegexp;
        }

        public boolean isAGoodService(OmiscidService s) {
            return s.getSimplifiedName().matches(nameRegexp);
        }
    }

    /**
     * Tests whether the host of the service has a good name
     */
    private static final class Host implements OmiscidServiceFilter {
        private String hostNameRegexp = null;

        public Host(String hostnameRegexp) {
            this.hostNameRegexp = hostnameRegexp;
        }

        public boolean isAGoodService(OmiscidService s) {
            return s.getHostName().matches(hostNameRegexp);
        }
    }

    /**
     * Tests whether the owner of the service is matching a given regexp
     */
    private static final class Owner implements OmiscidServiceFilter {
        private String ownerRegexp = null;

        public Owner(String ownerRegexp) {
            this.ownerRegexp = ownerRegexp;
        }

        public boolean isAGoodService(OmiscidService s) {
            return s.getOwner().matches(ownerRegexp);
        }
    }

    /**
     * Tests whether the peerId of the service is matching a given peerId (possibly from a connector)
     */
    private static final class PeerId implements OmiscidServiceFilter {
        private int peerId;

        public PeerId(int peerId) {
            this.peerId = BipUtils.rootBIPPeerId(peerId);
        }

        public boolean isAGoodService(OmiscidService s) {
            return s.getRemotePeerId() == peerId;
        }
    }

    /**
     * Tests whether the given service has a given variable,
     * with a particular access type and a value matching the given regexp.
     */
    private static final class Variable implements OmiscidServiceFilter {
        private String variableName = null;
        private VariableAccessType variableAccessType = null;
        private String variableValueRegexp = null;

        public Variable(String variableName, VariableAccessType variableAccessType, String variableValueRegexp) {
            this.variableName = variableName;
            this.variableAccessType = variableAccessType;
            this.variableValueRegexp = variableValueRegexp;
        }

        public boolean isAGoodService(OmiscidService s) {
            return s.hasVariable(variableName, variableAccessType, variableValueRegexp);
        }
    }

    /**
     * Tests whether the given service has a given connector,
     * with a particular type.
     */
    private static final class Connector implements OmiscidServiceFilter {
        private String connectorName = null;
        private ConnectorType connectorType = null;
        public Connector(String connectorName, ConnectorType connectorType) {
            super();
            this.connectorName = connectorName;
            this.connectorType = connectorType;
        }
        public boolean isAGoodService(OmiscidService s) {
            return s.hasConnector(connectorName, connectorType);
        }
    }

    /**
     * Do not test anything, just return a constant boolean value.
     */
    private static final class Boolean implements OmiscidServiceFilter {
        private boolean value;
        public Boolean(boolean value) {
            this.value = value;
        }
        public boolean isAGoodService(OmiscidService s) {
            return value;
        }
    }

    private static final class Not implements OmiscidServiceFilter {
        private OmiscidServiceFilter baseFilter;
        public Not(OmiscidServiceFilter baseFilter) {
            this.baseFilter = baseFilter;
        }
        public boolean isAGoodService(OmiscidService s) {
            return ! baseFilter.isAGoodService(s);
        }
    }

//    /**
//     * Tests whether a value in the TXT record is ok
//     */
//    private static final class KeyValue implements OmiscidServiceFilter {
//        private String key;
//
//        private String valueRegexp;
//
//        public KeyValue(String key, String valueRegexp) {
//            this.key = key;
//            this.valueRegexp = valueRegexp;
//        }
//
//        public boolean isAGoodService(OmiscidService s) {
//            byte[] b = s.getServiceInformation().getProperty(key);
//            if (valueRegexp == null) {
//                return b == null;
//            } else {
//                return (b != null && (new String(b)).matches(valueRegexp));
//            }
//        }
//    }

    public static String baseNameRegexp(String nameRegexp) {
        return "^" + nameRegexp + "( \\(\\d+\\))?" + "$";
    }

    /**
     * Tests whether the service name (with possible trailing dnssd number
     * removed).
     *
     * @param nameRegexp
     * @return
     */
    public static OmiscidServiceFilter nameIs(String nameRegexp) {
        return new Name(baseNameRegexp(nameRegexp)); // @@@( \(\d+\))?@@@
    }

    public static OmiscidServiceFilter namePrefixIs(String prefixRegexp) {
        return new Name("^" + prefixRegexp + ".*$");
    }

    // commented out because of trailing .local.
    // public static OmiscidServiceFilter hostIs(String hostnameRegexp) {
    // return new Host("^"+hostnameRegexp+"$");
    // }
    public static OmiscidServiceFilter hostPrefixIs(String prefixRegexp) {
        return new Host("^" + prefixRegexp + ".*$");
    }

    public static OmiscidServiceFilter ownerIs(String ownerRegexp) {
        return new Owner("^" + ownerRegexp + "$");
    }

    public static OmiscidServiceFilter peerIdIs(int peerId) {
        return new PeerId(peerId);
    }

    public static OmiscidServiceFilter hasVariable(String variableName) {
        return new Variable(variableName, null, null);
    }
    public static OmiscidServiceFilter hasVariable(String variableName, VariableAccessType variableAccessType) {
        return new Variable(variableName, variableAccessType, null);
    }
    public static OmiscidServiceFilter hasVariable(String variableName, String variableValueRegexp) {
        return new Variable(variableName, null, variableValueRegexp);
    }
    public static OmiscidServiceFilter hasVariable(String variableName, VariableAccessType variableAccessType, String variableValueRegexp) {
        return new Variable(variableName, variableAccessType, variableValueRegexp);
    }
    public static OmiscidServiceFilter hasConnector(String connectorName, ConnectorType connectorType) {
        return new Connector(connectorName, connectorType);
    }
    public static OmiscidServiceFilter hasConnector(String connectorName) {
        return new Connector(connectorName, null);
    }

    public static OmiscidServiceFilter not(OmiscidServiceFilter filter) {
        return new Not(filter);
    }
    public static OmiscidServiceFilter not(ServiceProxy proxy) {
        return not(peerIdIs(proxy.getPeerId()));
    }
    public static OmiscidServiceFilter yes() {
        return new Boolean(true);
    }
    public static OmiscidServiceFilter no() {
        return new Boolean(false);
    }

//    public static OmiscidServiceFilter keyPresent(String txtRecordKey) {
//        return new KeyValue(txtRecordKey, ".*");
//    }
//
//    public static OmiscidServiceFilter keyValue(String txtRecordKey, String regexp) {
//        return new KeyValue(txtRecordKey, regexp);
//    }

    public static OmiscidServiceFilter and(OmiscidServiceFilter... filters) {
        return new OmiscidServiceFilterCascade(filters);
    }

    public static OmiscidServiceFilter or(OmiscidServiceFilter... filters) {
        return new OmiscidServiceFilterCascade(false, filters);
    }

}
