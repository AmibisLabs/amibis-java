package fr.prima.bipcontrol ;

/**
 * Interface to implements to subscribe to the variable modification 
 * in VariableAttribut objects.
 * 
 * When an implementation of this interface is given to a VariableAttribut object,
 * the method changeOccured is called each time the value of the variable change,
 * that is to say each time the method {@link fr.prima.bipcontrol.VariableAttribut#setValueStr(String)} is called.
 * 
 * @see fr.prima.bipcontrol.VariableAttribut
 * @see fr.prima.bipcontrol.VariableAttribut#addListenerChange(VariableChangeListener)
 * 
 * @author Sebastien Pesnel 
 * Refactoring by Patrick Reignier
 */
public interface VariableChangeListener {
    /**
     * This method is called when the value of a VariableAttribut object change 
     * (for the VariableAttribut object where this instance is registered as listener on variable modification)
     * @param var the variable description where the value has changed
     */
    public void changeOccured(VariableAttribut var);
}
