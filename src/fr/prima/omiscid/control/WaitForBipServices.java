/*
 * Created on 6 juin 2005
 * TODO wait for 2 different instance of a service 
 */
package fr.prima.omiscid.control ;


/**
 * Wait for several BIP services. This class enables to search in the same time several BIP services. Then it enables to wait that they are all found.
 * @author  Sebastien Pesnel refactoring emonet
 */
public class WaitForBipServices {
    /** Number max of service for which we can wait */
    private final int MAX_SERVICES = 10;
    /**
	 * researched services
	 * @uml.property  name="searchServiceArray"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
    private BipServiceWaiter searchServiceArray[] = new BipServiceWaiter[MAX_SERVICES];

    /** Create a new instance of WaitForBipServices */
    public WaitForBipServices() {
        for (int i = 0; i < searchServiceArray.length; i++) {
            searchServiceArray[i] = null;
        }
    }
    /** Need a service 
     * @param name the name of the wanted service 
     * @return the index to retrieve the wanted service or to know if it has been found */
    public int needService(String name) {
        return needService(name, null);
    }
    /** Need a service 
     * @param name the name of the wanted service 
     * @param w an object implementing WaitForTheGoodBipService interface to test
     * if the service is ok according the wishes of the user.
     * @return the index to retrieve the wanted service or to know if it has been found */
    public int needService(String name, BipServiceFilter w) {
        int index = -1;
        for (int i = 0; i < searchServiceArray.length; i++) {
            if (searchServiceArray[i] == null) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            searchServiceArray[index] = new BipServiceWaiter(name,w);
            searchServiceArray[index].startSearch();
        }
        return index;
    }
    /** Returns if all the needed services have been found
     * @return true if all the needed services have been found */
    private boolean areAllResolved() {
        for (int i = 0; i < searchServiceArray.length; i++) {
            if (searchServiceArray[i] != null
                    && !searchServiceArray[i].isResolved())
                return false;
        }
        return true;
    }
    /** Wait until all the needed services have been found */
    public void waitResolve() {
        while (!areAllResolved()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /** Returns if a needed service has been found
     * @param index the index associated to the needed service (return by needService)
     * @return if a needed service has been found
     * @see WaitForBipServices#needService(String) */
    public boolean isResolved(int index){
        return searchServiceArray[index].isResolved();
    }
    /** Returns the needed service 
     * @param index the index associated to the needed service (return by needService)
     * @return the needed service or null if not found yet. 
     * @see WaitForBipServices#needService(String)
     * @see WaitForBipServices#isResolved(int)*/
    public BipService getService(int index) {
        if (searchServiceArray[index].isResolved())
            return searchServiceArray[index].getBipService();
        else
            return null;
    }
//    /** Main method for test. Wait for a service named "essai".*/
//    public static void main(String[] arg) {
//        WaitForBipServices waitfor = new WaitForBipServices();
//        int index = waitfor.needService("essai");
//        waitfor.waitResolve();
//
//        Service s = waitfor.getService(index);
//
//        for (int i = 0; i < s.txtRecord.size(); i++) {
//            System.out.println(s.txtRecord.getKey(i) + " --> "
//                    + s.txtRecord.getValueAsString(i));
//        }
//    }
}

