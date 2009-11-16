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

import fr.prima.omiscid.user.exception.ServiceRunning;
import java.io.IOException;

import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;


import org.junit.Test;

public class I0062_StartServiceTwiceWithNoStopTest {

    @Test(expected = TestPassedPseudoException.class)
    public void doIt() throws IOException {
        final ServiceFactory factory = FactoryFactory.factory();
        final Service s = factory.create("I0062Service");
        s.start();
        try {
            s.start();
            FactoryFactory.failed("Second “start” did not cause an error");
        } catch (ServiceRunning ex) {
            if (ex.getMessage().contains("start")) {
                FactoryFactory.passed("Proper exception on second “start”");
            } else {
                FactoryFactory.failed("Wrong message for (proper) exception on second “start”");
            }
        }
    }

}
