package fr.prima.bipcontrol ;

/**
 * Provide simple test. Enables to combine several tests.
 * @author Sebastien Pesnel refactoring emonet
 * 
 */
public class BipServiceFilterCascade implements BipServiceFilter{
    /** Test if a service has the good owner name */
    public static final class GoodOwner implements BipServiceFilter{
        private String owner = null;
        public GoodOwner(String owner){ 
            this.owner = owner;            
        }
        public boolean isAGoodService(BipService s) {
            byte[] b = s.getServiceInformation().getProperty("owner");                
            return (b != null  && b.length != 0 && (new String(b)).equals(owner));
        }
    }
    /** Test if the host of the service has a name that begins with a particular string */
    public static final class GoodHost  implements BipServiceFilter{
        private String hostName = null;
        public GoodHost(String hostname){
            hostName = hostname;
        }
        public boolean isAGoodService(BipService s){
            return s.getHostName().startsWith(hostName);                
        }
    }
    /** test a value in the TXT record */
    public static final class GoodKeyValue implements BipServiceFilter{
        private String key = null;
        private String value = null;
        public GoodKeyValue(String k, String v){
            key = k; value = v; 
        }
        public boolean isAGoodService(BipService s){
            String val = s.getServiceInformation().getStringProperty(key);
            if(value == null)
                return val == null;
            else
                return  value.equals(val);                
        }
    }
    
    private java.util.LinkedList<BipServiceFilter> listTest = 
        new java.util.LinkedList<BipServiceFilter>();
    
    public BipServiceFilterCascade(){}
    public BipServiceFilterCascade(BipServiceFilter w){
        addTest(w);        
    }
    
    public void addTest(BipServiceFilter w){
        synchronized (listTest) {        
            listTest.add(w);
        }
    }

    public void removeTest(BipServiceFilter w){
        synchronized (listTest) {        
            listTest.remove(w);
        }
    }
   
    public boolean isAGoodService(BipService s) {
        java.util.Iterator<BipServiceFilter> it = listTest.iterator();
        while(it.hasNext()){
            if(!it.next().isAGoodService(s)) return false;
        }
        return true;
    }
}
