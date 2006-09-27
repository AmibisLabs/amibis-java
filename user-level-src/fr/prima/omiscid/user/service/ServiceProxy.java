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

package fr.prima.omiscid.user.service;

import java.util.Set;

import fr.prima.omiscid.user.connector.ConnectorListener;
import fr.prima.omiscid.user.exception.UnknownVariable;
import fr.prima.omiscid.user.variable.RemoteVariableChangeListener;
import fr.prima.omiscid.user.variable.VariableAccessType;

/**
 * @author Patrick Reignier (UJF/Gravir)
 * Local representation of a remote Bip service
 * To speed up request, the configuration of the
 * remote service is done when we connect to it and
 * create this proxy. If the remote service is dynamically modifying
 * its structure (adding new variables or connectors), it is necessary
 * to call the updateDescription method
 * @see ServiceProxy#updateDescription()
 */
public interface ServiceProxy {

	/**
	 * Returns the service name (as it appears in DNS-SD)
	 * @return the service name
	 */
	public String getName() ;

   /**
	 * Returns the list of variables
	 * @return the list of variables
	 */
	public Set<String> getVariables() ;

	/**
	 * Returns the list of connectors (input type)
	 * @return the list of connectors
	 */
	public Set<String> getInputConnectors() ;

    /**
     * Returns the list of connectors (output type)
     * @return the list of connectors
     */
    public Set<String> getOutputConnectors() ;

    /**
     * Returns the list of connectors (input-output type)
     * @return the list of connectors
     */
    public Set<String> getInputOutputConnectors() ;

    /**
     * Updates the local view of a remote bip service :
     * <ul>
     * <li> the list of variables
     * <li> the list of connectors
     * </ul>
     */
    public void updateDescription() ;

    /**
     * Host name where the remote service is located
     * @return the host name
     */
    public String getHostName() ;

    /**
     * The Peer Id of the remote bip service
     * @return the peer id
     */
    public int getPeerId() ;

    /**
     * Sets the new value of a remote variable
     * @param varName the name of the remote variable
     * @param value the value (String format)
     */
    public void setVariableValue(String varName, String value) ;

    /**
     * Gets the value of a remote variable
     * @param varName
     * @return the value
     */
    public String getVariableValue(String varName);

    /**
     * Gets the access type of a remote variable
     * @param varName
     * @return the access type of the variable
     */
    public VariableAccessType getVariableAccessType(String varName);

    /**
     * Registers a listener that will be triggered when the remote variable value will change
     * @param varName the remote variable that must be monitored
     * @param remoteVariableChangeListener the listener
     * @throws UnknownVariable the variable does not exist
     * @see #removeRemoteVariableChangeListener(String, RemoteVariableChangeListener)
     * @see RemoteVariableChangeListener
     */
    public void addRemoteVariableChangeListener(String varName, final  RemoteVariableChangeListener remoteVariableChangeListener)
       throws UnknownVariable;

    /**
     * Unregisters a listener on a remote variable value change
     * @param varName the name of the variable
     * @param remoteVariableChangeListener the listener
     * @throws UnknownVariable the variable does not exist
     * @see #addRemoteVariableChangeListener(String, RemoteVariableChangeListener)
     * @see RemoteVariableChangeListener
     */
    public void removeRemoteVariableChangeListener(String varName,
    																							   RemoteVariableChangeListener remoteVariableChangeListener)
        throws UnknownVariable;

    /**
     * Extract the connector name of a remote service from its peerId. This peerId can be obtained from a message
     * sent by the remote service.
     * @param peerId the remote connector perrId
     * @return the remote connector name
     * @see ConnectorListener#messageReceived(Service, String, fr.prima.omiscid.user.connector.Message)
     */
    public String findConnector(int peerId) ;

}
