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
import java.util.Set;

import fr.prima.omiscid.control.OmiscidService;
import fr.prima.omiscid.control.VariableAttribute;
import fr.prima.omiscid.control.interf.VariableChangeListener;
import fr.prima.omiscid.user.exception.UnknownVariable;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.variable.RemoteVariableChangeListener;
import fr.prima.omiscid.user.variable.VariableAccessType;

/**
 * @author reignier
 *
 */
public class ServiceProxyImpl implements ServiceProxy {
	protected HashMap<String, HashMap<RemoteVariableChangeListener, VariableChangeListener>> remoteVariableListeners;
    protected OmiscidService omiscidService ;

    
    /**/
    private static class ProxyInfo {
        long timeout;
        ServiceProxyImpl serviceProxyImpl;
    }
    private static HashMap<ServiceImpl, HashMap<Integer, ProxyInfo>> proxyForService = new HashMap<ServiceImpl, HashMap<Integer,ProxyInfo>>();
    public static ServiceProxyImpl forService(ServiceImpl owner, OmiscidService omiscidService) {
        HashMap<Integer, ProxyInfo> proxies = proxyForService.get(owner);
        if (proxies == null) {
            proxies = new HashMap<Integer, ProxyInfo>();
            proxyForService.put(owner, proxies);
        }
        ProxyInfo proxyInfo = proxies.get(omiscidService.getRemotePeerId());
        if (proxyInfo == null) {
            proxyInfo = new ProxyInfo();
        }
        long now = System.currentTimeMillis();
        if (proxyInfo == null || proxyInfo.timeout < now) {
            proxyInfo.serviceProxyImpl = new ServiceProxyImpl(omiscidService);
            proxyInfo.timeout = Long.MAX_VALUE; // ignoring timeout for now //now + ???;
            proxies.put(omiscidService.getRemotePeerId(), proxyInfo);
        }
        return proxyInfo.serviceProxyImpl;
//        return new ServiceProxyImpl(omiscidService);
    }
    /**/
    
    /**
     * Constructs a new ServiceProxy.
     * @param omiscidService
     */
    private ServiceProxyImpl(OmiscidService omiscidService)
    {
        this.omiscidService = omiscidService ;
        remoteVariableListeners = new HashMap<String, HashMap<RemoteVariableChangeListener, VariableChangeListener>>();
        updateDescription() ;
    }

	/* (non-Javadoc)
	 * @see fr.prima.bip.service.BipServiceProxy#getVariables()
	 */
	synchronized  public Set<String> getVariables() {
       return omiscidService.getVariableNamesSet() ;

	}

	/* (non-Javadoc)
	 * @see fr.prima.bip.service.BipServiceProxy#getInputConnectors()
	 */
	synchronized  public Set<String> getInputConnectors() {
		return omiscidService.getInputNamesSet();
	}

    /* (non-Javadoc)
     * @see fr.prima.bip.service.BipServiceProxy#updateDescription()
     */
    synchronized  public void updateDescription() {
        omiscidService.updateDescription() ;
    }

    /* (non-Javadoc)
     * @see fr.prima.bip.service.BipServiceProxy#getInputOutputConnectors()
     */
    synchronized  public Set<String> getInputOutputConnectors() {
        return omiscidService.getInOutputNamesSet() ;
    }

    /* (non-Javadoc)
     * @see fr.prima.bip.service.BipServiceProxy#getOutputConnectors()
     */
    synchronized  public Set<String> getOutputConnectors() {
        return omiscidService.getOutputNamesSet();
    }

	synchronized OmiscidService getOmiscidService() {
        return omiscidService;
    }

    /* (non-Javadoc)
	 * @see fr.prima.bip.service.BipServiceProxy#getHostName()
	 */
	synchronized  public String getHostName() {
		// TODO Auto-generated method stub
		return omiscidService.getHostName();
	}

	/* (non-Javadoc)
	 * @see fr.prima.bip.service.BipServiceProxy#getPeerId()
	 */
	synchronized  public int getPeerId() {
		// TODO Auto-generated method stub
		return omiscidService.getRemotePeerId();
	}

	synchronized  public String toString()
	{
		return omiscidService.toString() + " " + getInputOutputConnectors() ;
	}

	/* (non-Javadoc)
	 * @see fr.prima.bip.service.BipServiceProxy#setVariableValue(java.lang.String, java.lang.String)
	 */
	synchronized  public void setVariableValue(String varName, String value) {
        omiscidService.queryVariableModification(varName, value) ;
	}

    /* (non-Javadoc)
     * @see fr.prima.omiscid.service.ServiceProxy#getVariableValue(java.lang.String)
     */
    synchronized  public String getVariableValue(String varName) {
        return omiscidService.findVariable(varName).getValueStr();
    }

    /* (non-Javadoc)
     * @see fr.prima.omiscid.service.ServiceProxy#getVariableValue(java.lang.String)
     */
    synchronized  public VariableAccessType getVariableAccessType(String varName) {
        return omiscidService.findVariable(varName).getAccess();
    }

	/* (non-Javadoc)
	 * @see fr.prima.omiscid.service.ServiceProxy#addRemoteVariableChangeListener(String, fr.prima.omiscid.variable.RemoteVariableChangeListener)
	 */
	synchronized  public void addRemoteVariableChangeListener(String varName, final RemoteVariableChangeListener remoteVariableChangeListener)
                    	throws UnknownVariable
     {
		VariableAttribute varAttr = omiscidService.findVariable(varName);
		if (varAttr == null)
			// the variable does not exist
		{
			throw new UnknownVariable("Unknown variable : "  + varName);
		}

		HashMap<RemoteVariableChangeListener, VariableChangeListener> listeners =
			remoteVariableListeners.get(varName);
		if (listeners == null)
			// we have not registered listeners for this variable yet
		{
			listeners = new HashMap< RemoteVariableChangeListener, VariableChangeListener>();
			remoteVariableListeners.put(varName, listeners);
		}

		VariableChangeListener variableChangeListener = new VariableChangeListener()
		{
			public void variableChanged(VariableAttribute var) {
				remoteVariableChangeListener.variableChanged(ServiceProxyImpl.this, var.getValueStr());
			}
		} ;
		listeners.put(remoteVariableChangeListener, variableChangeListener);
        omiscidService.subscribe(varName, variableChangeListener);
	}

	/* (non-Javadoc)
	 * @see fr.prima.omiscid.service.ServiceProxy#removeRemoteVariableChangeListener(String, fr.prima.omiscid.variable.RemoteVariableChangeListener)
	 */
	synchronized  public void removeRemoteVariableChangeListener(String varName,
										RemoteVariableChangeListener remoteVariableChangeListener)
				throws UnknownVariable
	{
		VariableAttribute varAttr = omiscidService.findVariable(varName);
		if (varAttr == null)
			// the variable does not exist
		{
			throw new UnknownVariable("Unknown variable : "  + varName);
		}

		HashMap<RemoteVariableChangeListener, VariableChangeListener> listeners =
			remoteVariableListeners.get(varName);

		if (listeners != null)
		{
			VariableChangeListener varListener = listeners.get(remoteVariableChangeListener);
            omiscidService.unsubscribe(varName, varListener);
		}
	}

	/* (non-Javadoc)
	 * @see fr.prima.omiscid.service.ServiceProxy#findConnector(int)
	 */
	synchronized  public String findConnector(int peerId) {
		return omiscidService.findConnector(peerId).getName();
	}

	/* (non-Javadoc)
	 * @see fr.prima.omiscid.service.ServiceProxy#getName()
	 */
	public String getName() {
        return omiscidService.getSimplifiedName();
	}
}
