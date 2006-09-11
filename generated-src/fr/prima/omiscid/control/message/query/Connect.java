/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.prima.omiscid.control.message.query;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class Connect.
 * 
 * @version $Revision$ $Date$
 */
public class Connect implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _host
     */
    private java.lang.String _host;

    /**
     * Field _connectChoice
     */
    private fr.prima.omiscid.control.message.query.ConnectChoice _connectChoice;


      //----------------/
     //- Constructors -/
    //----------------/

    public Connect() 
     {
        super();
    } //-- fr.prima.omiscid.control.message.query.Connect()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'connectChoice'.
     * 
     * @return ConnectChoice
     * @return the value of field 'connectChoice'.
     */
    public fr.prima.omiscid.control.message.query.ConnectChoice getConnectChoice()
    {
        return this._connectChoice;
    } //-- fr.prima.omiscid.control.message.query.ConnectChoice getConnectChoice() 

    /**
     * Returns the value of field 'host'.
     * 
     * @return String
     * @return the value of field 'host'.
     */
    public java.lang.String getHost()
    {
        return this._host;
    } //-- java.lang.String getHost() 

    /**
     * Returns the value of field 'name'.
     * 
     * @return String
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Method isValid
     * 
     * 
     * 
     * @return boolean
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Sets the value of field 'connectChoice'.
     * 
     * @param connectChoice the value of field 'connectChoice'.
     */
    public void setConnectChoice(fr.prima.omiscid.control.message.query.ConnectChoice connectChoice)
    {
        this._connectChoice = connectChoice;
    } //-- void setConnectChoice(fr.prima.omiscid.control.message.query.ConnectChoice) 

    /**
     * Sets the value of field 'host'.
     * 
     * @param host the value of field 'host'.
     */
    public void setHost(java.lang.String host)
    {
        this._host = host;
    } //-- void setHost(java.lang.String) 

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return Connect
     */
    public static fr.prima.omiscid.control.message.query.Connect unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.prima.omiscid.control.message.query.Connect) Unmarshaller.unmarshal(fr.prima.omiscid.control.message.query.Connect.class, reader);
    } //-- fr.prima.omiscid.control.message.query.Connect unmarshal(java.io.Reader) 

    /**
     * Method validate
     * 
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
