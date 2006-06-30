package fr.prima.omiscid.control.filter;

import java.util.Vector;

import fr.prima.omiscid.control.OmiscidService;

/**
 * Enables to combine several tests (with an "and" operator).
 *
 * @author Sebastien Pesnel refactoring emonet
 */
public class OmiscidServiceFilterCascade implements OmiscidServiceFilter {

    private final Vector<OmiscidServiceFilter> filtersList = new Vector<OmiscidServiceFilter>();

    public OmiscidServiceFilterCascade() {
    }

    public OmiscidServiceFilterCascade(OmiscidServiceFilter... filters) {
        for (OmiscidServiceFilter filter : filters) {
            addFilter(filter);
        }
    }

    public synchronized void addFilter(OmiscidServiceFilter w) {
        filtersList.add(w);
    }

    public synchronized void removeFilter(OmiscidServiceFilter w) {
        filtersList.remove(w);
    }

    public synchronized boolean isAGoodService(OmiscidService s) {
        for (OmiscidServiceFilter filter : filtersList) {
            if (!filter.isAGoodService(s)) {
                return false;
            }
        }
        return true;
    }
}
