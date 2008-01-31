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


import fr.prima.omiscid.user.connector.ConnectorType;
import java.io.IOException;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.variable.VariableAccessType;
import java.util.Vector;

public class I0049_TestCheckConnectorAndVariableNames {
    
    /*
     * Following a problem encountered by Jean Pascal where
     * only adding a sync-in connector to a service makes its
     * imageStream connector not connectable by gui.
     */
    public static void main(String[] args) throws IOException {
        ServiceFactory factory = FactoryFactory.factory();
        final Service server = factory.create("I0049Server");
        server.addVariable("bugvar", "", "", VariableAccessType.CONSTANT);
        server.addVariable("  ", "", "", VariableAccessType.CONSTANT);
        server.addVariable("  a", "", "", VariableAccessType.CONSTANT);
        server.addVariable("a  ", "", "", VariableAccessType.CONSTANT);
        server.addVariable(" a ", "", "", VariableAccessType.CONSTANT);
        server.addVariable("more-var_!/\\!\"", "", "", VariableAccessType.CONSTANT);
        server.addConnector("bug", "", ConnectorType.OUTPUT);
        server.addConnector("dash-bug", "", ConnectorType.OUTPUT);
        server.addConnector("more-bug_!/\\!\"", "", ConnectorType.OUTPUT);
        server.addConnector(" ", "", ConnectorType.OUTPUT);
        server.addConnector(" a", "", ConnectorType.OUTPUT);
        server.addConnector("a ", "", ConnectorType.OUTPUT);
        Vector<String> problems = new Vector<String>();
        for (String name : new String[]{"noé", "no=", "no\t", "", "desc", "name", "owner", "class", "lock", "id", "host", "BUG", "BuGvAr", "bug", "bugvar", "no\n"}) {
            try {
                server.addConnector(name, "", ConnectorType.INPUT);
            } catch (Exception e) {
                System.out.println("Connector '"+name+"' properly refused");
                continue;
            }
            problems.add(name);
        }
        for (String name : new String[]{"noé", "no=", "no\t", "", "desc", "name", "owner", "class", "lock", "id", "host", "BUG", "BuGvAr", "bug", "bugvar", "no\n"}) {
            try {
                server.addVariable(name, "", "", VariableAccessType.READ);
            } catch (Exception e) {
                System.out.println("Variable '"+name+"' properly refused");
                continue;
            }
            problems.add(name);
        }
        if (!problems.isEmpty()) {
            String problemString = "";
            for (String pb : problems) {
                problemString += "\"" + pb + "\" ; ";
            }
            FactoryFactory.failed("Some wrong names where wrongly accepted: "+problemString.substring(0, problemString.length()-3));
            System.exit(1);
        } else {
            FactoryFactory.passed("All wrong names have been properly refused");
            System.exit(0);
        }
    }

}
