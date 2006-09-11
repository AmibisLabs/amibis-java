package fr.prima.omiscid.control.interf;

public final class GlobalConstants {

    public static final String controlAnswerXMLTag = "controlAnswer";

    public static final String dnssdDefaultWorkingDomain = "_bip_dev_rem._tcp";
    public static final String dnssdWorkingDomainEnvironmentVariableName = "OMISCID_WORKING_DOMAIN";

    public static final String constantNameForPeerId = "id";
    public static final String constantNameForName = "name";
    public static final String constantNameForOwner = "owner";
    public static final String constantNameForClass = "class";
    public static final String variableNameForLock = "lock";
    public static final String defaultServiceClassValue = ".Void";
    public static final String keyForFullTextRecord = "desc";
    public static final String keyForFullTextRecordFull = "full";
    public static final String keyForFullTextRecordNonFull = "part";
    
    public static final String[] specialVariablesNames = new String[] {
        constantNameForOwner,
        constantNameForClass,
        variableNameForLock,
        constantNameForName,
    };

}
