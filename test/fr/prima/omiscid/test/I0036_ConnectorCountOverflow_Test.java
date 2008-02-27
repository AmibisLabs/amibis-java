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
import fr.prima.omiscid.user.exception.ConnectorLimitReached;
import java.io.IOException;

import fr.prima.omiscid.user.exception.InvalidDescriptionException;
import fr.prima.omiscid.user.service.Service;

public class I0036_ConnectorCountOverflow_Test {
    
    public static void main(String[] args) throws InvalidDescriptionException, IOException {
        Service service = FactoryFactory.factory().create("I0036Server");
        try {
            for (int i = 0; i < 300; i++) {
                service.addConnector("c"+i, "...", ConnectorType.INOUTPUT);
            }
        } catch (ConnectorLimitReached e) {
            FactoryFactory.passed("ConnectorLimitReached properly occured");
            System.exit(0);
        } catch (Exception e) {
            FactoryFactory.failed("Unspecified exception occured "+e.getClass().getCanonicalName());
            System.exit(1);
        }
        FactoryFactory.failed("No exceptions occured");
        System.exit(1);
    }

}
