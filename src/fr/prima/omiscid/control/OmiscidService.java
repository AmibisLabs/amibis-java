package fr.prima.omiscid.control ;

import java.io.IOException;
import java.util.Random;

import fr.prima.omiscid.com.MsgSocket;
import fr.prima.omiscid.com.interf.OmiscidMessageListener;
import fr.prima.omiscid.dnssd.interf.DNSSDFactory;
import fr.prima.omiscid.dnssd.interf.ServiceInformation;

/**
 * Structure to group the data about a service. Group the data extracted from DNS-SD. Provides also methods to generate id for OMiSCID service  (used in OMiSCID exchange), to manipulate the text records.
 * @author  Sebastien Pesnel  Refactoring by Patrick Reignier and emonet
 */
public class OmiscidService {
    /** Type for the registration */
    public static final String REG_TYPE = "_bip._tcp";
    public static final String KEY_PEERID = "id";
    public static final String KEY_INPUTS = "inputs";
    public static final String KEY_OUTPUTS = "outputs";
    public static final String KEY_INOUTPUTS = "inoutputs";
        
    public static DNSSDFactory dnssdFactory = DNSSDFactory.DefaultFactory.instance();
    
    /** service id used to create the control client*/
    private int serviceId = generateServiceId();
    /** Object used to synchronize the access to the controlClient, 
     * and the number of user */
    private final Object controlClientSync = new Object();
    /** A control client to interrogate the service */
    private ControlClient ctrlClient = null;
    /** Number of current user for the control client */
    private int nbUserForControl = 0;
    
    private ServiceInformation serviceInformation;

    /** Creates a new instance of OmiscidService with the data of a service.
     * @param serviceId an id used to create a control client to communicate 
     * with the service s.
     * @param s service containing the data from DNS-SD */
    public OmiscidService(int serviceId, ServiceInformation s){        
        this.serviceInformation = s;
        this.serviceId = serviceId;
    }

    public OmiscidService(ServiceInformation s){        
        this.serviceInformation = s;
    }

    /**
	 * Define the id to use to create the control client and communicate with the control server
	 * @param serviceId  the id to use
	 * @see  OmiscidService#initControlClient()
	 * @uml.property  name="serviceId"
	 */
    public void setServiceId(int serviceId){
        this.serviceId = serviceId;
    }
    
    /** Returns the name of the service */
    public String toString(){
        return serviceInformation.getFullName();
    }
    
    /** Extract owner name from the text records 
     * @return the owner name, or "" if the owner property is not defined
     */
    public String getOwner(){
        String str = serviceInformation.getStringProperty("owner");
        if(str == null) return "";
        else return str;
    }
    /** Extract peer id from the text record or from message exchange on controlClient */
    public int getPeerId(){
        String str = serviceInformation.getStringProperty("peerId");
        if(str != null){
            return MsgSocket.hexStringToInt(str);
        }else{
            ControlClient ctrlClient = initControlClient();
            int pid = ctrlClient.getPeerId();
            closeControlClient();
            return pid;
        }
    }

    /** Initialize a new Control Client if not already existing.
     * Increment the number of users, this number is used by closeControlClient.
     * When the user do not use the control client any more, the user must call
     * closeControlClient.
     * @return the control client or null if the creation failed 
     * @see OmiscidService#closeControlClient()
     */
    public ControlClient initControlClient(){
        synchronized (controlClientSync){
            if(ctrlClient == null || !ctrlClient.isConnected()){
                ctrlClient = new ControlClient(serviceId);
                if( ! ctrlClient.connectToControlServer(serviceInformation.getHostName(), serviceInformation.getPort())){                    
                    ctrlClient = null;
                }/*else{
                    System.out.println("new control client");
                }*/
            }/*else System.out.println("reuse control client");*/
            if(ctrlClient != null) nbUserForControl++;
            return ctrlClient;
        }
    }
    /** Close the control client when it is no more used.
     * (Must be called even if the connection has already been lost)
     * The number of current user of control client is decremented.
     * If the number becomes 0, the control client is really closed. 
     * The control client can be obtained by calling {@link OmiscidService#initControlClient()}
     */
    public void closeControlClient(){
        synchronized (controlClientSync){
            nbUserForControl--;
            if(nbUserForControl <= 0){
                if(ctrlClient != null) ctrlClient.close();
                nbUserForControl = 0;
            }
        }
    }
    
    /** Create a TCP connection to a server.
     * @param ioa description associated to the server
     * @return a new TcpClient object or null if the connection failed.
     * */
    public fr.prima.omiscid.com.TcpClient connectToAServer(InOutputAttribut ioa, OmiscidMessageListener bml){
        if(ioa != null){
            try{
                fr.prima.omiscid.com.TcpClient tcpClient = new fr.prima.omiscid.com.TcpClient(serviceId);
                if(bml != null) tcpClient.addOmiscidMessageListener(bml);
                tcpClient.connectTo(serviceInformation.getHostName(), ioa.getTcpPort());            
                return tcpClient;
            }catch(IOException e){}
        }
        return null;
    }
    
    private static Random randomForThisJVM = new Random(System.currentTimeMillis());
    /**
     * Generate an id for a OMiSCID service
     * based on a random number and the current time.
     * 
     * Warning !!! If two jvms init their variables at the same currentTimeMillis
     * and call generateServiceId at the same currentTimeMillis there *will* be a problem.
     * 
     * Note that it is virtually not guaranteed that this id is unique.
     *  
     * @return a new id for a service
     */
    public static int generateServiceId() {
        //System.out.println(Thread.currentThread().getId() + " , "+ Thread.currentThread().getName() + " , "+ Thread.currentThread().getThreadGroup().getName() + ": " + randomForThisJVM);
        int partTime = (int) (System.currentTimeMillis() & 0x0000FFFF);
        double r = randomForThisJVM.nextDouble();
        int partRandom = (int)((int)( r * 0xEFFFFFFF) & 0xFFFF0000);
        return partTime + partRandom;
        //return (int) (System.currentTimeMillis() & 0xFFFFFFFF);
    }

    public ServiceInformation getServiceInformation() {
        return serviceInformation;
    }

    public String getHostName() {
        return serviceInformation.getHostName();
    }

    public int getPort() {
        return serviceInformation.getPort();
    }

    public String getFullName() {
        return serviceInformation.getFullName();
    }
}
