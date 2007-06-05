/**
 * Copyright© 2005-2006 INRIA/Université Pierre Mendès-France/Université Joseph Fourier.
 *
 * O3MiSCID (aka OMiSCID) Software written by Sebastien Pesnel, Dominique
 * Vaufreydaz, Patrick Reignier, Remi Emonet and Julien Letessier.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package fr.prima.omiscid.user.service.impl;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import fr.prima.omiscid.control.Attribute;
import fr.prima.omiscid.control.InOutputAttribute;
import fr.prima.omiscid.control.OmiscidService;
import fr.prima.omiscid.control.VariableAttribute;
import fr.prima.omiscid.control.interf.VariableChangeListener;
import fr.prima.omiscid.user.exception.UnknownConnector;
import fr.prima.omiscid.user.exception.UnknownVariable;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.util.Utility;
import fr.prima.omiscid.user.variable.RemoteVariableChangeListener;
import fr.prima.omiscid.user.variable.VariableAccessType;

/**
 * @author reignier
 *
 */
public class ServiceProxyImpl implements ServiceProxy {
    protected HashMap<String, HashMap<RemoteVariableChangeListener, VariableChangeListener>> remoteVariableListeners;
    protected OmiscidService omiscidService;
    
    
    /**/
    private static class ProxyInfo {
        long timeout;
        ServiceProxyImpl serviceProxyImpl;
    }
    // Hastable is synchronized
    private static Hashtable<ServiceImpl, Map<Integer, ProxyInfo>> proxyForService = new Hashtable<ServiceImpl, Map<Integer,ProxyInfo>>();
    public static ServiceProxyImpl forService(ServiceImpl owner, OmiscidService omiscidService) {
        Map<Integer, ProxyInfo> proxies;
        synchronized (proxyForService) {
            proxies = proxyForService.get(owner);
            if (proxies == null) {
                proxies = new Hashtable<Integer, ProxyInfo>();
                proxyForService.put(owner, proxies);
            }
        }
        ProxyInfo proxyInfo;
        synchronized (proxies) {
            proxyInfo = proxies.get(omiscidService.getRemotePeerId());
            if (proxyInfo == null) {
                proxyInfo = new ProxyInfo();
                proxies.put(omiscidService.getRemotePeerId(), proxyInfo);
            }
        }
        synchronized (proxyInfo) {
            long now = System.currentTimeMillis();
            if (proxyInfo.timeout < now) {
                try {
                    proxyInfo.serviceProxyImpl = new ServiceProxyImpl(omiscidService);
                    proxyInfo.timeout = Long.MAX_VALUE; // ignoring timeout for now //now + ???;
                } catch (RuntimeException e) {
                    proxyInfo.serviceProxyImpl = null;
                    proxyInfo.timeout = now;
                }
            }
        }
        return proxyInfo.serviceProxyImpl;
//        return new ServiceProxyImpl(omiscidService);
    }
    /**/
    
    /**
     * Constructs a new ServiceProxy.
     * @param omiscidService
     */
    private ServiceProxyImpl(OmiscidService omiscidService) {
        this.omiscidService = omiscidService;
        remoteVariableListeners = new HashMap<String, HashMap<RemoteVariableChangeListener, VariableChangeListener>>();
    }
    
    /* (non-Javadoc)
     * @see fr.prima.bip.service.BipServiceProxy#getVariables()
     */
    public Set<String> getVariables() {
        return omiscidService.getVariableNamesSet();
        
    }
    
    /* (non-Javadoc)
     * @see fr.prima.bip.service.BipServiceProxy#getInputConnectors()
     */
    public Set<String> getInputConnectors() {
        return omiscidService.getInputNamesSet();
    }
    
    /* (non-Javadoc)
     * @see fr.prima.bip.service.BipServiceProxy#updateDescription()
     */
    public void updateDescription() {
        checkedUpdateDescription();
    }
    public boolean checkedUpdateDescription() {
        return omiscidService.updateDescription();
    }
    /* (non-Javadoc)
     * @see fr.prima.bip.service.BipServiceProxy#getInputOutputConnectors()
     */
    public Set<String> getInputOutputConnectors() {
        return omiscidService.getInOutputNamesSet();
    }
    
    /* (non-Javadoc)
     * @see fr.prima.bip.service.BipServiceProxy#getOutputConnectors()
     */
    public Set<String> getOutputConnectors() {
        return omiscidService.getOutputNamesSet();
    }
    
    OmiscidService getOmiscidService() {
        return omiscidService;
    }
    
    /* (non-Javadoc)
     * @see fr.prima.bip.service.BipServiceProxy#getHostName()
     */
    public String getHostName() {
        return omiscidService.getHostName();
    }

    /* (non-Javadoc)
     * @see fr.prima.bip.service.BipServiceProxy#getPeerId()
     */
    public int getPeerId() {
        return omiscidService.getRemotePeerId();
    }

    /* (non-Javadoc)
     * @see fr.prima.bip.service.BipServiceProxy#getPeerId()
     */
    public String getPeerIdAsString() {
        return Utility.intTo8HexString(getPeerId()).toLowerCase();
    }

    public String toString() {
        return omiscidService.toString() + " " + getInputOutputConnectors();
    }

    /* (non-Javadoc)
     * @see fr.prima.bip.service.BipServiceProxy#setVariableValue(java.lang.String, java.lang.String)
     */
    public void setVariableValue(String varName, String value) throws UnknownVariable {
        getVariable(varName); // This checks for the variable existence
        omiscidService.queryVariableModification(varName, value);
    }
    
    /* (non-Javadoc)
     * @see fr.prima.omiscid.service.ServiceProxy#getVariableValue(java.lang.String)
     */
    public String getVariableValue(String varName) throws UnknownVariable {
        //    return getVariable(varName).getValueStr();
        return omiscidService.getVariableValue(getVariable(varName).getName());
    }
    
    /* (non-Javadoc)
     * @see fr.prima.omiscid.service.ServiceProxy#getVariableValue(java.lang.String)
     */
    public VariableAccessType getVariableAccessType(String varName) throws UnknownVariable {
        return getVariable(varName).getAccess();
    }
    
    /* (non-Javadoc)
     * @see fr.prima.omiscid.service.ServiceProxy#addRemoteVariableChangeListener(String, fr.prima.omiscid.variable.RemoteVariableChangeListener)
     */
    synchronized public void addRemoteVariableChangeListener(String varName, final RemoteVariableChangeListener remoteVariableChangeListener)
            throws UnknownVariable {
        getVariable(varName); // This checks for the variable existence
        
        HashMap<RemoteVariableChangeListener, VariableChangeListener> listeners =
                remoteVariableListeners.get(varName);
        if (listeners == null)
            // we have not registered listeners for this variable yet
        {
            listeners = new HashMap< RemoteVariableChangeListener, VariableChangeListener>();
            remoteVariableListeners.put(varName, listeners);
        }

        VariableChangeListener variableChangeListener = new VariableChangeListener() {
            public void variableChanged(VariableAttribute var) {
                remoteVariableChangeListener.variableChanged(ServiceProxyImpl.this, var.getName(), var.getValueStr());
            }
        };
        listeners.put(remoteVariableChangeListener, variableChangeListener);
        omiscidService.subscribe(varName, variableChangeListener);
    }

    /* (non-Javadoc)
     * @see fr.prima.omiscid.service.ServiceProxy#removeRemoteVariableChangeListener(String, fr.prima.omiscid.variable.RemoteVariableChangeListener)
     */
    synchronized public void removeRemoteVariableChangeListener(String varName,
            RemoteVariableChangeListener remoteVariableChangeListener)
            throws UnknownVariable {
        getVariable(varName); // This checks for the variable existence

        HashMap<RemoteVariableChangeListener, VariableChangeListener> listeners =
                remoteVariableListeners.get(varName);

        if (listeners != null) {
            VariableChangeListener varListener = listeners.get(remoteVariableChangeListener);
            omiscidService.unsubscribe(varName, varListener);
        }
    }
    
    private VariableAttribute getVariable(String varName) throws UnknownVariable {
        VariableAttribute varAttr = omiscidService.findVariable(varName);
        if (varAttr == null) {
            throw new UnknownVariable("Unknown variable: '"  + varName+"'");
        }
        return varAttr;
    }
    
    /* (non-Javadoc)
     * @see fr.prima.omiscid.service.ServiceProxy#findConnector(int)
     */
    public String findConnector(int peerId) throws UnknownConnector {
        return getConnector(peerId).getName();
    }
    
    private Attribute getConnector(int peerId) throws UnknownConnector {
        InOutputAttribute connector = omiscidService.findConnector(peerId);
        if (connector == null) {
            throw new UnknownConnector("Unknown connector with peer id: '"+peerId+"'");
        }
        return connector;
    }
    /* (non-Javadoc)
     * @see fr.prima.omiscid.service.ServiceProxy#getName()
     */
    public String getName() {
        return omiscidService.getSimplifiedName();
    }
}
