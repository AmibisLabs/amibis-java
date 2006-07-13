/**
 * Copyright (c) 2006 INRIA/Université Joseph Fourrier/Université Pierre Mendès-France.  
 * O3MiSCID (aka OMiSCID) Software written by Sebastien Pesnel, Dominique
 * Vaufreydaz, Patrick Reigner, Remi Emonnet and Julien Letessier. 
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
**/

package fr.prima.omiscid.user.service.impl;

import java.util.HashMap;
import java.util.Set;

import fr.prima.omiscid.control.ControlClient;
import fr.prima.omiscid.control.OmiscidService;
import fr.prima.omiscid.control.VariableAttribute;
import fr.prima.omiscid.control.interf.VariableChangeListener;
import fr.prima.omiscid.user.exception.UnknownVariable;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.variable.RemoteVariableChangeListener;

/**
 * @author reignier
 *
 */
public class ServiceProxyImpl implements ServiceProxy {
	protected HashMap<String, HashMap<RemoteVariableChangeListener, VariableChangeListener>> remoteVariableListeners;
    protected ControlClient controlClient ;
    protected OmiscidService omiscidService ;

    /**
     * Constructs a new ServiceProxy.
     * @param omiscidService
     */
    public ServiceProxyImpl(OmiscidService omiscidService)
    {
        this.omiscidService = omiscidService ;
        remoteVariableListeners = new HashMap<String, HashMap<RemoteVariableChangeListener, VariableChangeListener>>();
        controlClient = omiscidService.initControlClient() ;
        updateDescription() ;
 //       omiscidService.closeControlClient() ;
    }

	/* (non-Javadoc)
	 * @see fr.prima.bip.service.BipServiceProxy#getVariables()
	 */
	synchronized  public Set<String> getVariables() {
       return controlClient.getVariableNamesSet() ;

	}

	/* (non-Javadoc)
	 * @see fr.prima.bip.service.BipServiceProxy#getInputConnectors()
	 */
	synchronized  public Set<String> getInputConnectors() {
		return controlClient.getInputNamesSet();
	}

    /* (non-Javadoc)
     * @see fr.prima.bip.service.BipServiceProxy#updateDescription()
     */
    synchronized  public void updateDescription() {
    		controlClient.queryGlobalDescription() ;
        controlClient.queryCompleteDescription() ;

    }

    /* (non-Javadoc)
     * @see fr.prima.bip.service.BipServiceProxy#getInputOutputConnectors()
     */
    synchronized  public Set<String> getInputOutputConnectors() {
        return controlClient.getInOutputNamesSet() ;
    }

    /* (non-Javadoc)
     * @see fr.prima.bip.service.BipServiceProxy#getOutputConnectors()
     */
    synchronized  public Set<String> getOutputConnectors() {
        return controlClient.getOutputNamesSet();
    }

	/**
	 * @return Returns the controlClient.
	 */
	synchronized  public ControlClient getControlClient() {
		return controlClient;
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
		controlClient.queryVariableModification(varName, value) ;
	}

	/* (non-Javadoc)
	 * @see fr.prima.omiscid.service.ServiceProxy#getVariableValue(java.lang.String)
	 */
	synchronized  public String getVariableValue(String varName) {
		controlClient.findVariable(varName).getValueStr();
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.prima.omiscid.service.ServiceProxy#addRemoteVariableChangeListener(String, fr.prima.omiscid.variable.RemoteVariableChangeListener)
	 */
	synchronized  public void addRemoteVariableChangeListener(String varName, final RemoteVariableChangeListener remoteVariableChangeListener) 
                    	throws UnknownVariable
     {
		VariableAttribute varAttr = controlClient.findVariable(varName);
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
				remoteVariableChangeListener.variableChanged(ServiceProxyImpl.this, var.generateValueMessage());
			}
		} ;
		
		listeners.put(remoteVariableChangeListener, variableChangeListener);
	}

	/* (non-Javadoc)
	 * @see fr.prima.omiscid.service.ServiceProxy#removeRemoteVariableChangeListener(String, fr.prima.omiscid.variable.RemoteVariableChangeListener)
	 */
	synchronized  public void removeRemoteVariableChangeListener(String varName, 
										RemoteVariableChangeListener remoteVariableChangeListener)
				throws UnknownVariable
	{
		VariableAttribute varAttr = controlClient.findVariable(varName);
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
			varAttr.removeListenerChange(varListener);
		}
	}

	/* (non-Javadoc)
	 * @see fr.prima.omiscid.service.ServiceProxy#findConnector(int)
	 */
	synchronized  public String findConnector(int peerId) {
		return controlClient.findConnector(peerId).getName();
	}

	/* (non-Javadoc)
	 * @see fr.prima.omiscid.service.ServiceProxy#getName()
	 */
	public String getName() {
		return controlClient.findVariable("name").getValueStr() ;
	}
}
