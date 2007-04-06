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

import java.io.InputStream;

import fr.prima.omiscid.control.ControlServer;
import fr.prima.omiscid.control.ServiceFromXml;
import fr.prima.omiscid.control.VariableAttribute;
import fr.prima.omiscid.control.interf.GlobalConstants;
import fr.prima.omiscid.user.exception.InvalidDescriptionException;
import fr.prima.omiscid.user.service.MainClass;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import fr.prima.omiscid.user.service.ServiceRepository;

/**
 * @author Patrick Reignier (UJF/Gravir)
 *
 */
public class ServiceFactoryImpl implements ServiceFactory {
	
	/** default class name of a service if no class name is specified on the service creation 
	 * @see #create(String)
	 * */
	public static final String DEFAULTCLASS = "Service";

	/* (non-Javadoc)
	 * @see fr.prima.bip.service.ServiceFactory#create(java.lang.String)
	 */
	synchronized  public Service create(String serviceName) {
		return create(serviceName, ServiceFactoryImpl.DEFAULTCLASS);
	}

	/* (non-Javadoc)
	 * @see fr.prima.bip.service.ServiceFactory#createFromXml()
	 */
	synchronized  public Service createFromXML(InputStream stream) throws InvalidDescriptionException {
		ControlServer ctrlServer;
		try {
			ctrlServer = (ControlServer) new ServiceFromXml(stream);
		} catch (Exception e) {
			e.printStackTrace() ;
		  throw new InvalidDescriptionException("Invalid XML service description file");
		}
		
		Service service = new ServiceImpl(ctrlServer) ;
		
		return service ;
	}
	
	/* (non-Javadoc)
	 * @see fr.prima.omiscid.service.ServiceFactory#create(java.lang.String, java.lang.String)
	 */
	public Service create(String serviceName, String className) {
		ControlServer ctrlServer = new ControlServer(serviceName) ;
		Service service = new ServiceImpl(ctrlServer) ;
		
		VariableAttribute classVar = ctrlServer.findVariable(GlobalConstants.constantNameForClass);
		classVar.setValueStr(className);
		
		return service ;
	}
    
    public ServiceRepository createServiceRepositoy(Service service) {
        return new ServiceRepositoryImpl((ServiceImpl)service);
    }

    public ServiceRepository createServiceRepositoy() {
        // Creates a new service with a dummy name
        // Logically the service name isn't used anywhere and should be indifferent
        return createServiceRepositoy(create("ServiceRepository"));
    }
    
	/**
	 * @param bipComponent
	 */
	public void addMainClass(MainClass bipComponent) {
	    System.err.println("Debug : Omiscid arriving");
	}

	/**
	 * @param bipComponent
	 */
	public void removeMainClass(MainClass bipComponent) {
	    System.err.println("Debug : Omiscid  leaving");
	}

    
}
