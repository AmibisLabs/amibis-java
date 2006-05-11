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
