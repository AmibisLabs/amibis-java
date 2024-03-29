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

import fr.prima.omiscid.user.connector.ConnectorListener;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.variable.LocalVariableListener;
import fr.prima.omiscid.user.variable.VariableAccessType;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/*- IGNORE -*/
public class AudioServer {

    private final static String serviceName = "AudioServer";
    private final static String audioOutputName = "audioStream";
    private final static String titleVariableName = "songTitle";

    private Service service;

    public AudioServer(){
        this(false);
    }
    public AudioServer(boolean startInBg) {
        if(!init()){
            System.err.println("error initialization.");
            System.exit(0);
        }
        Runnable process = new Runnable() {
            public void run() {
                process();
            }
        };
        if (startInBg) {
            new Thread(process).start();
        } else {
            process.run();
        }
    }


    private final int nbSamples = 256;
    private final int nbChannels = 2;
    private final int sampleRate = 16000;
    private double timeLength = (nbSamples*1000/(double)sampleRate); //milliseconds
    //private double timeSample = (1000/(double)sampleRate); //milliseconds
    private boolean stereo = false;

    private byte data[] = new byte[2*nbChannels*nbSamples + 27];
    private final long startTime = System.currentTimeMillis();
    private long lastTime = System.nanoTime()/1000000;

    private void process() {
        timeLength = (nbSamples*1000/(double)sampleRate);
        int nbsample_byte = 2*nbSamples;
        data = new byte[33+nbsample_byte];
        data[0] = '<';
        data[1] = 'd';
        data[2] = 'a';
        data[3] = 't';
        data[4] = 'a';
        data[5] = ' ';
        data[6] = 't';
        data[7] = 'i';
        data[8] = 'm';
        data[9] = 'e';
        data[10] = '=';
        data[11] = '"';
        data[12] = '1';
        data[13] = '2';
        data[14] = '3';
        data[15] = '"';
        data[16] = ' ';
        data[17] = 'n';
        data[18] = 'b';
        data[19] = 'E';
        data[20] = 'c';
        data[21] = 'h';
        data[22] = '=';
        data[23] = '"';
        data[24] = '2';
        data[25] = '5';
        data[26] = '6';
        data[27] = '"';
        data[28] = ' ';
        data[29] = '/';
        data[30] = '>';
        data[31] = '\r';
        data[32] = '\n';
        long current_time = 0;
        double coeff = .2*2*Math.PI/nbsample_byte;
        boolean first = true;
        int j=0;
        int delta = 1;
        double phase = 0;
        double oldPhase;
        while (true) {
            oldPhase = phase;
            current_time = System.nanoTime()/1000000;
            //System.out.println(timeLength);
            //System.out.println(current_time);
            //System.out.println(lastTime);
            if (first || (current_time - lastTime > timeLength)) {
                while (current_time - lastTime > 4*timeLength) {
                    // too much behind of schedule, just skip
                    lastTime += timeLength;
                }
                //double time = (double)(current_time - startTime);
                //System.out.println("init : time = " + time);
                int i;
                for (i = 33; i < nbsample_byte + 33; i += 2) {
                    phase = j*(i-33)*coeff + oldPhase;
                    short value = (short)((1<<12)*Math.sin(phase));

                    data[i] = (byte)(0x00FF & value);
                    data[i+1] = (byte)((0xFF00 & value)>>8);

                }
                phase = j*(i-33)*coeff + oldPhase;
                //System.out.println("end : time = " + time);
                if (!stereo) {
                    service.sendToAllClients(audioOutputName, data);
                } else {
                    byte[] stereoData = new byte[data.length+nbsample_byte];
                    System.arraycopy(data, 0, stereoData, 0, 33);
                    for (int k = 33; k < nbsample_byte + 33; k+=2) {
                        stereoData[k+(k-33)] = data[k];
                        stereoData[k+(k-33)+1] = data[k+1];
                        stereoData[k+(k-33)+2] = data[k];
                        stereoData[k+(k-33)+3] = data[k+1];
                    }
                    service.sendToAllClients(audioOutputName, stereoData);
                }


                if (delta<0){
                    if(j<100){
                        delta = -1;
                    }
                    if(j<10){
                        delta = 1;
                        j = 20;
                    }
                } else {
                    if(j>100){
                        delta = 4;
                    }
                    if(j>300){
                        delta = -4;
                        j = 300;
                    }
                }
                j+=delta;

                //lastTime = current_time;
                lastTime += timeLength;
                first = false;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {}
            }
        }
    }
    
    private void processOld() {
        data[0] = 'c';
        data[1] = 'h';
        data[2] = 'a';
        data[3] = 'n';
        data[4] = 'n';
        data[5] = 'e';
        data[6] = 'l';
        data[7] = 's';
        data[8] = ' ';
        data[9] = 'i';
        data[10] = 'n';
        data[11] = 'f';
        data[12] = 'o';
        data[13] = ' ';
        data[14] = ':';
        data[15] = ' ';
        data[16] = ' ';
        data[17] = '2';
        data[18] = ',';
        data[19] = ' ';
        data[20] = '0';
        data[21] = '0';
        data[22] = '2';
        data[23] = '5';
        data[24] = '6';

        //lastTime = startTime;
        long current_time = 0;
        int nbsample_byte = nbSamples * 2;
        double coeff = .2*2*Math.PI/nbsample_byte;
        boolean first = true;
        int j=0;
        int delta = 1;
        while (true) {
            current_time = System.nanoTime()/1000000;
            //System.out.println(timeLength);
            //System.out.println(current_time);
            //System.out.println(lastTime);
            if (first || (current_time - lastTime > timeLength)) {
                while (current_time - lastTime > 4*timeLength) {
                    // skip
                    lastTime += timeLength;
                }
                //double time = (double)(current_time - startTime);
                //System.out.println("init : time = " + time);
                for(int i = 27; i<nbsample_byte + 27; i+=2){

                    short value = (short)((1<<12)*Math.sin(j*(i-27)*coeff));

                    data[i] = (byte)(0x00FF & value);
                    data[i+1] = (byte)((0xFF00 & value)>>8);

                    //System.out.println(time + " " + value + " ("+data[i]+", " +data[i+1]+")");

                    data[i+nbsample_byte] = (byte)(0x00FF & value);
                    data[i+nbsample_byte+1] = (byte)((0xFF00 & value)>>8);

                    //time += timeSample;
                }
                //System.out.println("end : time = " + time);
                service.sendToAllClients(audioOutputName, data);


                if (delta<0){
                    if(j<100){
                        delta = -1;
                    }
                    if(j<10){
                        delta = 1;
                        j = 20;
                    }
                } else {
                    if(j>100){
                        delta = 4;
                    }
                    if(j>300){
                        delta = -4;
                        j = 300;
                    }
                }
                j+=delta;

                //lastTime = current_time;
                lastTime += timeLength;
                first = false;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {}
            }
        }
    }

    private boolean init(){
        try {
            service = fr.prima.omiscid.test.FactoryFactory.factory().create(serviceName);
            service.addVariable("NbSamples", "integer", "Number of samples by message for each channel", fr.prima.omiscid.user.variable.VariableAccessType.CONSTANT);
            service.setVariableValue("NbSamples", java.lang.Integer.toString(nbSamples));
            service.addVariable("SampleRate", "integer", "sample rate", fr.prima.omiscid.user.variable.VariableAccessType.CONSTANT);
            service.setVariableValue("SampleRate", java.lang.Integer.toString(sampleRate));
            service.addVariable("NbChannels", "integer", "Number of Channels per message", fr.prima.omiscid.user.variable.VariableAccessType.CONSTANT);
            service.setVariableValue("NbChannels", java.lang.Integer.toString(nbChannels));
            service.addVariable(titleVariableName, "integer", "Titre de la Chanson", fr.prima.omiscid.user.variable.VariableAccessType.READ);
            service.setVariableValue(titleVariableName, "nono le robot");
            service.addConnector(audioOutputName, "output for sound", fr.prima.omiscid.user.connector.ConnectorType.OUTPUT);
            service.start();
            return true;
        }
        catch (IOException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private String times(String string, int i) {
        String res = "";
        while (i --> 0) {
            res += string;
        }
        return res;
    }

    public static void main(String arg[]){
        new AudioServer();
    }

}
