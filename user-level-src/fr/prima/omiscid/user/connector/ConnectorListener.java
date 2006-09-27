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

package fr.prima.omiscid.user.connector;

import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceProxy;

/**
 * @author Patrick Reignier (UJF/Gravir)
 * Jul 6, 2006
 */
public interface ConnectorListener {
	  /**
     * Processes a received BIP message. As a given message could be processed
     * by several others listeners, the message must not be modified by its
     * processing.
     * @param service the service receiving the message
     * @param localConnectorName the name of the connector that has received the message
     * @param message
     *            the BIP message to process
     */
    public void messageReceived(Service service, String localConnectorName , Message message);

    /**
     * Called when the connexion between the local service and the remote
     * service is broken.
     * @param service the service receiving the message
     * @param localConnectorName the name of the connector handling the broken link
     * @param peerId the disconnected remote service
     */
    public void disconnected(Service service, String localConnectorName, int peerId);

    /**
     * Called when a remote service connects to the local connector.
     * The peerId is a unique pair (service + remote connector). The
     * corresponding service filter can be found using a <CODE>PeerIdIs</CODE> filter. The connector
     * name can be found using the <CODE>findConnector</CODE> method on the <CODE>ServiceProxy</CODE>
     * @param service the service receiving the message
     * @param localConnectorName the name of the connector handling the broken link
     * @param peerId the peerId (a unique Id corresponding to a connector and a remote service)
     * @see ServiceProxy#findConnector(int)
     */
    public void connected(Service service, String localConnectorName, int peerId);

}
