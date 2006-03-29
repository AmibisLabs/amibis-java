/**
 * Copyright (C) Patrick Reignier (UJF/Gravir)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package fr.prima.omiscid.com;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import fr.prima.omiscid.com.interf.OmiscidMessageListener;

/**
 * @author Patrick Reignier (UJF/Gravir)
 *
 */
public class TcpClientServer extends TcpServer {
	
	/** list of clients connected to a remote server */
	protected HashMap<Integer, TcpClient> listOfClients = null ;
	
	public TcpClientServer(int serviceId) throws IOException
	{
		super(serviceId, 0) ;
		listOfClients = new HashMap<Integer, TcpClient>() ;
	}
	
	/**
	 * Connects to a remote server
	 * @param host the host address
	 * @param port the port number
	 * @return the peer id
	 */
	public int connectTo(String host, int port)
	{
		TcpClient tcpClient = new TcpClient(serviceId) ;
		try {
			tcpClient.connectTo(host, port) ;
			tcpClient.send((byte[]) null) ;
			while (tcpClient.getPeerId() == 0)
			{
				Thread.yield() ;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		synchronized(this)
		{
			listOfClients.put(tcpClient.getPeerId(), tcpClient) ;
		}
		
		return tcpClient.getPeerId() ;
	}
	
	/* (non-Javadoc)
	 * @see fr.prima.omiscid.com.TcpServer#sendToClients(byte[])
	 */
	@Override
	public void sendToClients(byte[] buffer) {
		// TODO Auto-generated method stub
		super.sendToClients(buffer);
		
		synchronized (this)
		{
			for (TcpClient client : listOfClients.values())
			{
				client.send(buffer) ;
			}
		}
	}
	
	/**
	 * Sends a buffer to a particular client
	 * @param buffer the buffer to send
	 * @param pid the peer id
	 * @return true if the client exists 
	 */
	public boolean sendToOneClient(byte[] buffer,int pid)
	{
		TcpClient client = listOfClients.get(pid) ;
		if (client != null)
		{
			client.send(buffer) ;
			return true ;
		}
		else
			return super.sendToOneClient(buffer, pid) ;
	}
	
	/* (non-Javadoc)
	 * @see fr.prima.omiscid.com.TcpServer#addOmiscidMessageListener(fr.prima.omiscid.com.interf.OmiscidMessageListener)
	 */
	@Override
	public void addOmiscidMessageListener(OmiscidMessageListener listener) {
		// TODO Auto-generated method stub
		super.addOmiscidMessageListener(listener);
		
		synchronized(this)
		{
			cleanListOfClients() ;
			for (TcpClient client : listOfClients.values())
			{
				if (client.isConnected())
					client.addOmiscidMessageListener(listener) ;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see fr.prima.omiscid.com.TcpServer#getNbConnections()
	 */
	@Override
	public int getNbConnections() {
		// TODO Auto-generated method stub
		cleanListOfClients() ;
		return super.getNbConnections() + listOfClients.size() ;
	}
	
	
	
	/**
	 * Removes unconnected clients
	 */
	protected synchronized void cleanListOfClients()
	{
		Vector<TcpClient> remove = new Vector<TcpClient>() ;
		
		for (TcpClient client : listOfClients.values())
		{
			if (!client.isConnected())
				remove.add(client) ;
		}
		
		for (TcpClient clientToRemove : remove)
			listOfClients.remove(clientToRemove) ;
	}
	
	/* (non-Javadoc)
	 * @see fr.prima.omiscid.com.TcpServer#getPeerId(java.util.Vector)
	 */
	@Override
	public int getPeerId(Vector<Integer> vec) {
		// TODO Auto-generated method stub
		int nb = super.getPeerId(vec);
		
		synchronized(this)
		{
			for (TcpClient client : listOfClients.values())
			{
				nb++ ;
				vec.add(client.getPeerId()) ;
			}
		}
		
		return nb ;
	}
	
	/* (non-Javadoc)
	 * @see fr.prima.omiscid.com.TcpServer#removeOmiscidMessageListener(fr.prima.omiscid.com.interf.OmiscidMessageListener)
	 */
	@Override
	public void removeOmiscidMessageListener(OmiscidMessageListener listener) {
		// TODO Auto-generated method stub
		cleanListOfClients() ;
		super.removeOmiscidMessageListener(listener);
		
		synchronized(this)
		{
			for (TcpClient client : listOfClients.values())
				client.removeOmiscidMessageListener(listener) ;
		}
	}
	
	/* (non-Javadoc)
	 * @see fr.prima.omiscid.com.TcpServer#isStillConnected(int)
	 */
	@Override
	public boolean isStillConnected(int peerId) {
		// TODO Auto-generated method stub
		boolean isConnected = super.isStillConnected(peerId);
		if (isConnected)
			return true ;
		else
		{
			synchronized(this)
			{
				cleanListOfClients() ;
				for (TcpClient client : listOfClients.values())
				{
					isConnected = client.isConnected() ;
					if (isConnected)
						return true ;
				}
			}
			return false ;
		}
	}

	/* (non-Javadoc)
	 * @see fr.prima.omiscid.com.TcpServer#close()
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		
		for (TcpClient client : listOfClients.values())
		{
			client.closeConnection() ;
		}
	}
}
