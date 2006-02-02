package fr.prima.bipcontrol ;

import fr.prima.bipcom.MsgSocket;
import fr.prima.bipcom.interf.BipMessageListener;
import java.io.IOException;
import java.util.Random;

/**
 * Structure to group the data about a service. Group the data extracted from DNS-SD. Provides also methods to generate id for BIP service  (used in BIP exchange), to manipulate the text records.
 * @author  Sebastien Pesnel  Refactoring by Patrick Reignier
 */
public class BipService extends Service{
    /** Type for the registration */
    public static final String REG_TYPE = new String("_bip._tcp");
    
    /** service id used to create the control client*/
    private int serviceId = generateServiceId();   
    /** Object used to synchronize the access to the controlClient, 
     * and the number of user */
    private final Object controlClientSync = new Object();
    /** A control client to interrogate the service */
    private ControlClient ctrlClient = null;
    /** Number of current user for the control client */
    private int nbUserForControl = 0;
    

    /** Creates a new instance of BipService with the data of a service.
     * @param serviceId an id used to create a control client to communicate 
     * with the service s.
     * @param s service containing the data from DNS-SD */
    public BipService(int serviceId, Service s){        
        super(s);
        this.serviceId = serviceId;
    }
    
    /**
     * Create a new instance of BipService
     * @param name service name
     * @param regType register type
     * @param aDomain domain name
     */
    public BipService(String name, String regType, String aDomain){
        super(name, regType, aDomain);
    }
    
    /**
	 * Define the id to use to create the control client and communicate with the control server
	 * @param serviceId  the id to use
	 * @see  BipService#initControlClient()
	 * @uml.property  name="serviceId"
	 */
    public void setServiceId(int serviceId){
        this.serviceId = serviceId;
    }
    
    /** Returns the name of the service */
    public String toString(){
        return fullName;
    }
    
    /** Extract owner name from the text records 
     * @return the owner name, or "" if the owner property is not defined */
    public String getOwner(){
        String str = txtRecord.getValueAsString("owner");
        if(str == null) return "";
        else return str;
    }
    /** Extract peer id from the text record or from message exchange on controlClient */
    public int getPeerId(){
        String str = txtRecord.getValueAsString("peerId");
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
     * @see BipService#closeControlClient()
     */
    public ControlClient initControlClient(){
        synchronized (controlClientSync){
            if(ctrlClient == null || !ctrlClient.isConnected()){
                ctrlClient = new ControlClient(serviceId);
                if(!ctrlClient.connectToControlServer(hostName, port)){                    
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
     * The control client can be obtained by calling {@link BipService#initControlClient()}
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
    public fr.prima.bipcom.TcpClient connectToAServer(InOutputAttribut ioa, BipMessageListener bml){
        if(ioa != null){
            try{
                fr.prima.bipcom.TcpClient tcpClient = new fr.prima.bipcom.TcpClient(serviceId);
                if(bml != null) tcpClient.addBipMessageListener(bml);
                tcpClient.connectTo(hostName, ioa.getTcpPort());            
                return tcpClient;
            }catch(IOException e){}
        }
        return null;
    }
    
    
    private static Random randomForThisProcess = new Random(System.currentTimeMillis());
    /**
     * Generate an id for a BIP service
     * based on a random number and the current time
     * @return a new id for a service
     */
    public static int generateServiceId() {
        System.out.println(Thread.currentThread().getId() + ": " + randomForThisProcess);
        int partTime = (int) (System.currentTimeMillis() & 0x0000FFFF);
        double r = Math.random();
        int partRandom = (int)((int)( r * 0xEFFFFFFF) & 0xFFFF0000);
        return partTime + partRandom;
        //return (int) (System.currentTimeMillis() & 0xFFFFFFFF);
    }
}
