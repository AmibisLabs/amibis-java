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


import org.junit.Test;
import static org.junit.Assert.*;

public class I0012_SimpleServiceXMLDescription_Test {
    
    @Test(expected=TestPassedPseudoException.class)
    public void doIt() throws InvalidDescriptionException, IOException {
        String serviceDotXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
                "<service name=\"I0012Service\" xmlns=\"http://www-prima.inrialpes.fr/schemas/bip/service.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www-prima.inrialpes.fr/schemas/bip/service.xsd service.xsd \">\n" + 
                "   <output name=\"sinus\">\n" + 
                "       <description>Stream of points (sinus fonction)</description>\n" + 
                "   </output>\n" + 
                "   <inoutput name=\"f\"></inoutput>\n" + 
                "   <input name=\"fin\"></input>\n" + 
                "   <variable name=\"w\">\n" + 
                "       <access>readWrite</access>\n" + 
                "   </variable>\n" + 
                "</service>";
        InputStream in = new ByteArrayInputStream(serviceDotXml.getBytes("utf-8"));
        Service service = FactoryFactory.factory().createFromXML(in);
        service.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        service.stop();
        FactoryFactory.passed("no exceptions occured");
        in.close();
    }

}
