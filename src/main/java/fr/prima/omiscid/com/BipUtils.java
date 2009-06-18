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

package fr.prima.omiscid.com;

import java.util.Random;


/**
 * Utility class containing BIP related constants and some Com Layer tools.
 * See also {@link fr.prima.omiscid.user.util.Utility} for more user oriented and xml tools.
 */
public final class BipUtils {

    /** characters found at the beginning of the message header */
    public static final byte[] messageBegin = { 'B', 'I', 'P', '/', '1', '.', '0', ' ' };

    /** string found at the end of the header */
    public static final byte[] headerEnd = { '\r', '\n' };

    /** string found at the end of the message */
    public static final byte[] messageEnd = { '\r', '\n' };

    private static Random randomForThisJVM = new Random(System.currentTimeMillis());

    /**
     * Generates an id for a BIP peer based on a random number and the current
     * time. Warning!!! If two jvms init their variables at the same
     * currentTimeMillis and call generateServiceId at the same
     * currentTimeMillis there *will* be a problem. Note that this is virtually
     * not guaranteed that this id is unique.
     *
     * @return a new id for a BIP peer
     */
    public static int generateBIPPeerId() {
        // System.out.println(Thread.currentThread().getId() + " , "+
        // Thread.currentThread().getName() + " , "+
        // Thread.currentThread().getThreadGroup().getName() + ": " +
        // randomForThisJVM);
        int partTime = (int) (System.currentTimeMillis() & 0x0000FF00);
        // The last byte is reserved for connector index while in the context of a service
        double r = randomForThisJVM.nextDouble();
        int partRandom = ((int) (r * 0xEFFFFFFF) & 0xFFFF0000);
        return partTime + partRandom;
        // return (int) (System.currentTimeMillis() & 0xFFFFFFFF);
    }

}
