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

package fr.prima.omiscid.test.other;

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
public class ControlPerformanceTest {
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
