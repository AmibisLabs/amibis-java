/*
 * Created on Apr 28, 2006
 *
 */
package fr.prima.omiscid.com;

import java.util.Random;

import fr.prima.omiscid.user.util.Utility;

/**
 * Utility class containing BIP related constants and some Com Layer tools.
 * See also {@link Utility} for more user oriented and xml tools.
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
