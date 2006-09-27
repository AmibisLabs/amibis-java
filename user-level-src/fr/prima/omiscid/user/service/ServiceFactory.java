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

import java.io.InputStream;

import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.exception.InvalidDescriptionException;


/**
 * @author Patrick Reignier (UJF/Gravir)
 *
 */
public interface ServiceFactory {
	
	/** 
	 * Creates a new Omiscid service. This service is not yet advertised in DSN-SD. It must
	 * be first launched (see {@link fr.prima.omiscid.user.service.Service#start}). 
	 * <BR>
	 * <B>Caution :</B> As long as the service is not started, it is still possible to add connectors
	 * or variables . Adding connectors or variables to a started service will trigger an exception.
	 * @param serviceName the name of the service as it will appear in DNS-SD
	 * @return the bip service. All access to the service will be through this object
	 * @see fr.prima.omiscid.user.service.Service#addConnector(String, String, ConnectorType)
	 * @see fr.prima.omiscid.user.service.Service#addVariable(String, String, String, fr.prima.omiscid.user.variable.VariableAccessType) 
	 */
	public Service create(String serviceName) ;
	
	/** 
	 * Creates a new Omiscid service. This service is not yet advertised in DSN-SD. It must
	 * be first launched (see {@link fr.prima.omiscid.user.service.Service#start}). 
	 * <BR>
	 * <B>Caution :</B> As long as the service is not started, it is still possible to add connectors
	 * or variables . Adding connectors or variables to a started service will trigger an exception.
	 * @param serviceName the name of the service as it will appear in DNS-SD
	 * @param className the name of the service class
	 * @return the bip service. All access to the service will be through this object
	 * @see fr.prima.omiscid.user.service.Service#addConnector(String, String, ConnectorType)
	 * @see fr.prima.omiscid.user.service.Service#addVariable(String, String, String, fr.prima.omiscid.user.variable.VariableAccessType) 
	 */
	public Service create(String serviceName, String className);
	
	/** 
	 * Creates a new Omiscid service from an XML description. This service is not yet advertised in DSN-SD. It must
	 * first be launched (see {@link fr.prima.omiscid.user.service.Service#start})
	 * <BR>
	 * <B>Caution :</B> As long as the service is not started, it is still possible to add connectors 
	 * or variables. Adding connectors or variables to a started service will trigger an exception.
	 * @param stream the input stream of the xml service description
	 * @return the bip service. All access to the service will be through this object
	 * @throws InvalidDescriptionException the xml file does not respect the corresponding schema
	 * @see fr.prima.omiscid.user.service.Service#addConnector(String, String, ConnectorType)
	 * @see fr.prima.omiscid.user.service.Service#addVariable(String, String, String, fr.prima.omiscid.user.variable.VariableAccessType) 
	 */
	public Service createFromXML(InputStream stream) throws InvalidDescriptionException ;	

	/**
	 * Future extension : used by the service Binder to detect incoming new bundled services
	 * @param bundle
	 */
	public void addMainClass(MainClass bundle) ;

	/**
	 * Future extension : used by the service Binder to detect departure of bundled services
	 * @param bundle
	 */
	public void removeMainClass(MainClass bundle);

}
