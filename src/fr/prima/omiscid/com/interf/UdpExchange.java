package fr.prima.omiscid.com.interf;

public interface UdpExchange {

	/**
	 * Send a message to a particular destination
	 * 
	 * @param buffer
	 *            the message body
	 * @param host
	 *            the host name of the destination
	 * @param port
	 *            the port number of the destination
	 */
	public abstract void send(byte[] buffer, String host, int port);

}