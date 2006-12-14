/*
 * Created on 14 déc. 06
 *
 */
package fr.prima.omiscid.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import fr.prima.omiscid.user.exception.InvalidDescriptionException;
import fr.prima.omiscid.user.service.Service;

public class TestSimpleServiceXMLDescription {
    
    public static void main(String[] args) throws InvalidDescriptionException, IOException {
        String serviceDotXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
                "<service name=\"fromXMLDesc\" xmlns=\"http://www-prima.inrialpes.fr/schemas/bip/service.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www-prima.inrialpes.fr/schemas/bip/service.xsd service.xsd \">\n" + 
                "   <output name=\"sinus\">\n" + 
                "       <description>Stream of points (sinus fonction)</description>\n" + 
                "   </output>\n" + 
                "   <inoutput name=\"f\"></inoutput>\n" + 
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
