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

import fr.prima.omiscid.user.connector.Message;
import java.io.IOException;
import java.util.HashMap;

import fr.prima.omiscid.user.connector.ConnectorListener;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.exception.ConnectionRefused;
import fr.prima.omiscid.user.exception.ConnectorAlreadyExisting;
import fr.prima.omiscid.user.exception.ConnectorLimitReached;
import fr.prima.omiscid.user.exception.IncorrectConnectorType;
import fr.prima.omiscid.user.exception.ServiceRunning;
import fr.prima.omiscid.user.exception.UnknownConnector;
import fr.prima.omiscid.user.exception.UnknownVariable;
import fr.prima.omiscid.user.exception.VariableAlreadyExisting;
import fr.prima.omiscid.user.exception.WrongVariableAccessType;
import fr.prima.omiscid.user.variable.LocalVariableListener;
import fr.prima.omiscid.user.variable.VariableAccessType;

/**
 * @author Patrick Reignier (UJF/Gravir)
 *
 */
public interface Service {
    
    /**
     * Adds a new connector to the service.
     * @param connectorName the name of the connector
     * @param connectorDescription the description of the connector
     * @param connectorKind connector type. This can be input, output or input-output
     * @throws ConnectorAlreadyExisting thrown if we try to recreate an already existing connector
     * @throws VariableAlreadyExisting thrown if there is already a variable with this name
     * @throws IOException thrown if there is an error in the tcp socket creation
     * @throws ServiceRunning It is not possible to add a new connector if the service is already running. You have to stop it before
     * @throws ConnectorLimitReached when no more connectors can be added to the service.
     */
    public void addConnector(String connectorName,
            String connectorDescription,
            ConnectorType connectorKind) throws ConnectorAlreadyExisting, VariableAlreadyExisting, IOException, ServiceRunning, ConnectorLimitReached;
    
    /**
     * Adds a new connector to the service specifying a preferred tcp port for it.
     * @param connectorName the name of the connector
     * @param connectorDescription the description of the connector
     * @param connectorKind connector type. This can be input, output or input-output
     * @param port the preferred port number
     * @throws ConnectorAlreadyExisting thrown if we try to recreate an already existing connector
     * @throws VariableAlreadyExisting thrown if there is already a variable with this name
     * @throws IOException thrown if there is an error in the tcp socket creation (e.g. port already in use)
     * @throws ServiceRunning It is not possible to add a new connector if the service is already running. You have to stop it before
     * @throws ConnectorLimitReached when no more connectors can be added to the service.
     */
    public void addConnector(String connectorName,
            String connectorDescription,
            ConnectorType connectorKind, int port) throws ConnectorAlreadyExisting, VariableAlreadyExisting, IOException, ServiceRunning, ConnectorLimitReached;
    
    /**
     * Add a message listener to a connector
     * @param connectorName the name of the connector
     * @param msgListener the object that will handle messages sent to this connector
     * @throws UnknownConnector thrown if the service has not declared this connector
     */
    public void addConnectorListener(final String connectorName,
            ConnectorListener msgListener) throws UnknownConnector;
    
    /**
     * Registers the service in  DNS-SD. The service is now running and exposed to the other services.
     * <BR>
     * <B>Caution :</B> when the service is started, it is no more possible to add new connectors or variables.
     */
    public void start();
    
    /**
     * Stops the service : closes all the connections and unregisters the service
     */
    public void stop();
    
    
    /**
     * Sends a message to a particular client. This client is identified by its Peer id (pid).
     * This method is usually used to answer a request coming from another service that
     * has requested a connexion with us. We know this service from its pid inside its request message. We
     * do not have a ServiceProxy for it because we have not found this service to initiate the connexion.
     * @param connectorName the name of the connector that will send the message
     * @param msg the message to send
     * @param pid peer id : the identification of the client that must receive the message
     * @param unreliableButFastSend not implemented yet
     * @throws UnknownConnector thrown if the service has not declared this connector
     * @see Service#sendToOneClient(String, byte[], ServiceProxy)
     */
    public void sendToOneClient(String connectorName, byte[] msg, int pid, boolean unreliableButFastSend)
    throws UnknownConnector;
    
    /**
     * Sends a message to a particular client. This client is identified by its Peer id (pid).
     * This method is usually used to answer a request coming from another service that
     * has requested a connexion with us. We know this service from its pid inside its request message. We
     * do not have a ServiceProxy for it because we have not found this service to initiate the connexion.
     * Defaults to reliable send.
     * @param connectorName the name of the connector that will send the message
     * @param msg the message to send
     * @param pid peer id : the identification of the client that must receive the message
     * @throws UnknownConnector thrown if the service has not declared this connector
     * @see Service#sendToOneClient(String, byte[], ServiceProxy)
     */
    public void sendToOneClient(String connectorName, byte[] msg, int pid)
    throws UnknownConnector;
    
    /**
     * Sends a message to a particular client. This client is identified by ServiceProxy because
     * we have been looking for it to create the connexion.
     * @param connectorName the name of the connector that will send the message
     * @param msg the message to send
     * @param serviceProxy : the proxy of the remote service
     * @param unreliableButFastSend not implemented yet
     * @throws UnknownConnector thrown if the service has not declared this connector
     * @see Service#sendToOneClient(String, byte[], int)
     */
    public void sendToOneClient(String connectorName, byte[] msg, ServiceProxy serviceProxy, boolean unreliableButFastSend)
    throws UnknownConnector;
    
    /**
     * Sends a message to a particular client. This client is identified by ServiceProxy because
     * we have been looking for it to create the connexion.
     * Defaults to reliable send.
     * @param connectorName the name of the connector that will send the message
     * @param msg the message to send
     * @param serviceProxy : the proxy of the remote service
     * @throws UnknownConnector thrown if the service has not declared this connector
     * @see Service#sendToOneClient(String, byte[], int)
     */
    public void sendToOneClient(String connectorName, byte[] msg, ServiceProxy serviceProxy)
    throws UnknownConnector;
    
    /**
     * Sends a message to all the clients connected to the service.
     * @param connectorName the name of the connector sending the message
     * @param msg the message to send
     * @param unreliableButFastSend not implemented yet
     * @throws UnknownConnector thrown if the service has not declared this connector
     */
    public void sendToAllClients(String connectorName, byte[] msg, boolean unreliableButFastSend)
    throws UnknownConnector;
    
    /**
     * Sends a message to all the clients connected to the service.
     * Defaults to reliable send.
     * @param connectorName the name of the connector sending the message
     * @param msg the message to send
     * @throws UnknownConnector thrown if the service has not declared this connector
     */
    public void sendToAllClients(String connectorName, byte[] msg)
    throws UnknownConnector;

    /**
     * 
     * Allows to specify on which connector to send the answer.
     * @param connectorName the name of the connector sending the message
     * @param msg the message to send
     * @param message the message to reply to
     * @param unreliableButFastSend not implemented yet
     * @throws UnknownConnector thrown if the service has not declared this connector
     */
    public void sendReplyToMessage(String connectorName, byte[] msg, Message message, boolean unreliableButFastSend)
    throws UnknownConnector;
    
    /**
     * Sends a message back to the sender of a message just received.
     * Allows to specify on which connector to send the answer.
     * Defaults to reliable send.
     * @param connectorName the name of the connector sending the message
     * @param msg the message to send
     * @param message the message to reply to
     * @throws UnknownConnector thrown if the service has not declared this connector
     */
    public void sendReplyToMessage(String connectorName, byte[] msg, Message message)
    throws UnknownConnector;

    /**
     * Sends a message back to the sender of a message just received.
     * The message is sent on the connector on which the message to reply to
     * was received.
     * @param msg the message to send
     * @param message the message to reply to
     * @param unreliableButFastSend not implemented yet
     * @throws UnknownConnector thrown if the service has not declared this connector
     */
    public void sendReplyToMessage(byte[] msg, Message message, boolean unreliableButFastSend);
    
    /**
     * Sends a message back to the sender of a message just received.
     * Defaults to reliable send.
     * The message is sent on the connector on which the message to reply to
     * was received.
     * @param msg the message to send
     * @param message the message to reply to
     * @throws UnknownConnector thrown if the service has not declared this connector
     */
    public void sendReplyToMessage(byte[] msg, Message message);

    /**
     * Sets the value of a service variable
     * @param varName the variable name
     * @param varValue the variable value
     * @throws UnknownVariable thrown if the variable has not been created
     * @see Service#addVariable
     */
    public void setVariableValue(String varName, String varValue)
    throws UnknownVariable, WrongVariableAccessType;
    
    /**
     * Returns the variable value
     * @param varName the variable name
     * @return the variable value
     * @throws UnknownVariable thrown if the variable has not been created
     * @see Service#addVariable
     */
    public String getVariableValue(String varName)
    throws UnknownVariable;
    
    /**
     * Creates a new variable for this service.
     * @param varName the variable name
     * @param type the variable type (or null if no type is associated)
     * @param description the variable description
     * @param accessType the access type of the variable
     * @throws VariableAlreadyExisting thrown if a variable with the same name has already been declated.
     * @throws ServiceRunning It is not possible to add a new connector if the service is already running. You have to stop it before.
     *
     */
    public void addVariable(String varName, String type, String description, VariableAccessType accessType)
    throws ConnectorAlreadyExisting, VariableAlreadyExisting, ServiceRunning;
    
    /**
     * Associate a description to an existing variable
     * @param varName the var name
     * @param varDescription the description
     * @throws UnknownVariable thrown if the variable has not been created
     */
    public void setVariableDescription(String varName, String varDescription)
    throws UnknownVariable;
    
    /**
     * Returns the description associated to a variable
     * @param varName the variable name
     * @return the description
     * @throws UnknownVariable thrown if the variable has not been created
     * @see Service#addVariable(String, String, String, VariableAccessType)
     */
    public String getVariableDescription(String varName)
    throws UnknownVariable;
    
    /**
     * Returns the variable access type
     * @param varName the variable name
     * @return the access type (String version)
     * @throws UnknownVariable thrown if the variable has not been decladed
     * @see Service#addVariable(String, String, String, VariableAccessType)
     */
    public String getVariableAccessType(String varName)
    throws UnknownVariable;
    
    /**
     * Returns the string version of the variable type
     * @param varName the variable name
     * @return the variable type
     * @throws UnknownVariable thrown if the variable has not been declared
     * @see Service#addVariable(String, String, String, VariableAccessType)
     */
    public String getVariableType(String varName)
    throws UnknownVariable;
    
    /**
     * Adds a listener that will be triggered at every variable change. The listener must
     * first check the new variable value
     *  (see  {@link LocalVariableListener#isValid(Service, String, String)})
     *  It will then be notified when the value has changed (see {@link LocalVariableListener#variableChanged(Service,String,String)})
     * @param varName the varName
     * @param listener the listener
     * @throws UnknownVariable thrown if the variable has not been declared
     * @see Service#addVariable(String, String, String, VariableAccessType)
     */
    public void addLocalVariableListener(String varName, final LocalVariableListener listener)
    throws UnknownVariable;
    
    /**
     * Removes a listener on a variable change
     * @param varName the varName
     * @param listener the listener
     * @throws UnknownVariable thrown if the variable has not been declared
     * @see Service#addVariable(String, String, String, VariableAccessType)
     */
    public void removeLocalVariableListener(String varName, LocalVariableListener listener)
    throws UnknownVariable;
    
    /**
     * Removes a connector listener
     * @param connector the connector name
     * @param listener the connector listener
     * @throws UnknownConnector the connector does not exist
     */
    public void removeConnectorListener(String connector, ConnectorListener listener) throws UnknownConnector;
    
    /**
     * Connects a local connector to a remote connector of a remote service
     * @param localConnector
     * @param proxy the proxy of the remote service
     * @param remoteConnector the name of the remote connector on the remote service
     * @throws UnknownConnector thrown if one of the connector does not exist
     * @throws IncorrectConnectorType thrown if the coonnectors cannot connect:
     * for instance : trying to connect an input connector on another input connector.
     * @throws ConnectionRefused when connection is refused by remote host. This
     * can because of network problem.
     */
    public void connectTo(String localConnector, ServiceProxy proxy, String remoteConnector)
    throws UnknownConnector, IncorrectConnectorType, ConnectionRefused;
    
    /**
     * 
     * @param localConnector
     * @param hostName the host name of the remote service
     * @param tcpPort the port number for the remote connector
     * @throws fr.prima.omiscid.user.exception.UnknownConnector
     * @throws fr.prima.omiscid.user.exception.ConnectionRefused thrown in case of connection problem
     */
    public void connectTo(String localConnector, String hostName, int tcpPort)
            throws UnknownConnector, ConnectionRefused;
    
    /**
     * Finds a service on the network.
     * @param filter the filter that specifies the service that we search
     * @return the service Proxy
     * @see ServiceProxy
     */
    public ServiceProxy findService(ServiceFilter filter);
    
    /**
     * Finds a list of services on the network.
     * @param filters the filters that specifies the service that we search
     * @return the list of associated services proxy
     * @see ServiceProxy
     */
    public HashMap<ServiceFilter, ServiceProxy> findServices(ServiceFilter[] filters);
    
    /**
     * Finds a service on the network.
     * @param filter the filter that specifies the service that we search.
     * @param timeout maximum delay (in ms) to find the resquest service.
     * @return the service Proxy or null if the search timed out
     * @see ServiceProxy
     */
    public ServiceProxy findService(ServiceFilter filter, long timeout);
    
    /**
     * Finds a list of services on the network.
     * @param filters the filters that specifies the list of services that we search. Each filter in the array is associated to a requested service.
     * @param timeout maximum delay (in ms) to find all the requested services.
     * @return the list of associated services proxy
     * @see ServiceProxy
     */
    public HashMap<ServiceFilter, ServiceProxy> findServices(ServiceFilter[] filters, long timeout);
    
    /**
     * The peer identifier of the local service.
     * @return the peer id
     */
    public int getPeerId();

    /**
     * The peer identifier of the local service as a hexadecimal string.
     * The peer identifier is formated as a 8 character long string
     * containing the lower case hexadecimal representation of the integer peer id.
     * @return the peer id
     */
    public String getPeerIdAsString();

    /**
     * Closes a connection on a given connector to a given remote peer.
     * @param localConnector the connector on which connection will be closed
     * @param peerId the id of the remote peer which connection is to be closed
     * @return whether the connection existed and have been closed
     */
    public boolean closeConnection(String localConnector, int peerId) throws UnknownConnector;
    
    /**
     * Closes a connection on a given connector to a given remote peer.
     * @param localConnector the connector on which connection will be closed
     * @param serviceProxy the ServiceProxy which connection is to be closed
     * @return whether the connection existed and have been closed
     */
    public boolean closeConnection(String localConnector, ServiceProxy serviceProxy) throws UnknownConnector;

    /**
     * Closes all connections on a given connector.
     * @param localConnector the connector on which connections will be closed
     */
    public void closeAllConnections(String localConnector) throws UnknownConnector;
    
    /**
     * Closes all connections on all connectors.
     */
    public void closeAllConnections();
    
    /**
     * Removes all listeners on a given connector.
     * @param localConnector the connector on which listeners will be removed.
     */
    public void removeAllConnectorListeners(String localConnector) throws UnknownConnector;
    
    /**
     * Removes all listeners on all connectors.
     */
    public void removeAllConnectorListeners();
}
