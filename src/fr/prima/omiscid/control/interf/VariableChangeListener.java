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
