/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0.5</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.prima.omiscid.control.message.query;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class ConnectChoice.
 * 
 * @version $Revision$ $Date$
 */
public class ConnectChoice implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _tcp
     */
    private int _tcp;

    /**
     * keeps track of state for field: _tcp
     */
    private boolean _has_tcp;

    /**
     * Field _udp
     */
    private int _udp;

    /**
     * keeps track of state for field: _udp
     */
    private boolean _has_udp;


      //----------------/
     //- Constructors -/
    //----------------/

    public ConnectChoice() 
     {
        super();
    } //-- fr.prima.omiscid.control.message.query.ConnectChoice()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteTcp()
    {
        this._has_tcp= false;
    } //-- void deleteTcp() 

    /**
     */
    public void deleteUdp()
    {
        this._has_udp= false;
    } //-- void deleteUdp() 

    /**
     * Returns the value of field 'tcp'.
     * 
     * @return the value of field 'Tcp'.
     */
    public int getTcp()
    {
        return this._tcp;
    } //-- int getTcp() 

    /**
     * Returns the value of field 'udp'.
     * 
     * @return the value of field 'Udp'.
     */
    public int getUdp()
    {
        return this._udp;
    } //-- int getUdp() 

    /**
     * Method hasTcp
     * 
     * 
     * 
     * @return true if at least one Tcp has been added
     */
    public boolean hasTcp()
    {
        return this._has_tcp;
    } //-- boolean hasTcp() 

    /**
     * Method hasUdp
     * 
     * 
     * 
     * @return true if at least one Udp has been added
     */
    public boolean hasUdp()
    {
        return this._has_udp;
    } //-- boolean hasUdp() 

    /**
     * Method isValid
     * 
     * 
     * 
     * @return true if this object is valid according to the schema
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
     * 
     * 
     * @param out
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * 
     * @param handler
     * @throws java.io.IOException if an IOException occurs during
     * marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Sets the value of field 'tcp'.
     * 
     * @param tcp the value of field 'tcp'.
     */
    public void setTcp(int tcp)
    {
        this._tcp = tcp;
        this._has_tcp = true;
    } //-- void setTcp(int) 

    /**
     * Sets the value of field 'udp'.
     * 
     * @param udp the value of field 'udp'.
     */
    public void setUdp(int udp)
    {
        this._udp = udp;
        this._has_udp = true;
    } //-- void setUdp(int) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled
     * fr.prima.omiscid.control.message.query.ConnectChoice
     */
    public static fr.prima.omiscid.control.message.query.ConnectChoice unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.prima.omiscid.control.message.query.ConnectChoice) Unmarshaller.unmarshal(fr.prima.omiscid.control.message.query.ConnectChoice.class, reader);
    } //-- fr.prima.omiscid.control.message.query.ConnectChoice unmarshal(java.io.Reader) 

    /**
     * 
     * 
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
