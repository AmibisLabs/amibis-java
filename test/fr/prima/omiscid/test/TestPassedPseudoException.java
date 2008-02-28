/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.omiscid.test;

/**
 *
 * @author emonet
 */
public class TestPassedPseudoException extends RuntimeException {

    public TestPassedPseudoException() {
        super("Test passed, this is an execption to be expected by junit");
    }

    
}
