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

package fr.prima.omiscid.user.service;

import java.util.Vector;

import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.util.Utility;
import fr.prima.omiscid.user.variable.VariableAccessType;


public final class ServiceFilters {

    private static final class ServiceFilterCascade implements ServiceFilter {
        private final Vector<ServiceFilter> filters = new Vector<ServiceFilter>();
        private boolean isAnd = true;

        public ServiceFilterCascade(ServiceFilter... filters) {
            for (ServiceFilter filter : filters) {
                this.filters.add(filter);
            }
        }
        public ServiceFilterCascade(boolean isAnd, ServiceFilter... filters) {
            this(filters);
            this.isAnd = isAnd;
        }
        public synchronized boolean acceptService(ServiceProxy serviceProxy) {
            for (ServiceFilter filter : filters) {
                if (filter.acceptService(serviceProxy) != isAnd) {
                    return !isAnd;
                }
            }
            return isAnd;
        }
    }

    private static final class Name implements ServiceFilter {
        private String nameRegexp = null;

        public Name(String nameRegexp) {
            this.nameRegexp = nameRegexp;
        }

        public boolean acceptService(ServiceProxy serviceProxy) {
            return serviceProxy.getName().matches(nameRegexp);
        }
    }

    private static final class Host implements ServiceFilter {
        private String hostNameRegexp = null;

        public Host(String hostnameRegexp) {
            this.hostNameRegexp = hostnameRegexp;
        }

        public boolean acceptService(ServiceProxy s) {
            return s.getHostName().matches(hostNameRegexp);
        }
    }

    private static final class Owner implements ServiceFilter {
        private String ownerRegexp = null;

        public Owner(String ownerRegexp) {
            this.ownerRegexp = ownerRegexp;
        }

        public boolean acceptService(ServiceProxy s) {
            return s.getVariableValue("owner").matches(ownerRegexp);
        }
    }

    private static final class PeerId implements ServiceFilter {
        private int peerId;

        public PeerId(int peerId) {
            this.peerId = Utility.PeerId.rootPeerIdFromConnectorPeerId(peerId);
        }

        public boolean acceptService(ServiceProxy s) {
            return s.getPeerId() == peerId;
        }
    }

    private static final class Variable implements ServiceFilter {
        private String variableName = null;
        private VariableAccessType variableAccessType = null;
        private String variableValueRegexp = null;

        public Variable(String variableName, VariableAccessType variableAccessType, String variableValueRegexp) {
            this.variableName = variableName;
            this.variableAccessType = variableAccessType;
            this.variableValueRegexp = variableValueRegexp;
        }

        public boolean acceptService(ServiceProxy s) {
            return s.getVariables().contains(variableName)
            &&
            (
                    variableAccessType == null
                    ||
                    variableAccessType == s.getVariableAccessType(variableName)
            )
            &&
            (
                    variableValueRegexp == null
                    ||
                    s.getVariableValue(variableName).matches(variableValueRegexp)
            );
        }
    }

    private static final class Connector implements ServiceFilter {
        private String connectorName = null;
        private ConnectorType connectorType = null;
        public Connector(String connectorName, ConnectorType connectorType) {
            super();
            this.connectorName = connectorName;
            this.connectorType = connectorType;
        }
        public boolean acceptService(ServiceProxy s) {
            if (connectorType == null) {
                return s.getInputConnectors().contains(connectorName)
                || s.getOutputConnectors().contains(connectorName)
                || s.getInputOutputConnectors().contains(connectorName);
            } else {
                switch (connectorType) {
                case INPUT: return s.getInputConnectors().contains(connectorName);
                case OUTPUT: return s.getOutputConnectors().contains(connectorName);
                case INOUTPUT: return s.getInputOutputConnectors().contains(connectorName);
                default:
                    System.err.println("Unhandled connector type "+connectorType+" in service filters");
                    return false;
                }
            }
        }
    }

    /**
     * Do not test anything, just return a constant boolean value.
     */
    private static final class Boolean implements ServiceFilter {
        private boolean value;
        public Boolean(boolean value) {
            this.value = value;
        }
        public boolean acceptService(ServiceProxy s) {
            return value;
        }

    }

    private static final class Not implements ServiceFilter {
        private ServiceFilter serviceFilter;
        public Not(ServiceFilter serviceFilter) {
            this.serviceFilter = serviceFilter;
        }
        public boolean acceptService(ServiceProxy s) {
            return !serviceFilter.acceptService(s);
        }
    }





    public static String baseNameRegexp(String nameRegexp) {
        return "^" + nameRegexp + "( \\(\\d+\\))?" + "$";
    }

    /**
     * Tests whether the service name (with possible trailing dnssd number
     * removed).
     *
     * @param nameRegexp
     */
    public static ServiceFilter nameIs(String nameRegexp) {
        return new Name(baseNameRegexp(nameRegexp)); // @@@( \(\d+\))?@@@
    }

    public static ServiceFilter namePrefixIs(String prefixRegexp) {
        return new Name("^" + prefixRegexp + ".*$");
    }

    // commented out because of trailing .local.
    // public static ServiceFilter hostIs(String hostnameRegexp) {
    // return new Host("^"+hostnameRegexp+"$");
    // }
    public static ServiceFilter hostPrefixIs(String prefixRegexp) {
        return new Host("^" + prefixRegexp + ".*$");
    }

    public static ServiceFilter ownerIs(String ownerRegexp) {
        return new Owner("^" + ownerRegexp + "$");
    }

    public static ServiceFilter peerIdIs(int peerId) {
        return new PeerId(peerId);
    }

    public static ServiceFilter hasVariable(String variableName) {
        return new Variable(variableName, null, null);
    }
    public static ServiceFilter hasVariable(String variableName, VariableAccessType variableAccessType) {
        return new Variable(variableName, variableAccessType, null);
    }
    public static ServiceFilter hasVariable(String variableName, String variableValueRegexp) {
        return new Variable(variableName, null, variableValueRegexp);
    }
    public static ServiceFilter hasVariable(String variableName, VariableAccessType variableAccessType, String variableValueRegexp) {
        return new Variable(variableName, variableAccessType, variableValueRegexp);
    }
    public static ServiceFilter hasConnector(String connectorName, ConnectorType connectorType) {
        return new Connector(connectorName, connectorType);
    }
    public static ServiceFilter hasConnector(String connectorName) {
        return new Connector(connectorName, null);
    }

    public static ServiceFilter not(ServiceProxy proxy) {
        return not(peerIdIs(proxy.getPeerId()));
    }

    public static ServiceFilter not(ServiceFilter serviceFilter) {
        return new Not(serviceFilter);
    }
    public static ServiceFilter yes() {
        return new Boolean(true);
    }
    public static ServiceFilter no() {
        return new Boolean(false);
    }

//    public static ServiceFilter keyPresent(String txtRecordKey) {
//        return new KeyValue(txtRecordKey, ".*");
//    }
//
//    public static ServiceFilter keyValue(String txtRecordKey, String regexp) {
//        return new KeyValue(txtRecordKey, regexp);
//    }

    public static ServiceFilter and(ServiceFilter... filters) {
        return new ServiceFilterCascade(filters);
    }

    public static ServiceFilter or(ServiceFilter... filters) {
        return new ServiceFilterCascade(false, filters);
    }
}
