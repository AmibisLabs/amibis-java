package fr.prima.omiscid.control.interf;

public enum VariableAccessType {
    CONSTANT(
            GlobalConstants.variableAccessTypeConstant,
            GlobalConstants.prefixForConstantInDnssd
            ),
    READ(
            GlobalConstants.variableAccessTypeRead,
            GlobalConstants.prefixForReadOnlyVariableInDnssd
            ),
    READ_WRITE(
            GlobalConstants.variableAccessTypeReadWrite,
            GlobalConstants.prefixForReadWriteVariableInDnssd);

    private String stringDescription;
    private String prefixInDnssd;

    private VariableAccessType(String stringDescription, String prefixInDnssd) {
        this.stringDescription = stringDescription;
        this.prefixInDnssd = prefixInDnssd;
    }

    public String getStringDescription() {
        return stringDescription;
    }

    public String getPrefixInDnssd() {
        return prefixInDnssd;
    }
}
