package fr.prima.omiscid.com.interf;

public interface Message {

	/**
	 * Access to the data buffer as a string
	 * 
	 * @return a string built on the data buffer
	 */
	public abstract String getBufferAsString();

	/**
	 * Access to the data buffer
	 * 
	 * @return the data buffer
	 */
	public abstract byte[] getBuffer();

	/**
	 * Access to the id of the source
	 * 
	 * @return an integer to identify the source of this message
	 */
	public abstract int getPeerId();

	/**
	 * Access to the message id
	 * 
	 * @return the message number
	 */
	public abstract int getMsgId();

	/**
	 * Build string with pid, mid and message length (mainly use for test)
	 * 
	 * @return "Msg from "+pid+" "+mid+" "+buffer.length
	 */
	public abstract String toString();

}