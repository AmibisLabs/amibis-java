/*
 * Created on 2006 uzt 13
 *
 */
package fr.prima.omiscid.control.filter;

import java.util.Vector;

import fr.prima.omiscid.control.OmiscidService;

public interface ServiceFilter {
    boolean acceptService(ServiceProxy serviceProxy);
}
