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

package fr.prima.omiscid.dnssd.avahi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.parsers.ParserConfigurationException;

import org.freedesktop.dbus.CreateInterface;
import org.freedesktop.dbus.DBusException;
import org.xml.sax.SAXException;

public class GenerateDBusInterface {
    static class FileStreamFactory extends CreateInterface.PrintStreamFactory {
        public String prefix = "";

        public FileStreamFactory(String prefix) {
            super();
            this.prefix = prefix;
        }
        public void init(String file, String path) {
            new File(prefix+path).mkdirs();
        }
        /**
         * @param file
         * @return
         * @throws IOException
         */
        public PrintStream createPrintStream(final String file) throws IOException {
            return new PrintStream(new FileOutputStream(prefix+file)) {
                @Override
                public void println(String x) {
                    super.println(x.replace("interface", "_interface"));
                }

            };
        }

    }

    public static void main(String[] args) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, DBusException {
        CreateInterface createInterface = new CreateInterface(new FileStreamFactory("generated-src/"));
        for (File file : new File("/usr/share/avahi/introspection/").listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".introspect");
            }
        })) {
            createInterface.createInterface(new FileReader(file));
        }
    }
}
