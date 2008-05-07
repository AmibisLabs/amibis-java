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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import fr.prima.omiscid.user.exception.InvalidDescriptionException;
import fr.prima.omiscid.user.service.Service;


import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.variable.VariableAccessType;
import org.junit.Test;

public class I0012_SimpleServiceXMLDescription_Test {
    
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws InvalidDescriptionException, IOException {
        String serviceDotXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
                "<service name=\"I0012Server\" xmlns=\"http://www-prima.inrialpes.fr/schemas/omiscid/service.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www-prima.inrialpes.fr/schemas/bip/service.xsd service.xsd \">\n" + 
                "   <output name=\"sinus\">\n" + 
                "       <description>Stream of points (sinus fonction)</description>\n" + 
                "   </output>\n" + 
                "   <inoutput name=\"f\"></inoutput>\n" + 
                "   <input name=\"fin\"></input>\n" + 
                "   <variable name=\"w\">\n" + 
                "       <access>readWrite</access>\n" + 
                "   </variable>\n" + 
                "   <variable name=\"c\">\n" + 
                "       <access>constant</access>\n" + 
                "   </variable>\n" + 
                "   <variable name=\"r\">\n" + 
                "       <access>read</access>\n" + 
                "   </variable>\n" + 
                "</service>";
        InputStream in = new ByteArrayInputStream(serviceDotXml.getBytes("utf-8"));
        Service server = FactoryFactory.factory().createFromXML(in);
        server.start();
        Service client = FactoryFactory.factory().create("I0012Client");
        ServiceProxy proxy = client.findService(ServiceFilters.nameIs("I0012Server"), 5000);
        FactoryFactory.assertTrue("sinus", proxy.getOutputConnectors().contains("sinus"));
        FactoryFactory.assertTrue("f", proxy.getInputOutputConnectors().contains("f"));
        FactoryFactory.assertTrue("fin", proxy.getInputConnectors().contains("fin"));
        FactoryFactory.assertFalse("fin not an i/o", proxy.getInputOutputConnectors().contains("fin"));
        FactoryFactory.assertFalse("cosinus not present", proxy.getOutputConnectors().contains("cosinus"));
        FactoryFactory.assertTrue("w", proxy.getVariables().contains("w"));
        FactoryFactory.assertTrue("w is readwrite", proxy.getVariableAccessType("w") == VariableAccessType.READ_WRITE);
        FactoryFactory.assertTrue("c", proxy.getVariables().contains("c"));
        FactoryFactory.assertTrue("c is constant", proxy.getVariableAccessType("c") == VariableAccessType.CONSTANT);
        FactoryFactory.assertTrue("r", proxy.getVariables().contains("r"));
        FactoryFactory.assertTrue("r is read", proxy.getVariableAccessType("r") == VariableAccessType.READ);
        FactoryFactory.assertTrue("peerId", proxy.getVariables().contains("peerId"));
        FactoryFactory.passed("no exceptions occured");
        in.close();
    }

}
