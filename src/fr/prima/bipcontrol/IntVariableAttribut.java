package fr.prima.bipcontrol ;

/**
 * Group an integer, and a variable description for bip service. Enable to maintain easily the value in the integer and the value in the description. For that purpose, the interger is accessed through methods as getIntValue, SetIntValue.
 * @see fr.prima.bipcontrol.VariableAttribut
 * @author  Sebastien Pesnel  Refactoring by Patrick Reignier
 */
public class IntVariableAttribut {
    /** the integer value */
    private int integerValue = 0;
    
    /** the variable description that is associated to the integer */
    private VariableAttribut variableAttribut = null;
    
    /** Build a new instance of IntVariableAttribut
     * @param variableAttribut the variable description
     * @param value the initial value for the integer */
    public IntVariableAttribut(VariableAttribut variableAttribut, int value) {
        this.variableAttribut = variableAttribut;
        this.variableAttribut.setType("integer");
        this.variableAttribut.setFormatDescription("decimal representation");
        if (value == integerValue)
            integerValue = value + 1;
        setIntValue(value);
    }
    
    /** @return the value of the integer */
    public int getIntValue() {
        return integerValue;
    }
    
    /** Change the value of the integer
     * Modify the variable description 
     * @param value the new value for the integer */
    public void setIntValue(int value) {
        if (integerValue != value) {
            integerValue = value;
            variableAttribut.setValueStr(Integer.toString(integerValue));
        }
    }
    
    /** Increment the value of the integer */
    public void incr() {
        setIntValue(integerValue + 1);
    }
    /** Decrement the value of the integer */
    public void decr() {
        setIntValue(integerValue - 1);
    }
}
