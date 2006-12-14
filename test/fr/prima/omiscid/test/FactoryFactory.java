/*
 * Created on 14 déc. 06
 *
 */
package fr.prima.omiscid.test;

import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.impl.ServiceFactoryImpl;

public class FactoryFactory {

    /*package*/ static ServiceFactory factory() {
        return new ServiceFactoryImpl();
    }
    
    /*package*/ static void passed(String msg) {
        System.err.println("Test Passed: "+msg);
    }    

    /*package*/ static void failed(String msg) {
        System.err.println("Test Failed: "+msg);
    }    
}
