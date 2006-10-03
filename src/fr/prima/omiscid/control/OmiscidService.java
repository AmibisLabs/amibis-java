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

package fr.prima.omiscid.control;

import java.io.IOException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import fr.prima.omiscid.com.BipUtils;
import fr.prima.omiscid.com.TcpClient;
import fr.prima.omiscid.com.interf.BipMessageListener;
import fr.prima.omiscid.control.interf.GlobalConstants;
import fr.prima.omiscid.control.interf.VariableChangeListener;
import fr.prima.omiscid.dnssd.interf.DNSSDFactory;
import fr.prima.omiscid.dnssd.interf.ServiceInformation;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.util.Utility;
import fr.prima.omiscid.user.variable.VariableAccessType;

/**
 * Encapsulates the data about a <b>remote</b> service. Service instantiation is done by
 * instantiating the {@link ControlServer} class. Contains the data extracted
 * from DNS-SD. Provides convenient access to these data. Provides also some
 * utility methods to generate ids for OMiSCID service (used in BIP exchange)
 * and to clean the names read from DNS-SD.
 *
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
// \REVIEWTASK shouldn't this be a monitor?
// \REVIEWTASK should add some delegate to the control client class
public class OmiscidService {

    /** Type for the registration */
    public static String REG_TYPE() { return REG_TYPE; }
    private static String REG_TYPE = GlobalConstants.dnssdDefaultWorkingDomain;
    static {
        try {
            // \REVIEWTASK this variable name should be documented somewhere
            if (null != System.getenv(GlobalConstants.dnssdWorkingDomainEnvironmentVariableName)) {
                REG_TYPE = System.getenv(GlobalConstants.dnssdWorkingDomainEnvironmentVariableName);
            }
        } catch (SecurityException e) {
            // Access to environment variable is forbidden
            System.err.println("Warning: access to environment variables is forbidden.");
        }
    };

    public static DNSSDFactory dnssdFactory = DNSSDFactory.DefaultFactory.instance();

    /** service id used to create the control client */
    private int peerId = 0;

    /**
     * Object used to synchronize the access to the controlClient, and the
     * number of user
     */
    private final Object controlClientSync = new Object();

    /** A control client to interrogate the service */
    private ControlClient ctrlClient = null;

    /** Number of current user for the control client */
    private int nbUserForControl = 0;

    /** DNS-SD informations about the service */
    private ServiceInformation serviceInformation;

    /**
     * Creates a new instance of OmiscidService with the given service
     * information.
     *
     * @param peerId
     *            the peer id used to represent the local peer in bip
     *            connections with the remote service
     * @param serviceInformation
     *            service information containing the data from DNS-SD
     */
    public OmiscidService(int peerId, ServiceInformation serviceInformation) {
        this.serviceInformation = serviceInformation;
        this.peerId = peerId;
    }

    /**
     * Creates an instance of OmiscidService with the given service information.
     * No local peer id is specified (unlike in
     * {@link #OmiscidService(int, ServiceInformation)), and it must be
     * specified with {@link #setServiceId(int)} if the OmiscidService is
     * intended to be used to make connections to the remote peer (explicitly or
     * implicitly).
     *
     * @param serviceInformation
     *            service information containing the data from DNS-SD
     */
    public OmiscidService(ServiceInformation serviceInformation) {
        this.serviceInformation = serviceInformation;
    }

    /**
     * Sets the peer id to use to represent the local peer in BIP exchanges with
     * the remote service.
     *
     * @param peerId
     *            the id to use
     * @see OmiscidService#initControlClient()
     */
    public void setServiceId(int peerId) {
        if (this.peerId != 0) {
            if (this.peerId != peerId) {
                System.err.println("Warning: peer id already set in OmiscidService (was " + Utility.intTo8HexString(this.peerId) + "), setting anyway (to "+Utility.intTo8HexString(peerId)+")");
            } else {
                System.err.println("Warning: useless setting of OmiscidService to the same value (" + Utility.intTo8HexString(this.peerId) + ")");
            }
        }
        this.peerId = peerId;
    }

    /**
     * Returns the name of the service (with the bip suffix removed and spaces
     * replaced).
     *
     * @return {@link #getSimplifiedName()}
     */
    public String toString() {
        return getSimplifiedName();
    }

    /**
     * Extracts owner name from the text record
     *
     * @return the owner name, or "" if the owner property is not defined
     */
    public String getOwner() {
        String str = serviceInformation.getStringProperty(GlobalConstants.constantNameForOwner);
        if (str != null) {
            return VariableAccessType.realValueFromDnssdValue(str);
        } else {
            // should do as getRemotePeerId does
            return "";
        }
    }

    /**
     * Extracts the remote peer id from the text record or by querying the
     * remote control server.
     *
     * @return the BIP peer id of the remote service
     */
    public int getRemotePeerId() {
        String str = OmiscidService.cleanName(serviceInformation.getFullName());
//        String str = serviceInformation.getStringProperty(GlobalConstants.constantNameForPeerId);
        if (str != null) {
            return Utility.hexStringToInt(str);
//            return Utility.hexStringToInt(VariableAccessType.realValueFromDnssdValue(str));
        } else {
            ControlClient ctrlClient = initControlClient();
            if (ctrlClient != null) {
                int pid = ctrlClient.getPeerId();
                closeControlClient();
                return pid;
            }
            return 0;
        }
    }

    /**
     * Initializes a new Control Client if it is not already existing.
     * <b>Warning:</b> {@link #closeControlClient()} must be called once for
     * each call to this method that have non-null return value. Increments the
     * number of users, this number is used by closeControlClient. When the user
     * do not use the control client any more, the user must call
     * closeControlClient.
     *
     * @return the control client or null if the creation failed
     * @see OmiscidService#closeControlClient()
     */
    public ControlClient initControlClient() {
        synchronized (controlClientSync) {
            if (peerId == 0) {
                System.err.println("Warning: no local peer id (service id) set in OmiscidService to access remote control server ... generating a new one");
                peerId = BipUtils.generateBIPPeerId();
            }
            if (ctrlClient == null) {
                ctrlClient = new ControlClient(peerId);
            }
            if (!ctrlClient.isConnected()) {
                if (!ctrlClient.connectToControlServer(serviceInformation.getHostName(), serviceInformation.getPort())) {
                    ctrlClient = null;
                }
            }
            if (ctrlClient != null) {
                nbUserForControl++;
            }
            return ctrlClient;
        }
    }

    /**
     * Closes the control client when it is no more used. (Must be called even
     * if the connection has already been lost) Decrements the number of current
     * user of control client. If the number reaches 0, the control client is
     * really closed. The control client can be obtained by calling
     * {@link OmiscidService#initControlClient()}
     */
    public void closeControlClient() {
        synchronized (controlClientSync) {
            nbUserForControl--;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    freeControlClient();
                }
            }, 1000); // One second delay before disconnecting
            if (nbUserForControl < 0) {
                System.err.println("Warning: in OmiscidService, to many calls to closeControlClient ... ignoring");
                nbUserForControl = 0;
            }
        }
    }

    protected void freeControlClient() {
        synchronized (controlClientSync) {
            if (nbUserForControl == 0) {
                if (ctrlClient != null) {
                    ctrlClient.close();
                }
            }
        }
    }

    /**
     * Creates a TCP connection to a connector on the remote service.
     *
     * @param ioa
     *            description associated to the connector on the remote server
     * @param messageListener
     *            a listener to add on this connection
     * @return a new TcpClient object or null if the connection failed
     */
    public TcpClient connectToConnector(InOutputAttribute ioa, BipMessageListener messageListener) {
        if (ioa != null) {
            try {
                TcpClient tcpClient = new TcpClient(peerId);
                if (messageListener != null) {
                    tcpClient.addBipMessageListener(messageListener);
                }
                tcpClient.connectTo(serviceInformation.getHostName(), ioa.getTcpPort());
                return tcpClient;
            } catch (IOException e) {
            }
        }
        return null;
    }

    /**
     * Utility method. Cleans a dnssd service name by removing trailing
     * "._bip._tcp.local." and restoring spaces characters. If you have an
     * {@link OmiscidService} instance, the dedicated method
     * {@link #getSimplifiedName()} can be used instead.
     *
     * @param fullName
     *            the service fullname to process
     * @return the cleaned service name
     */
    public static String cleanName(String fullName) {
        return fullName.replaceAll(("." + OmiscidService.REG_TYPE + ".local.?$").replaceAll("[.]", "\\."), "").replaceAll("\\\\032", " ");
    }

    public ServiceInformation getServiceInformation() {
        return serviceInformation;
    }

    /**
     * Gets the host name as expressed in the DNS-SD service information.
     */
    public String getHostName() {
        return serviceInformation.getHostName();
    }

    public int getPort() {
        return serviceInformation.getPort();
    }

    /**
     * Gets the full service name as expressed in the DNS-SD service
     * information.
     */
    public String getFullName() {
        return serviceInformation.getFullName();
    }

    public String getSimplifiedName() {
        String str = serviceInformation.getStringProperty(GlobalConstants.constantNameForName);
        if (str != null) {
            return VariableAccessType.realValueFromDnssdValue(str);
        } else {
            ControlClient ctrlClient = initControlClient();
            if (ctrlClient != null) {
                String name = ctrlClient.findVariable(GlobalConstants.constantNameForName).getValueStr();
                closeControlClient();
                return name;
            }
            return "";
        }
    }

    private boolean isServiceInformationDescriptionFull() {
        return GlobalConstants.keyForFullTextRecordFull.equals(serviceInformation.getStringProperty(GlobalConstants.keyForFullTextRecord));
    }

    /**
     *
     * @param variableName
     * @param variableAccessType null or a required access type for the variable
     * @param variableValueRegexp null or a regular expression that the value of the variable must match
     * @return
     */
    public boolean hasVariable(String variableName, VariableAccessType variableAccessType, String variableValueRegexp) {
        if (!isServiceInformationDescriptionFull()) {
            System.err.println("hasVariable not implemented completely, trying to find what we can anyway (service is "+getSimplifiedName()+" )");
        }
        {
            String property = serviceInformation.getStringProperty(variableName);
            return property != null
            && VariableAccessType.realValueFromDnssdValue(property) != null
            &&
            (
                    variableAccessType == null
                    ||
                    variableAccessType == VariableAccessType.fromDnssdValue(property)
            )
            &&
            (
                    variableValueRegexp == null
                    ||
                    (
                            VariableAccessType.CONSTANT == VariableAccessType.fromDnssdValue(property)
                            // FIXME if not constant should query control ...
                            &&
                            VariableAccessType.realValueFromDnssdValue(property).matches(variableValueRegexp)

                    )
            );
        }
    }

    public boolean hasConnector(String connectorName, ConnectorType connectorType) {
        if (!isServiceInformationDescriptionFull()) {
            System.err.println("hasConnector not implemented completely, trying to find what we can anyway (service is "+getSimplifiedName()+" )");
        }
        {
            String property = serviceInformation.getStringProperty(connectorName);
            return property != null
            && ConnectorType.realValueFromDnssdValue(property) != null
            &&
            (
                    connectorType == null
                    ||
                    connectorType == ConnectorType.fromDnssdValue(property)
            );
        }
    }

    public InOutputAttribute findConnector(int peerId) {
        initControlClient();
        InOutputAttribute findConnector = ctrlClient.findConnector(peerId);
        closeControlClient();
        return findConnector;
    }

    public InOutputAttribute findInOutput(String name) {
        initControlClient();
        InOutputAttribute findInOutput = ctrlClient.findInOutput(name);
        closeControlClient();
        return findInOutput;
    }

    public InOutputAttribute findInput(String name) {
        initControlClient();
        InOutputAttribute findInput = ctrlClient.findInput(name);
        closeControlClient();
        return findInput;
    }

    public InOutputAttribute findOutput(String name) {
        initControlClient();
        InOutputAttribute findOutput = ctrlClient.findOutput(name);
        closeControlClient();
        return findOutput;
    }

    public VariableAttribute findVariable(String name) {
        initControlClient();
        VariableAttribute findVariable = ctrlClient.findVariable(name);
        closeControlClient();
        return findVariable;
    }

    public Set<InOutputAttribute> getInOutputAttributesSet() {
        initControlClient();
        Set<InOutputAttribute> inOutputAttributesSet = ctrlClient.getInOutputAttributesSet();
        closeControlClient();
        return inOutputAttributesSet;
    }

    public Set<String> getInOutputNamesSet() {
        initControlClient();
        Set<String> inOutputNamesSet = ctrlClient.getInOutputNamesSet();
        closeControlClient();
        return inOutputNamesSet;
    }

    public Set<InOutputAttribute> getInputAttributesSet() {
        initControlClient();
        Set<InOutputAttribute> inputAttributesSet = ctrlClient.getInputAttributesSet();
        closeControlClient();
        return inputAttributesSet;
    }

    public Set<String> getInputNamesSet() {
        initControlClient();
        Set<String> inputNamesSet = ctrlClient.getInputNamesSet();
        closeControlClient();
        return inputNamesSet;
    }

    public Set<InOutputAttribute> getOutputAttributesSet() {
        initControlClient();
        Set<InOutputAttribute> outputAttributesSet = ctrlClient.getOutputAttributesSet();
        closeControlClient();
        return outputAttributesSet;
    }

    public Set<String> getOutputNamesSet() {
        initControlClient();
        Set<String> outputNamesSet = ctrlClient.getOutputNamesSet();
        closeControlClient();
        return outputNamesSet;
    }

    public Set<VariableAttribute> getVariableAttributesSet() {
        initControlClient();
        Set<VariableAttribute> variableAttributesSet = ctrlClient.getVariableAttributesSet();
        closeControlClient();
        return variableAttributesSet;
    }

    public Set<String> getVariableNamesSet() {
        initControlClient();
        Set<String> variableNamesSet = ctrlClient.getVariableNamesSet();
        closeControlClient();
        return variableNamesSet;
    }

    public void updateDescription() {
        initControlClient();
        ctrlClient.queryGlobalDescription();
        ctrlClient.queryCompleteDescription();
        closeControlClient();
    }

    public VariableAttribute queryVariableModification(String name, String value) {
        initControlClient();
        VariableAttribute queryVariableModification = ctrlClient.queryVariableModification(name, value);
        closeControlClient();
        return queryVariableModification;
    }

    public boolean subscribe(String varName, VariableChangeListener variableChangeListener) {
        //could hold a count of the allready subscribed variables ... to handle multiple subscribe to a same variable
        initControlClient();
        VariableAttribute va = findVariable(varName);
        if (va != null) {
            boolean subscribe = ctrlClient.subscribe(varName);
            if (!subscribe) {
                closeControlClient();
            } else {
                va.addListenerChange(variableChangeListener);
            }
            return subscribe;
        } else {
            closeControlClient();
            return false;
        }
    }

    public boolean unsubscribe(String varName, VariableChangeListener varListener) {
        VariableAttribute va = findVariable(varName);
        if (va != null) {
            boolean unsubscribe = ctrlClient.unsubscribe(varName);
            if (unsubscribe) {
                va.removeListenerChange(varListener);
                closeControlClient();
            }
            return unsubscribe;
        } else {
            return false;
        }
    }

}
