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
import java.util.Vector;

import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.exception.InvalidDescriptionException;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.service.ServiceProxy;

public class BugI0008_MultipleFindAndConnectToCausesConnectionException {
    
    // This tries to replicate a problem seen with Marina's osgi code.
    // 
    
    // This works (at the time of writing)
    public static void main(String[] args) throws IOException, InterruptedException {
        ServiceFactory factory = FactoryFactory.factory();
        Vector<String> startedServices = new Vector<String>();
        for (int i = 0; i<5; i++) {
            String name = "BugI0008Server"+i;
            startedServices.add(name);
            
            String serviceDotXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
            "<service name=\""+name+"\" xmlns=\"http://www-prima.inrialpes.fr/schemas/bip/service.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www-prima.inrialpes.fr/schemas/bip/service.xsd service.xsd \">\n" + 
            "   <output name=\""+"bug"+"\">\n" + 
            "       <description>Stream of points (sinus fonction)</description>\n" + 
            "   </output>\n" + 
            "   <inoutput name=\"f\"></inoutput>\n" + 
            "   <variable name=\"w\">\n" + 
            "       <access>readWrite</access>\n" + 
            "   </variable>\n" + 
            "</service>";
            InputStream in = new ByteArrayInputStream(serviceDotXml.getBytes("utf-8"));
            try {
                Service service = factory.createFromXML(in);
                service.start();
            } catch (InvalidDescriptionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
//            final Service server = factory.create(name);
//            server.addConnector("bug", "", ConnectorType.OUTPUT);
//            server.start();
        }
        {
            final Service client = factory.create("BugI0008Client");
            client.addConnector("bug", "plop", ConnectorType.INPUT);
            client.start();
            client.findService(ServiceFilters.nameIs("BugI0008Client"));
            for (String name : startedServices) {
                ServiceProxy proxy = client.findService(ServiceFilters.nameIs(name));
                client.connectTo("bug", proxy, "bug");
            }
            FactoryFactory.passed("Search done properly");
            System.exit(0);
        }
    }

}
