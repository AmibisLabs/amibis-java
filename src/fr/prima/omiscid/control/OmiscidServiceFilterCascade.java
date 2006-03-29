package fr.prima.omiscid.control ;

/**
 * Provide simple test. Enables to combine several tests.
 * @author Sebastien Pesnel refactoring emonet
 * 
 */
public class OmiscidServiceFilterCascade implements OmiscidServiceFilter{
    
    private java.util.LinkedList<OmiscidServiceFilter> listTest = 
        new java.util.LinkedList<OmiscidServiceFilter>();
    
    public OmiscidServiceFilterCascade(){}
    public OmiscidServiceFilterCascade(OmiscidServiceFilter ...filters){
        for (OmiscidServiceFilter filter : filters) {
            addTest(filter);
        }
    }
    
    public void addTest(OmiscidServiceFilter w){
        synchronized (listTest) {        
            listTest.add(w);
        }
    }

    public void removeTest(OmiscidServiceFilter w){
        synchronized (listTest) {        
            listTest.remove(w);
        }
    }
   
    public boolean isAGoodService(OmiscidService s) {
        java.util.Iterator<OmiscidServiceFilter> it = listTest.iterator();
        while(it.hasNext()){
            if(!it.next().isAGoodService(s)) return false;
        }
        return true;
    }
}
