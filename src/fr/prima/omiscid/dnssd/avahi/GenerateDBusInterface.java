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
