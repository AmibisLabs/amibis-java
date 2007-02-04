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
    
//    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
//        Class[] tests = new Class[] {
//            CheckRemoteVariableRefresh_BugI0001.class,
//            SafeListenerCalls_BugI0002.class,
//            StressTestManyBigMessages.class,
//            TestSimpleServiceXMLDescription.class,
//        };
//        for (Class pseudoJUnit : tests) {
//           Method main = pseudoJUnit.getMethod("main", String[].class);
//           main.invoke(pseudoJUnit, new Object[]{new String[]{}});
//        }
//    }
}
