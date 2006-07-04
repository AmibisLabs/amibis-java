package fr.prima.omiscid.control.interf;

public enum VariableAccessType {
    CONSTANT(GlobalConstants.variableAccessTypeConstant),
    READ(GlobalConstants.variableAccessTypeRead),
    READ_WRITE(GlobalConstants.variableAccessTypeReadWrite);
    
    private String stringDescription;

    private VariableAccessType(String stringDescription) {
        this.stringDescription = stringDescription;
    }

    public String getStringDescription() {
        return stringDescription;
    }
}
