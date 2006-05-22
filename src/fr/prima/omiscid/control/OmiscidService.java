package fr.prima.omiscid.control;

import java.io.IOException;

import fr.prima.omiscid.com.BipUtils;
import fr.prima.omiscid.com.TcpClient;
import fr.prima.omiscid.com.interf.BipMessageListener;
import fr.prima.omiscid.dnssd.interf.DNSSDFactory;
import fr.prima.omiscid.dnssd.interf.ServiceInformation;

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
    public static final String REG_TYPE = "_bip._tcp";

    public static final String KEY_PEERID = "id";

    public static final String KEY_INPUTS = "inputs";

    public static final String KEY_OUTPUTS = "outputs";

    public static final String KEY_INOUTPUTS = "inoutputs";

    public static final String KEY_OWNER = "owner";

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
            System.err.println("Warning: peer id already set in OmiscidService (was " + this.peerId + "), setting anyway");
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
     * Extracts owner name from the text records
     *
     * @return the owner name, or "" if the owner property is not defined
     */
    public String getOwner() {
        String str = serviceInformation.getStringProperty("owner");
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * Extracts the remote peer id from the text record or by querying the
     * remote control server.
     *
     * @return the BIP peer id of the remote service
     */
    public int getRemotePeerId() {
        String str = serviceInformation.getStringProperty(KEY_PEERID);
        if (str != null) {
            return BipUtils.hexStringToInt(str);
        } else {
            ControlClient ctrlClient = initControlClient();
            int pid = ctrlClient.getPeerId();
            closeControlClient();
            return pid;
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
            if (ctrlClient == null || !ctrlClient.isConnected()) {
                ctrlClient = new ControlClient(peerId);
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
            if (nbUserForControl == 0) {
                if (ctrlClient != null) {
                    ctrlClient.close();
                }
            } else if (nbUserForControl < 0) {
                System.err.println("Warning: in OmiscidService, to many calls to closeControlClient ... ignoring");
                nbUserForControl = 0;
            }
        }
    }

    /**
     * Creates a TCP connection to a channel on the remote service.
     *
     * @param ioa
     *            description associated to the channel on the remote server
     * @param messageListener
     *            a listener to add on this connection
     * @return a new TcpClient object or null if the connection failed
     */
    public TcpClient connectToChannel(InOutputAttribute ioa, BipMessageListener messageListener) {
        if (ioa != null) {
            try {
                TcpClient tcpClient = new TcpClient(peerId);
                if (messageListener != null) {
                    tcpClient.addOmiscidMessageListener(messageListener);
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
        return fullName.replaceAll(("." + OmiscidService.REG_TYPE + ".local.$").replaceAll("[.]", "\\."), "").replaceAll("\\\\032", " ");
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
        return cleanName(getFullName());
    }

}
