/*
 * Created on Mar 29, 2006
 *
 */
package fr.prima.omiscid.control.filter;

import fr.prima.omiscid.control.OmiscidService;

/**
 * Utility class. Provides some {@link OmiscidServiceFilter} creators for
 * classical requirements.
 */
public final class OmiscidServiceFilters {

    /**
     * Tests whether a service has the good owner name
     */
    private static final class Name implements OmiscidServiceFilter {
        private String nameRegexp = null;

        public Name(String nameRegexp) {
            this.nameRegexp = nameRegexp;
        }

        public boolean isAGoodService(OmiscidService s) {
            return s.getSimplifiedName().matches(nameRegexp);
        }
    }

    /**
     * Tests whether the host of the service has a good name
     */
    private static final class Host implements OmiscidServiceFilter {
        private String hostNameRegexp = null;

        public Host(String hostnameRegexp) {
            hostNameRegexp = hostnameRegexp;
        }

        public boolean isAGoodService(OmiscidService s) {
            System.out.println(s.getHostName());
            return s.getHostName().matches(hostNameRegexp);
        }
    }

    /**
     * Tests whether a value in the TXT record is ok
     */
    private static final class KeyValue implements OmiscidServiceFilter {
        private String key;

        private String valueRegexp;

        public KeyValue(String key, String valueRegexp) {
            this.key = key;
            this.valueRegexp = valueRegexp;
        }

        public boolean isAGoodService(OmiscidService s) {
            byte[] b = s.getServiceInformation().getProperty(key);
            if (valueRegexp == null) {
                return b == null;
            } else {
                return (b != null && (new String(b)).matches(valueRegexp));
            }
        }
    }

    public static String baseNameRegexp(String nameRegexp) {
        return "^" + nameRegexp + "( \\(\\d+\\))?" + "$";
    }

    /**
     * Tests whether the service name (with possible trailing dnssd number
     * removed).
     *
     * @param nameRegexp
     * @return
     */
    public static OmiscidServiceFilter nameIs(String nameRegexp) {
        return new Name(baseNameRegexp(nameRegexp)); // @@@( \(\d+\))?@@@
    }

    public static OmiscidServiceFilter namePrefixIs(String prefixRegexp) {
        return new Name("^" + prefixRegexp + ".*$");
    }

    // commented out because of trailing .local.
    // public static OmiscidServiceFilter hostIs(String hostnameRegexp) {
    // return new Host("^"+hostnameRegexp+"$");
    // }
    public static OmiscidServiceFilter hostPrefixIs(String prefixRegexp) {
        return new Host("^" + prefixRegexp + ".*$");
    }

    public static OmiscidServiceFilter ownerIs(String ownerRegexp) {
        return new KeyValue("owner", "^" + ownerRegexp + "$");
    }

    public static OmiscidServiceFilter keyPresent(String txtRecordKey) {
        return new KeyValue(txtRecordKey, ".*");
    }

    public static OmiscidServiceFilter keyValue(String txtRecordKey, String regexp) {
        return new KeyValue(txtRecordKey, regexp);
    }

    public static OmiscidServiceFilter and(OmiscidServiceFilter... filters) {
        return new OmiscidServiceFilterCascade(filters);
    }

}
