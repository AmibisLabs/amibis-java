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

package fr.prima.omiscid.control.interf;

import fr.prima.omiscid.control.VariableAttribute;

/**
 * Interface to implement to subscribe to the variable modifications in
 * VariableAttribute objects. When an implementation of this interface is given
 * to a VariableAttribute object, the method changeOccured is called each time
 * the value of the variable change, that is to say each time the method
 * {@link fr.prima.omiscid.control.VariableAttribute#setValueStr(String)} is
 * called.
 * 
 * @see fr.prima.omiscid.control.VariableAttribute
 * @see fr.prima.omiscid.control.VariableAttribute#addListenerChange(VariableChangeListener)
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
public interface VariableChangeListener {
    /**
     * This method is called when the value of a VariableAttribute object
     * changes (for the VariableAttribute object where this instance is
     * registered as listener on variable modification).
     * 
     * @param var
     *            the description of the variable which value has changed
     */
    public void variableChanged(VariableAttribute var);
}
