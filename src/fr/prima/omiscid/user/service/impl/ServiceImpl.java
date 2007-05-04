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

import fr.prima.omiscid.user.exception.MessageInterpretationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map.Entry;

import fr.prima.omiscid.com.TcpClientServer;
import fr.prima.omiscid.com.interf.BipMessageListener;
import fr.prima.omiscid.control.ControlServer;
import fr.prima.omiscid.control.InOutputAttribute;
import fr.prima.omiscid.control.OmiscidService;
import fr.prima.omiscid.control.VariableAttribute;
import fr.prima.omiscid.control.WaitForOmiscidServices;
import fr.prima.omiscid.control.filter.OmiscidServiceFilter;
import fr.prima.omiscid.control.interf.VariableChangeListener;
import fr.prima.omiscid.control.interf.VariableChangeQueryListener;
import fr.prima.omiscid.user.connector.ConnectorListener;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.exception.ConnectionRefused;
import fr.prima.omiscid.user.exception.ConnectorAlreadyExisting;
import fr.prima.omiscid.user.exception.IncorrectConnectorType;
import fr.prima.omiscid.user.exception.ServiceRunning;
import fr.prima.omiscid.user.exception.UnknownConnector;
import fr.prima.omiscid.user.exception.UnknownVariable;
import fr.prima.omiscid.user.exception.VariableAlreadyExisting;
import fr.prima.omiscid.user.exception.WrongVariableAccessType;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFilter;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.util.Utility;
import fr.prima.omiscid.user.variable.LocalVariableListener;
import fr.prima.omiscid.user.variable.VariableAccessType;
import org.w3c.dom.Element;

/**
 * @author Patrick Reignier (UJF/Gravir)
 *
 */
public class ServiceImpl implements Service {
    
    private static class VariableListenerBridge {
        public VariableChangeListener variableChangeListener ;
        public VariableChangeQueryListener variableChangeQueryListener ;
    }
    private static class MessageOnConnector implements Message {
        private Message message;
        private String connectorName;
        MessageOnConnector(Message message, String connectorName) {
            this.message = message;
            this.connectorName = connectorName;
        }
        public String getBufferAsString() throws MessageInterpretationException {
            return message.getBufferAsString();
        }
        public String getBufferAsStringUnchecked() {
            return message.getBufferAsStringUnchecked();
        }
        public Element getBufferAsXML() throws MessageInterpretationException {
            return message.getBufferAsXML();
        }
        public Element getBufferAsXMLUnchecked() {
            return message.getBufferAsXMLUnchecked();
        }
        public byte[] getBuffer() {
            return message.getBuffer();
        }
        public int getPeerId() {
            return message.getPeerId();
        }
        public int getMessageId() {
            return message.getMessageId();
        }
    }
    private static Message wrap(Message message, String connectorName) {
        return new MessageOnConnector(message, connectorName);
    }
    private static String connector(Message message) {
        return ((MessageOnConnector)message).connectorName;
    }
    
    private Object lock = new Object() ;
    
    /* the Bip Control Server : this is the heart of the bip service */
    private ControlServer ctrlServer ;
    
    private boolean started = false ;
    
    /** The input output type for each connector of each service */
    private HashMap<String, TcpClientServer>  tcpClientServers ;
    
    private HashMap<String,HashMap<LocalVariableListener,  VariableListenerBridge> > variableListeners ;
    
    /** Association between a connector name and an associated listener */
    private HashMap<String, HashMap<ConnectorListener, BipMessageListener> > msgListeners ;
    
    public ServiceImpl(ControlServer ctrlServer) {
        this.ctrlServer = ctrlServer ;
        tcpClientServers = new HashMap<String, TcpClientServer>() ;
        variableListeners = new HashMap<String, HashMap<LocalVariableListener, VariableListenerBridge> >() ;
        msgListeners = new HashMap<String, HashMap<ConnectorListener, BipMessageListener> >() ;
        
        for (InOutputAttribute inoutput : ctrlServer.getConnectors()) {
            tcpClientServers.put(inoutput.getName(), (TcpClientServer) inoutput.getCommunicationServer());
        }
    }
    
    public int getPeerId() {
        return ctrlServer.getPeerId();
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#addConnector(java.lang.String, fr.prima.omiscid.control.interf.ChannelType)
         */
    synchronized  public void addConnector(String connectorName,
            String connectorDescription, ConnectorType connectorKind) throws ConnectorAlreadyExisting, IOException, ServiceRunning {
        
        if (started)
            throw new ServiceRunning("addConnector");
        
        InOutputAttribute ioa = null;
        TcpClientServer tcpClientServer = null ;
        
        synchronized (lock) {
            boolean alreadyExisting = true ;
            try {
                tcpClientServer = getTcpClientServer(connectorName) ;
            } catch (UnknownConnector e) {
                // TODO Auto-generated catch block
                alreadyExisting = false ;
            }
            
            if (alreadyExisting)
                throw new ConnectorAlreadyExisting("Connector already defined : " + connectorName) ;
            else {
                tcpClientServer = new fr.prima.omiscid.com.TcpClientServer(ctrlServer.getPeerId());
                tcpClientServers.put(connectorName, tcpClientServer) ;
                ioa = ctrlServer.addInOutput(connectorName, tcpClientServer, connectorKind);
                ioa.setDescription(connectorDescription);
                tcpClientServer.start() ;
            }
        }
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#addConnectorListener(java.lang.String, fr.prima.omiscid.com.interf.BipMessageListener)
         */
    synchronized  public void addConnectorListener(final String connectorName,
            final ConnectorListener msgListener) throws UnknownConnector {
        TcpClientServer tcpClientServer = getTcpClientServer(connectorName) ;
        BipMessageListener bipMessageListener = new BipMessageListener() {
            public void connected(int peerId) {
                msgListener.connected(ServiceImpl.this ,connectorName, peerId);
            }
            
            public void disconnected(int peerId) {
                msgListener.disconnected(ServiceImpl.this, connectorName, peerId);
            }
            
            public void receivedBipMessage(Message message) {
                msgListener.messageReceived(ServiceImpl.this, connectorName, wrap(message, connectorName)) ;
            }
        };
        
        tcpClientServer.addBipMessageListener(bipMessageListener) ;
        
        synchronized (lock) {
            HashMap<ConnectorListener, BipMessageListener> listeners = msgListeners.get(connectorName) ;
            if (listeners == null) {
                listeners = new HashMap<ConnectorListener, BipMessageListener>() ;
                msgListeners.put(connectorName, listeners) ;
            }
            
            listeners.put(msgListener, bipMessageListener) ;
        }
    }
    
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#start()
         */
    synchronized  public void start() {
        synchronized (lock) {
            if(ctrlServer.startServer(0)){
                //Thread process msg
                ctrlServer.startProcessMessagesThread();
                started = true ;
            }
        }
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#stop()
         */
    synchronized  public void stop() {
        started = false ;
        
        // we first close all the tcpServers
        Iterator<TcpClientServer> it = tcpClientServers.values().iterator() ;
        while (it.hasNext()) {
            TcpClientServer tcpClientServer = it.next() ;
            tcpClientServer.close() ;
        }
        ctrlServer.stop() ;
        
        // to avoid on an oscar update command to stop and start the bundle
        // too quickly. If stop and start are too close, DNSSD does not see the
        // unregistration and the new registration :-(
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
        }
        
        // we remove all the connectors listeners
        for (String connectorName : msgListeners.keySet()) {
            HashMap<ConnectorListener, BipMessageListener> map = msgListeners.get(connectorName);
            Vector<ConnectorListener> setClone = new Vector<ConnectorListener>();
            for (ConnectorListener listener : map.keySet()) {
                setClone.add(listener);
            }
            
            for (ConnectorListener listener : setClone) {
                try {
                    removeConnectorListener(connectorName, listener) ;
                } catch (UnknownConnector e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        // we remove all the variables listeners
        for (String varName : variableListeners.keySet()) {
            HashMap<LocalVariableListener, VariableListenerBridge> listeners = new HashMap<LocalVariableListener, VariableListenerBridge>();
            listeners.putAll(variableListeners.get(varName));
//			HashMap<LocalVariableListener, VariableListenerBridge> listeners =	             (HashMap<LocalVariableListener, VariableListenerBridge>) variableListeners.get(varName).clone();
            for (LocalVariableListener listener : listeners.keySet()) {
                try {
                    removeLocalVariableListener(varName, listener);
                } catch (UnknownVariable e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#sendToAllClients(java.lang.String, byte[], boolean)
         */
    public void sendToAllClients(String connectorName, byte[] msg, boolean unreliableButFastSend) throws UnknownConnector {
        TcpClientServer tcpClientServer = getTcpClientServer(connectorName) ;
        if (tcpClientServer == null)
            throw new UnknownConnector("Unknown bip connector : " + connectorName) ;
        
        tcpClientServer.sendToAllClients(msg) ;
    }
    
    public void sendToAllClients(String connectorName, byte[] msg) throws UnknownConnector {
        sendToAllClients(connectorName, msg, false);
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#sendToOneClient(java.lang.String, byte[], int, boolean)
         */
    public void sendToOneClient(String connectorName, byte[] msg, int pid, boolean unreliableButFastSend) throws UnknownConnector {
        TcpClientServer tcpClientServer = getTcpClientServer(connectorName) ;
        
        if (tcpClientServer == null)
            throw new UnknownConnector("Unknown bip connector : " + connectorName) ;
        
        tcpClientServer.sendToOneClient(msg, pid) ;
    }
    
    public void sendToOneClient(String connectorName, byte[] msg, int pid) throws UnknownConnector {
        sendToOneClient(connectorName, msg, pid, false);
    }
    
    public void sendReplyToMessage(String connectorName, byte[] msg, Message message, boolean unreliableButFastSend) throws UnknownConnector {
        sendToOneClient(connectorName, msg, message.getPeerId(), unreliableButFastSend);
    }

    public void sendReplyToMessage(String connectorName, byte[] msg, Message message) throws UnknownConnector {
        sendToOneClient(connectorName, msg, message.getPeerId());
    }

    public void sendReplyToMessage(byte[] msg, Message message, boolean unreliableButFastSend) {
        String connectorName = connector(message);
        sendReplyToMessage(connectorName, msg, message, unreliableButFastSend);
    }

    public void sendReplyToMessage(byte[] msg, Message message) {
        String connectorName = connector(message);
        sendReplyToMessage(connectorName, msg, message);
    }

    /**
     * Finds a tcpServer from the service reference and the connector name
     * @param connectorName the connector name
     * @param kind the inpout-output kind of the connector
     * @return the tcp client server
     * @throws UnknownConnector invalid connector name
     */
    private TcpClientServer getTcpClientServer(String connectorName) throws UnknownConnector {
        TcpClientServer tcpClientServer = null ;
        synchronized (lock) {
            tcpClientServer = tcpClientServers.get(connectorName) ;
            if (tcpClientServer == null)
                // the tcpServer might exist any way if it has been directly created by
                // a service.xml description file
            {
                InOutputAttribute attr = null ;
                attr = ctrlServer.findInOutput(connectorName, null) ;
                if (attr != null) {
                    tcpClientServer = (TcpClientServer) attr.getCommunicationServer() ;
                    if (tcpClientServer == null)
                        // first access to the service.xml created connector : we have to associate if a
                        // valid TcpServer
                    {
                        try {
                            tcpClientServer = new fr.prima.omiscid.com.TcpClientServer(ctrlServer.getPeerId());
                            tcpClientServer.start() ;
                            attr.setCommunicationServer(tcpClientServer) ;
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else
                    throw new UnknownConnector("Unknown Bip Connector " + connectorName) ;
                tcpClientServers.put(connectorName, tcpClientServer) ;
            }
        }
        return tcpClientServer;
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#addVariable(java.lang.String,java.lang.String, java.lang.String)
         */
    synchronized  public void addVariable(String varName, String type, String description, VariableAccessType accessType) throws VariableAlreadyExisting, ServiceRunning {
        
        if (started) {
            throw new ServiceRunning("addVariable");
        }
        
        if (ctrlServer.findVariable(varName) != null)
            // the variable already exists
            throw new VariableAlreadyExisting("Variable already declared : " + varName) ;
        ctrlServer.addVariable(varName) ;
        VariableAttribute var = ctrlServer.findVariable(varName) ;
        var.setDescription(description);
        var.setType(type) ;
        var.setAccessType(accessType);
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#getVariableDescription(java.lang.String)
         */
    synchronized  public String getVariableDescription(String varName) throws UnknownVariable {
        VariableAttribute var = getVariableAttribut(varName) ;
        return var.getDescription() ;
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#getVariableValue(java.lang.String)
         */
    synchronized  public String getVariableValue(String varName) throws UnknownVariable {
        VariableAttribute var = getVariableAttribut(varName) ;
        return var.getValueStr() ;
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#setVariableDescription(java.lang.String, java.lang.String)
         */
    synchronized  public void setVariableDescription(String varName, String varDescription) throws UnknownVariable {
        VariableAttribute var = getVariableAttribut(varName) ;
        var.setDescription(varDescription) ;
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#setVariableValue(java.lang.String, java.lang.String)
         */
    synchronized  public void setVariableValue(String varName, String varValue) throws UnknownVariable, WrongVariableAccessType {
        VariableAttribute var = getVariableAttribut(varName) ;
        if (var.getAccess() == VariableAccessType.CONSTANT && this.started) {
            throw new WrongVariableAccessType("Variable ("+varName+") is constant and cannot be modified when service is started");
        } else {
            var.setValueStr(varValue) ;
        }
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#getVariableType(java.lang.String)
         */
    synchronized  public String getVariableType(String varName) throws UnknownVariable {
        VariableAttribute var = getVariableAttribut(varName) ;
        return var.getType() ;
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#getVariableAccessType(java.lang.String)
         */
    synchronized  public String getVariableAccessType(String varName) throws UnknownVariable {
        VariableAttribute var = getVariableAttribut(varName) ;
        return var.getAccessString() ;
    }
    
    /**
     * Search for a variable
     * @param varName the variable name
     * @return the variable attribute
     * @throws UnknownVariable thrown if the variable is undeclared
     */
    protected VariableAttribute getVariableAttribut(String varName)
    throws UnknownVariable {
        VariableAttribute var = ctrlServer.findVariable(varName) ;
        if (var == null)
            throw new UnknownVariable("Undeclared variable : " + varName) ;
        return var ;
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#addLocalVariableListener(java.lang.String, fr.prima.omiscid.variable.LocalVariableListener)
         */
    synchronized  public void addLocalVariableListener(String varName, final LocalVariableListener listener) throws UnknownVariable {
        
        synchronized (lock) {
            VariableAttribute var = getVariableAttribut(varName) ;
            
            HashMap<LocalVariableListener, VariableListenerBridge> listeners = variableListeners.get(varName) ;
            if (listeners == null)
                // no listeners yet for this variable : we have to create the vector
            {
                listeners = new HashMap<LocalVariableListener, VariableListenerBridge>() ;
                variableListeners.put(varName, listeners);
            }
            
            // bridge between VariableListener et VariableChangeListener
            VariableChangeListener omiscidListener = new VariableChangeListener() {
                public void variableChanged(VariableAttribute variableAttribute) {
                    listener.variableChanged(ServiceImpl.this, variableAttribute.getName(),variableAttribute.getValueStr() );
                }
            } ;
            
            // bridge between VariableListener et VariableChangeQueryListener
            VariableChangeQueryListener queryListener = new VariableChangeQueryListener() {
                public boolean isAccepted(VariableAttribute currentVariable, String newValue) {
                    // TODO Auto-generated method stub
                    return listener.isValid(ServiceImpl.this, currentVariable.getName(), currentVariable.getValueStr(), newValue) ;
                }
            };
            
            VariableListenerBridge bridge = new VariableListenerBridge();
            
            bridge.variableChangeListener = omiscidListener;
            bridge.variableChangeQueryListener = queryListener;
            
            var.addListenerChange(omiscidListener);
            ctrlServer.addVariableChangeQueryListener(queryListener);
            listeners.put(listener, bridge);
        }
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#removeLocalVariableListener(java.lang.String, fr.prima.omiscid.LocalVariableListener)
         */
    synchronized  public void removeLocalVariableListener(String varName, LocalVariableListener listener) throws UnknownVariable {
        synchronized (lock) {
            VariableAttribute var = getVariableAttribut(varName) ;
            HashMap<LocalVariableListener, VariableListenerBridge> listeners = variableListeners.get(varName);
            if (listeners != null) {
                VariableListenerBridge bridge = listeners.get(listener);
                var.removeListenerChange(bridge.variableChangeListener);
                ctrlServer.removeVariableChangeQueryListener(bridge.variableChangeQueryListener);
                listeners.remove(listener);
            }
        }
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#connectTo(java.lang.String, fr.prima.omiscid.service.ServiceProxy, java.lang.String)
         */
    synchronized  public void connectTo(String localConnector, ServiceProxy proxy, String remoteConnector)
    throws UnknownConnector, IncorrectConnectorType, ConnectionRefused {
        // we first check if we can find the connectors
        TcpClientServer localClientServer = null ;
        try {
            localClientServer = getTcpClientServer(localConnector) ;
        } catch (UnknownConnector e) {
            throw new UnknownConnector("Unknown local connector : " + localConnector) ;
        }
        
        ConnectorType remoteKind = null ;
        OmiscidService omiscidService = ((ServiceProxyImpl) proxy).getOmiscidService() ;
        InOutputAttribute remoteAttribute = omiscidService.findOutput(remoteConnector) ;
        if (remoteAttribute == null) {
            remoteAttribute = omiscidService.findInOutput(remoteConnector);
            if (remoteAttribute == null) {
                remoteAttribute = omiscidService.findInput(remoteConnector);
                if (remoteAttribute == null)
                    // the connector does not exist
                    throw new UnknownConnector(Utility.intTo8HexString(this.getPeerId()) + " : Unknown remote connector : " + remoteConnector + " on "+Utility.intTo8HexString(proxy.getPeerId())) ;
                else
                    remoteKind = ConnectorType.INPUT ;
            } else
                remoteKind = ConnectorType.INOUTPUT;
        } else
            remoteKind = ConnectorType.OUTPUT ;
        
        // we check the connector's type
        if (remoteKind == ConnectorType.INPUT)
            // we check if the local connector is output or inputOutput
        {
            InOutputAttribute localAttr = ctrlServer.findInOutput(localConnector, ConnectorType.INPUT) ;
            if (localAttr != null)
                // this is not the good type
                throw new IncorrectConnectorType("Cannot connect two input connectors : " + localConnector + " to " + remoteConnector) ;
        } else if (remoteKind == ConnectorType.OUTPUT) {
            InOutputAttribute localAttr = ctrlServer.findInOutput(localConnector, ConnectorType.OUTPUT) ;
            if (localAttr != null)
                // this is not the good type
                throw new IncorrectConnectorType("Cannot connect two output connectors : " + localConnector + " to " + remoteConnector) ;
        }
        
        try {
            localClientServer.connectTo(proxy.getHostName(), remoteAttribute.getTcpPort());
        } catch(Exception e) {
            throw new ConnectionRefused(e);
        }
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#sendToOneClient(java.lang.String, byte[], fr.prima.omiscid.service.ServiceProxy, boolean)
         */
    synchronized  public void sendToOneClient(String connectorName, byte[] msg, ServiceProxy serviceProxy,boolean unreliableButFastSend) throws UnknownConnector {
        for (String remoteConnectorName : serviceProxy.getInputConnectors()) {
            int remotePeerId = ((ServiceProxyImpl)serviceProxy).getOmiscidService().findInput(remoteConnectorName).getPeerId();
            sendToOneClient(connectorName, msg, remotePeerId,unreliableButFastSend) ;
        };
        for (String remoteConnectorName : serviceProxy.getInputOutputConnectors()) {
            int remotePeerId = ((ServiceProxyImpl)serviceProxy).getOmiscidService().findInOutput(remoteConnectorName).getPeerId();
            sendToOneClient(connectorName, msg, remotePeerId,unreliableButFastSend) ;
        };
    }
    synchronized  public void sendToOneClient(String connectorName, byte[] msg, ServiceProxy serviceProxy) throws UnknownConnector {
        sendToOneClient(connectorName, msg, serviceProxy, false);
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#removeConnectorListener(String, fr.prima.omiscid.connector.ConnectorListener)
         */
    synchronized  public void removeConnectorListener(String connector, ConnectorListener listener)throws UnknownConnector {
        TcpClientServer tcpClientServer = getTcpClientServer(connector) ;
        synchronized (lock) {
            HashMap<ConnectorListener, BipMessageListener> listeners = msgListeners.get(connector) ;
            BipMessageListener bipMessageListener = listeners.get(listener);
            tcpClientServer.removeBIPMessageListener(bipMessageListener);
            listeners.remove(listener) ;
        }
    }
    
    synchronized  public String toString() {
        return ctrlServer.getServiceName() + " : " + Integer.toHexString(ctrlServer.getPeerId()) ;
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#findService(fr.prima.omiscid.control.filter.OmiscidServiceFilter)
         */
    public ServiceProxy findService(ServiceFilter filter) {
        ServiceFilter[] filters = {filter};
        
        return findServices(filters).get(filter);
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#findServices(fr.prima.omiscid.control.filter.OmiscidServiceFilter[])
         */
    public HashMap<ServiceFilter, ServiceProxy> findServices(ServiceFilter[] filters) {
        return findServices(filters,-1);
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#findService(fr.prima.omiscid.control.filter.OmiscidServiceFilter, long)
         */
    public ServiceProxy findService(ServiceFilter filter, long timeout) {
        ServiceFilter[] filters = {filter};
        
        return findServices(filters,timeout).get(filter);
    }
    
        /* (non-Javadoc)
         * @see fr.prima.omiscid.service.Service#findServices(fr.prima.omiscid.control.filter.OmiscidServiceFilter[], long)
         */
    public HashMap<ServiceFilter, ServiceProxy> findServices(ServiceFilter[] filters, long timeout) {
        WaitForOmiscidServices waitForServices = new WaitForOmiscidServices(ctrlServer.getPeerId()) ;
        HashMap<ServiceFilter, ServiceProxy> result = new HashMap<ServiceFilter, ServiceProxy>() ;
        HashMap<Integer, ServiceFilter> tmpAssociation = new HashMap<Integer, ServiceFilter>() ;
        
//        final HashMap<Integer, ServiceProxy> proxyForService = new HashMap<Integer, ServiceProxy>();
//        for (int i=0; i<filters.length; i++) {
//            final ServiceFilter finalFilter = filters[i];
//            tmpAssociation.put(waitForServices.needService(".*", new OmiscidServiceFilter() {
//                public boolean isAGoodService(OmiscidService s) {
//                    System.out.println(s.getRemotePeerId());
//                    ServiceProxy proxy = proxyForService.get(s.getRemotePeerId());
//                    if (proxy == null) {
//                        proxy = new ServiceProxyImpl(s);
//                        proxyForService.put(s.getRemotePeerId(),proxy);
//                        return finalFilter.acceptService(proxy);
//                    } else {
//                        System.out.println("/// recycling");
//                        return finalFilter.acceptService(proxy);
//                    }
//                }
//            }), filters[i]) ;
//        }
        
        for (int i=0; i<filters.length; i++) {
            final ServiceFilter finalFilter = filters[i];
            tmpAssociation.put(waitForServices.needService(new OmiscidServiceFilter() {
                public boolean isAGoodService(OmiscidService s) {
                    ServiceProxyImpl serviceProxy = ServiceProxyImpl.forService(ServiceImpl.this, s);
                    return serviceProxy != null && finalFilter.acceptService(serviceProxy);
                }
            }), filters[i]) ;
        }
        
        if (timeout != -1) {
            waitForServices.waitResolve(timeout);
        } else {
            waitForServices.waitResolve();
        }
        
        for (Entry<Integer, ServiceFilter> entry : tmpAssociation.entrySet()) {
            OmiscidService bipService = waitForServices.getService(entry.getKey());
            if (bipService != null) {
                //bipService.setServiceId(ctrlServer.getPeerId()); //useless as the waitforservices object is already instanciated with this peer id
                ServiceProxy proxy = ServiceProxyImpl.forService(this, bipService) ;
                result.put(entry.getValue(), proxy);
            } else {
                result.put(entry.getValue(), null);
            }
        }
        return result ;
    }
}
