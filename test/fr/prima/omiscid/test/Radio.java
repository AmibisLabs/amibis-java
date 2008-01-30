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

import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.util.Utility;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/*- IGNORE -*/
public class Radio {
    private final static String serviceName = "Radio";
    private final static String audioOutputName = "AudioForMovies";
    private final static String titleVariableName = "Titre chanson";
    private int sampleRate = 44100;
    private int channels = 2;

    public Radio(String[] userArgs) {
        File pipeFile = null;
        String pipe = "/tmp/radio";
        try {
            pipeFile = File.createTempFile("radio", null);
            pipeFile.delete();
            pipe = pipeFile.getAbsolutePath();
        } catch (IOException ex) {
        }
        Vector<String> args = new Vector<String>();
        args.addAll(Arrays.asList(new String[]{
            "mplayer", "-ao", "pcm:nowaveheader:file="+pipe, "-vc", "null", "-vo", "null",
            "-quiet", "-slave",
            "-af", "resample="+sampleRate
        }));
        args.addAll(Arrays.asList(userArgs));
        try {
            Service service = FactoryFactory.factory().create(serviceName);
            service.addVariable("SampleRate", "integer", "sample rate", fr.prima.omiscid.user.variable.VariableAccessType.CONSTANT);
            service.setVariableValue("SampleRate", java.lang.Integer.toString(sampleRate));
            service.addVariable("Channels", "integer", "sample rate", fr.prima.omiscid.user.variable.VariableAccessType.CONSTANT);
            service.setVariableValue("Channels", java.lang.Integer.toString(channels));
            service.addVariable(titleVariableName, "integer", "Titre de la Chanson", fr.prima.omiscid.user.variable.VariableAccessType.READ);
            service.setVariableValue(titleVariableName, userArgs[0]);
            service.addConnector(audioOutputName, "output for sound", fr.prima.omiscid.user.connector.ConnectorType.OUTPUT);
            service.start();
            while (true) {
                startPlayerAndStream(service, args, pipe);
            }
        } catch (Exception ex) {
            Logger.getLogger(Radio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            args = new String[]{"http://streaming.radio.rtl2.fr:80/rtl2-1-44-96"};
        }
        new Radio(args);
    }

    private void consume(final InputStream in) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    byte[] buf = new byte[128];
                    while (-1 != in.read(buf)) {}
                } catch (IOException ex) {
                    Logger.getLogger(Radio.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    private void startPlayerAndStream(Service service, Vector<String> args, String pipe) throws InterruptedException, IOException, FileNotFoundException {
        System.err.println("START");
        Runtime.getRuntime().exec(new String[]{"mkfifo", pipe}).waitFor();
        new File(pipe).deleteOnExit();
        Process mplayer = Runtime.getRuntime().exec(args.toArray(new String[0]));
        consume(mplayer.getInputStream());
        consume(mplayer.getErrorStream());
        InputStream input = new FileInputStream(pipe);
        int count = 0;
        int read;
        /*
        byte[] buffer = new byte[1024];
        for (int i = 0; i < 44; i++) {
        //System.out.println(i+": "+Integer.toHexString(input.read()));
        }
         */
        byte[] buffer = new byte[sampleRate];
        long frameLength = 1000 * buffer.length / channels / 2 / sampleRate;
        if (frameLength * 2 * 2 * sampleRate != 1000 * buffer.length) {
            System.err.println("Warning " + (frameLength * 2 * 2 * sampleRate) + "!=" + (1000 * buffer.length));
        }
        long nextRead = System.currentTimeMillis() + frameLength;
        while (-1 != (read = input.read(buffer, count, buffer.length - count))) {
            count += read;
            if (count == buffer.length) {
                byte[] header = Utility.stringToByteArray("<data time=\""+"123"+"\" nbEch=\"" + (buffer.length / 4) + "\" />\r\n");
                byte[] message = new byte[header.length + buffer.length];
                System.arraycopy(header, 0, message, 0, header.length);
                System.arraycopy(buffer, 0, message, header.length, buffer.length);
                service.sendToAllClients(audioOutputName, message);
                count = 0;
                nextRead += frameLength;
            }
            //System.out.println(input.available());
            while (System.currentTimeMillis() - nextRead < 0) {
                Thread.sleep(1);
            }
            /*
            while (input.available() < 16*1024) {
            }*/
        }
    }

}
