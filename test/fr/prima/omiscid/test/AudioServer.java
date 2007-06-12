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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author emonet
 */
public class AudioServer {

    private final static String serviceName = "AudioRouter";
    private final static String audioOutputName = "AudioForMovies";
    private final static String titleVariableName = "Titre chanson";

    private Service service;

    public AudioServer(){
        if(!init()){
            System.err.println("error initialization.");
            System.exit(0);
        }
        process();
    }


    private final int nbSamples = 256;
    private final int nbChannels = 2;
    private final int sampleRate = 16000;
    private double timeLength = (nbSamples*1000/(double)sampleRate); //milliseconds
    //private double timeSample = (1000/(double)sampleRate); //milliseconds

    private byte data[] = new byte[2*nbChannels*nbSamples + 27];
    private final long startTime = System.currentTimeMillis();
    private long lastTime = System.nanoTime()/1000000;

    private void process(){
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
