package fr.prima.omiscid.control.interf;

public final class GlobalConstants {

    public static final String connectorTypeInputXMLTag = "input";
    public static final String connectorTypeOutputXMLTag = "output";
    public static final String connectorTypeInOutputXMLTag = "inoutput";

    public static final String variableAccessTypeConstant = "constant";
    public static final String variableAccessTypeRead= "read";
    public static final String variableAccessTypeReadWrite = "readWrite";

    public static final String dnssdDefaultWorkingDomain = "_bip._tcp";
    public static final String dnssdWorkingDomainEnvironmentVariableName = "OMISCID_WORKING_DOMAIN";

    public static final String constantNameForPeerId = "id";
    public static final String constantNameForOwner = "owner";
    public static final String constantNameForClass = "class";
    public static final String variableNameForLock = "lock";
    public static final String defaultServiceClassValue = ".Void";
    public static final String keyForFullTextRecord = "desc";
    public static final String keyForFullTextRecordFull = "full";
    public static final String keyForFullTextRecordNonFull = "part";

    public static final String prefixForInputInDnssd = "i/";
    public static final String prefixForOutputInDnssd = "o/";
    public static final String prefixForInoutputInDnssd = "d/";
    public static final String prefixForConstantInDnssd = "c/";
    public static final String prefixForReadOnlyVariableInDnssd = "r";
    public static final String prefixForReadWriteVariableInDnssd = "w";

}
