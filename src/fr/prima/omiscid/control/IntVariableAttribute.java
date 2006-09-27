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

package fr.prima.omiscid.control;

/**
 * Groups an integer and a variable description for an OMiSCID service. Enables
 * to maintain easily the value in the integer and the value in the description.
 * 
 * @see fr.prima.omiscid.control.VariableAttribute
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
public class IntVariableAttribute {
    /** the integer value */
    private int integerValue = 0;

    /** the variable description that is associated to the integer */
    private VariableAttribute variableAttribute = null;

    /**
     * Creates a new instance of IntVariableAttribute.
     * 
     * @param variableAttribute
     *            the variable description
     * @param value
     *            the initial value for the integer
     */
    public IntVariableAttribute(VariableAttribute variableAttribute, int value) {
        this.variableAttribute = variableAttribute;
        this.variableAttribute.setType("integer");
        this.variableAttribute.setFormatDescription("decimal representation");
        if (value == integerValue) {
            integerValue = value + 1; // forces the following setIntValue to
                                        // trigger value change
        }
        setIntValue(value);
    }

    /**
     * @return the value of the integer
     */
    public int getIntValue() {
        return integerValue;
    }

    /**
     * Sets the value of the integer. Modify the variable description
     * accordingly (if necessary).
     * 
     * @param value
     *            the new value for the integer
     */
    public void setIntValue(int value) {
        if (integerValue != value) {
            integerValue = value;
            variableAttribute.setValueStr(Integer.toString(integerValue));
        }
    }

    /**
     * Increments the value of the integer
     */
    public void increment() {
        setIntValue(integerValue + 1);
    }

    /**
     * Decrements the value of the integer
     */
    public void decrement() {
        setIntValue(integerValue - 1);
    }
}
