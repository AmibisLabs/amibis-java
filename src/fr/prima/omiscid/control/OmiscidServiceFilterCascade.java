package fr.prima.omiscid.control;

import java.util.Vector;

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
            addTest(filter);
        }
    }

    public synchronized void addTest(OmiscidServiceFilter w) {
        filtersList.add(w);
    }

    public synchronized void removeTest(OmiscidServiceFilter w) {
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
