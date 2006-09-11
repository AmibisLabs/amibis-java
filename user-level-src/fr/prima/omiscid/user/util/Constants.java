package fr.prima.omiscid.user.util;

public final class Constants {

    public static final String connectorTypeInputXMLTag = "input";
    public static final String connectorTypeOutputXMLTag = "output";
    public static final String connectorTypeInOutputXMLTag = "inoutput";

    public static final String variableAccessTypeConstant = "constant";
    public static final String variableAccessTypeRead= "read";
    public static final String variableAccessTypeReadWrite = "readWrite";

    public static final String prefixForInputInDnssd = "i/";
    public static final String prefixForOutputInDnssd = "o/";
    public static final String prefixForInoutputInDnssd = "d/";
    public static final String prefixForConstantInDnssd = "c/";
    public static final String prefixForReadOnlyVariableInDnssd = "r";
    public static final String prefixForReadWriteVariableInDnssd = "w";
    public static final String valueForLongConstantsInDnssd = "c";

}
