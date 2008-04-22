
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.service.impl.ServiceFactoryImpl;
import fr.prima.omiscid.user.variable.RemoteVariableChangeListener;
import fr.prima.omiscid.user.variable.VariableAccessType;
import javax.swing.SwingUtilities;

/**
 *
 */
public class TestControlPerformances {
    
    public static void main(String[] args) {
        ServiceFactory f = new ServiceFactoryImpl();
        Service s1 = f.create("S1");
        s1.addVariable("v", "bla", "@Range(0..10000)", VariableAccessType.READ_WRITE);
        s1.start();
        final ServiceProxy p1 = s1.findService(ServiceFilters.nameIs("S1"));
        p1.addRemoteVariableChangeListener("v", new RemoteVariableChangeListener() {
            int count = 0;
            long lastPing = System.currentTimeMillis();
            public void variableChanged(ServiceProxy serviceProxy, String variableName, String value) {
                count++;
                //System.out.println("Count "+count);
                final int ccount = count;
                if (count % 20 == 0) {
                    long oldTime = lastPing;
                    lastPing = System.currentTimeMillis();
                    System.err.println("Elapsed time: "+(lastPing-oldTime)+" ms");
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        p1.setVariableValue("v", ""+ccount);
                    }
                });
            }
        });
        p1.setVariableValue("v", "bla");
    }

}
