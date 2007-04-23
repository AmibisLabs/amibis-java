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

package fr.prima.omiscid.user.variable;

import fr.prima.omiscid.user.service.Service;


/**
 * @author reignier
 *
 */
public interface LocalVariableListener {

    /**
     * This method is called when the value of a variable
     * changes
     *
     * @param service the service owning the variable
     * @param name the variable name
     * @param value the new variable value
     */
    public void variableChanged(Service service, String name, String value);
    
    /**
     * This method is called when a new value is requested on a variable.
     * This method must check that this new value is a valid value.
     * A listener throwing an exception in the #isValid call will be considered
     * as agreeing the change/returning true.
     *
     * @param service the service owning the variable
     * @param currentValue the current value of the variable
     * @param newValue the new requested value
     * @return true if the new value is accepted, false if rejected.
     */
    public boolean isValid(Service service, String currentValue, String newValue);
}
