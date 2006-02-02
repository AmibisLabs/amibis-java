package fr.prima.bipcontrol ;

/**
 * Provide simple test. Enables to combine several tests.
 * @author Sebastien Pesnel
 */
public class TestsForGoodService implements WaitForTheGoodBipService{
    /** Test if a service has the good owner name */
    public static final class GoodOwner implements WaitForTheGoodBipService{
        private String owner = null;
        public GoodOwner(String owner){ 
            this.owner = owner;            
        }
        public boolean isAGoodService(BipService s) {
            byte[] b = s.getTxtRecordValue("owner");                
            return (b != null  && b.length != 0 && (new String(b)).equals(owner));
        }
    }
    /** Test if the host of the service has a name that begins with a particular string */
    public static final class GoodHost  implements WaitForTheGoodBipService{
        private String hostName = null;
        public GoodHost(String hostname){
            hostName = hostname;
        }
        public boolean isAGoodService(BipService s){
            return s.hostName.startsWith(hostName);                
        }
    }
    /** test a value in the TXT record */
    public static final class GoodKeyValue implements WaitForTheGoodBipService{
        private String key = null;
        private String value = null;
        public GoodKeyValue(String k, String v){
            key = k; value = v; 
        }
        public boolean isAGoodService(BipService s){
            String val = s.txtRecord.getValueAsString(key);
            if(value == null)
                return val == null;
            else
                return  value.equals(val);                
        }
    }
    
    private java.util.LinkedList<WaitForTheGoodBipService> listTest = 
        new java.util.LinkedList<WaitForTheGoodBipService>();
    
    public TestsForGoodService(){}
    public TestsForGoodService(WaitForTheGoodBipService w){
        addTest(w);        
    }
    
    public void addTest(WaitForTheGoodBipService w){
        synchronized (listTest) {        
            listTest.add(w);
        }
    }

    public void removeTest(WaitForTheGoodBipService w){
        synchronized (listTest) {        
            listTest.remove(w);
        }
    }
   
    public boolean isAGoodService(BipService s) {
        java.util.Iterator<WaitForTheGoodBipService> it = listTest.iterator();
        while(it.hasNext()){
            if(!it.next().isAGoodService(s)) return false;
        }
        return true;
    }
}
