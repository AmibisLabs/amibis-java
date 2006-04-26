/*
 * Created on Mar 29, 2006
 *
 */
package fr.prima.omiscid.control;

public final class OmiscidServiceFilters {

    /** Test if a service has the good owner name */
    private static final class Name implements OmiscidServiceFilter {
        private String nameRegexp = null;
        public Name(String nameRegexp){
            this.nameRegexp = nameRegexp;            
        }
        public boolean isAGoodService(OmiscidService s) {
            return s.getSimplifiedName().matches(nameRegexp);
        }
    }
    /** Test if the host of the service has a good name */
    private static final class Host  implements OmiscidServiceFilter {
        private String hostNameRegexp = null;
        public Host(String hostnameRegexp){
            hostNameRegexp = hostnameRegexp;
        }
        public boolean isAGoodService(OmiscidService s){
            System.out.println(s.getHostName());
            return s.getHostName().matches(hostNameRegexp);                
        }
    }
//    /** Test if a service has the good owner name */
//    private static final class Owner implements OmiscidServiceFilter{
//        private String owner = null;
//        public Owner(String owner){ 
//            this.owner = owner;            
//        }
//        public boolean isAGoodService(OmiscidService s) {
//            byte[] b = s.getServiceInformation().getProperty("owner");                
//            return (b != null  && b.length != 0 && (new String(b)).equals(owner));
//        }
//    }
    /** Tests a value in the TXT record */
    private static final class KeyValue implements OmiscidServiceFilter {
        private String key;
        private String valueRegexp;
        public KeyValue(String key, String valueRegexp){
            this.key = key;
            this.valueRegexp = valueRegexp;
        }
        public boolean isAGoodService(OmiscidService s){
            byte[] b = s.getServiceInformation().getProperty(key);
            if (valueRegexp == null) {
                return b == null;
            } else {
                return (b != null  && (new String(b)).matches(valueRegexp));
            }
        }
    }

    public static OmiscidServiceFilter nameIs(String nameRegexp) {
        return new Name("^"+nameRegexp+"( \\(\\d+\\))?"+"$"); // @@@( \(\d+\))?@@@
    }
    public static OmiscidServiceFilter namePrefixIs(String prefixRegexp) {
        return new Name("^"+prefixRegexp+".*$");
    }
    // commented out because of trailing .local.
//    public static OmiscidServiceFilter hostIs(String hostnameRegexp) {
//        return new Host("^"+hostnameRegexp+"$");
//    }
    public static OmiscidServiceFilter hostPrefixIs(String prefixRegexp) {
        return new Host("^"+prefixRegexp+".*$");
    }
    public static OmiscidServiceFilter ownerIs(String ownerRegexp) {
        return new KeyValue( "owner", "^"+ownerRegexp+"$");
    }
    public static OmiscidServiceFilter keyPresent(String txtRecordKey) {
        return new KeyValue( txtRecordKey, ".*");
    }
    public static OmiscidServiceFilter keyValue(String txtRecordKey, String regexp) {
        return new KeyValue( txtRecordKey, regexp);
    }
    public static OmiscidServiceFilter and(OmiscidServiceFilter ... filters) {
        return new OmiscidServiceFilterCascade(filters);
    }

}
