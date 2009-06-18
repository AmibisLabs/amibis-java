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

package fr.prima.omiscid.control;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 *
 * @author emonet
 */
class JAXBTools {
    private static Map<Class, JAXBContext> context = new HashMap();
    
    static void marshal(Object o, OutputStreamWriter out) {
        try {
            JAXBContext c = context.get(o.getClass());
            if (c == null) {
                context.put(o.getClass(), c = JAXBContext.newInstance(o.getClass()));
            }
            c.createMarshaller().marshal(o, out);
        } catch (JAXBException ex) {
            Logger.getLogger(JAXBTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static <T> T unmarshal(InputStreamReader in, Class<T> aClass) {
        try {
            JAXBContext c = context.get(aClass);
            if (c == null) {
                context.put(aClass, c = JAXBContext.newInstance(aClass));
            }
            return (T) c.createUnmarshaller().unmarshal(in);
        } catch (JAXBException ex) {
            Logger.getLogger(JAXBTools.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
