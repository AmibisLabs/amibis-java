package fr.prima.omiscid.control.interf;

import fr.prima.omiscid.control.VariableAttribute;

public interface VariableChangeQueryListener {

    boolean isAccepted(VariableAttribute currentVariable, String newValue);

}
