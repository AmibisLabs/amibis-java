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
import java.util.Vector;

/**
 * @author Patrick Reignier (UJF/Gravir)
 *
 */
public class TcpClientServer extends TcpServer {
	
	/** list of clients connected to a remote server */
	protected Vector<TcpClient> listOfClients = null ;
	
	public TcpClientServer(int serviceId) throws IOException
	{
		super(serviceId, 0) ;
		listOfClients = new Vector<TcpClient>() ;
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		synchronized(this)
		{
			listOfClients.add(tcpClient) ;
		}
		
		return tcpClient.getPeerId() ;
	}
}
