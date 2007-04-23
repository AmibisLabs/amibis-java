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
    public Service create(String serviceName);
    
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
    public Service createFromXML(InputStream stream) throws InvalidDescriptionException;
    
    /**
     * Creates a new {@link ServiceRepository} based on a already created service.
     * A service repository is used to mainting automatically a list of running services and can notify of services apparition and disapparition.
     * If you are only willing to look for services at a given instant, using {@link Service#findService} methods is simpler.
     * Connections that may be necessary for the service repository will be done in the name of the provided service.
     *
     * @param service a {@link Service} in the name of which the connection necessary for the service repository will be done
     * @return an operational {@link ServiceRepository}
     */
    public ServiceRepository createServiceRepositoy(Service service);
    
    /**
     * Creates a new {@link ServiceRepository}.
     * A service repository is used to mainting automatically a list of running services and can notify of services apparition and disapparition.
     * If you are only willing to look for services at a given instant, using {@link Service#findService} methods is simpler.
     *
     * A new {@link Service} instance will be hiddenly created. If you already have a {@link Service} instance, you should probably prefer {@link #createServiceRepositoy(Service)}.
     * Connections that may be necessary for the service repository will be done in the name of the hiddenly created service.
     *
     * @return an operational {@link ServiceRepository}
     */
    public ServiceRepository createServiceRepositoy();
    
    /**
     * Future extension : used by the service Binder to detect incoming new bundled services
     * @param bundle
     */
    public void addMainClass(MainClass bundle);
    
    /**
     * Future extension : used by the service Binder to detect departure of bundled services
     * @param bundle
     */
    public void removeMainClass(MainClass bundle);
    
}
