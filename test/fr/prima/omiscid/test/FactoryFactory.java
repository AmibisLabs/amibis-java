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

/*- IGNORE -*/
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
public class FactoryFactory {

    static Map<String, Object> result = new HashMap<String, Object>();
    static final Object PASSED = new Object();
    
    /*package*/ static ServiceFactory factory() {
        return new ServiceFactoryImpl();
    }
    
    /*package*/ static synchronized void passed(String msg) {
        String testName = findTestClass();
        if (result.containsKey(testName)) return;
        System.err.println("Test "+testName+" Passed: "+msg);
        result.put(testName, PASSED);
        if (isInJUnitThread()) consumeResult();
    }

    /*package*/ static synchronized void failed(String msg) {
        String testName = findTestClass();
        if (result.containsKey(testName)) return;
        System.err.println("Test "+testName+" Failed: "+msg);
        result.put(testName, "Test " + testName + " Failed: " + msg);
        if (isInJUnitThread()) consumeResult();
    }

    static synchronized void waitResult(long delay) throws InterruptedException {
        String testName = findTestClass();
        long timeout = System.currentTimeMillis()+delay;
        while (timeout - System.currentTimeMillis() > 0 && !result.containsKey(testName)) {
            FactoryFactory.class.wait(10);
        }
        consumeResult();
        return;
    }

    private static void consumeResult() {
        String testName = findTestClass();
        Object o = result.get(testName);
        System.err.println(o);
        if (o == null) return;
        if (o == PASSED) throw new TestPassedPseudoException();
        Assert.assertTrue(o.toString(), false);
    }

    private static String findTestClass() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        Class<?> that = FactoryFactory.class;
        for (StackTraceElement e : stack) {
            String name = e.getClassName();
            if (
                    !name.equals(that.getCanonicalName()) &&
                    name.startsWith(that.getPackage().getName())
                ) {
                return name.substring(that.getPackage().getName().length()+1).replaceFirst("\\$.*$", "");
            }
        }
        Assert.assertTrue("Failed to find test name in stack: " +Arrays.toString(Thread.currentThread().getStackTrace()), false);
        return null;
    }
    
    private static boolean isInJUnitThread() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : stack) {
            String name = e.getClassName();
            if (name.equals("org.junit.internal.runners.TestClassRunner")) {
                return true;
            }
        }
        return false;
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
