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

    public static VariableAccessType fromDnssdValue(String propertyValue) {
        if (propertyValue.startsWith(CONSTANT.prefixInDnssd)) {
            return CONSTANT;
        } else if (propertyValue.startsWith(READ.prefixInDnssd)) {
            return READ;
        } else if (propertyValue.startsWith(READ_WRITE.prefixInDnssd)) {
            return READ_WRITE;
        }
        return null;
    }

    public static String realValueFromDnssdValue(String propertyValue) {
        VariableAccessType variableAccessType = fromDnssdValue(propertyValue);
        return variableAccessType == null ?
                null : propertyValue.replaceFirst(variableAccessType.getPrefixInDnssd(), "");
    }

//    public static VariableAccessType fromControlString(String accessTypeName) {
//        if (CONSTANT.stringDescription.equals(accessTypeName)) {
//            return CONSTANT;
//        } else if (READ.stringDescription.equals(accessTypeName)) {
//            return READ;
//        } else if (READ_WRITE.stringDescription.equals(accessTypeName)) {
//            return READ_WRITE;
//        }
//        return null;
//    }
}
