/*
 * Created on May 3, 2006
 *
 */
package fr.prima.omiscid.user.exception;


public class MessageInterpretationException extends Exception {
    private static final long serialVersionUID = -2759945647536972704L;

    public MessageInterpretationException(Exception e) {
        super(e);
    }
}