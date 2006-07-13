/*
 * Created on 2006 uzt 13
 *
 */
package fr.prima.omiscid.control.filter;

import java.util.Vector;

import fr.prima.omiscid.control.OmiscidService;

public final class ServiceFilters {

    private static final class ServiceFilterCascade implements ServiceFilter {
        private final Vector<ServiceFilter> filters = new Vector<ServiceFilter>();
        private boolean isAnd = true;

        public ServiceFilterCascade(ServiceFilter... filters) {
            for (ServiceFilter filter : filters) {
                this.filters.add(filter);
            }
        }
        public ServiceFilterCascade(boolean isAnd, ServiceFilter... filters) {
            this(filters);
            this.isAnd = isAnd;
        }
        public synchronized boolean acceptService(ServiceProxy serviceProxy) {
            for (ServiceFilter filter : filters) {
                if (filter.isAGoodService(s) != isAnd) {
                    return !isAnd;
                }
            }
            return isAnd;
        }
    }

}
