/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0.5</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.prima.omiscid.control.message.answer;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class CA_InOutputType.
 * 
 * @version $Revision$ $Date$
 */
public class CA_InOutputType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

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

    /**
     * Field _description
     */
    private java.lang.String _description;

    /**
     * Field _formatDescription
     */
    private java.lang.String _formatDescription;

    /**
     * Field _peers
     */
    private fr.prima.omiscid.control.message.answer.Peers _peers;

    /**
     * Field _peerId
     */
    private java.lang.String _peerId;

    /**
     * Field _require
     */
    private int _require;

    /**
     * keeps track of state for field: _require
     */
    private boolean _has_require;


      //----------------/
     //- Constructors -/
    //----------------/

    public CA_InOutputType() 
     {
        super();
    } //-- fr.prima.omiscid.control.message.answer.CA_InOutputType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteRequire()
    {
        this._has_require= false;
    } //-- void deleteRequire() 

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
     * Returns the value of field 'description'.
     * 
     * @return the value of field 'Description'.
     */
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
     * Returns the value of field 'formatDescription'.
     * 
     * @return the value of field 'FormatDescription'.
     */
    public java.lang.String getFormatDescription()
    {
        return this._formatDescription;
    } //-- java.lang.String getFormatDescription() 

    /**
     * Returns the value of field 'name'.
     * 
     * @return the value of field 'Name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Returns the value of field 'peerId'.
     * 
     * @return the value of field 'PeerId'.
     */
    public java.lang.String getPeerId()
    {
        return this._peerId;
    } //-- java.lang.String getPeerId() 

    /**
     * Returns the value of field 'peers'.
     * 
     * @return the value of field 'Peers'.
     */
    public fr.prima.omiscid.control.message.answer.Peers getPeers()
    {
        return this._peers;
    } //-- fr.prima.omiscid.control.message.answer.Peers getPeers() 

    /**
     * Returns the value of field 'require'.
     * 
     * @return the value of field 'Require'.
     */
    public int getRequire()
    {
        return this._require;
    } //-- int getRequire() 

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
     * Method hasRequire
     * 
     * 
     * 
     * @return true if at least one Require has been added
     */
    public boolean hasRequire()
    {
        return this._has_require;
    } //-- boolean hasRequire() 

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
     * Sets the value of field 'description'.
     * 
     * @param description the value of field 'description'.
     */
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * Sets the value of field 'formatDescription'.
     * 
     * @param formatDescription the value of field
     * 'formatDescription'.
     */
    public void setFormatDescription(java.lang.String formatDescription)
    {
        this._formatDescription = formatDescription;
    } //-- void setFormatDescription(java.lang.String) 

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
     * Sets the value of field 'peerId'.
     * 
     * @param peerId the value of field 'peerId'.
     */
    public void setPeerId(java.lang.String peerId)
    {
        this._peerId = peerId;
    } //-- void setPeerId(java.lang.String) 

    /**
     * Sets the value of field 'peers'.
     * 
     * @param peers the value of field 'peers'.
     */
    public void setPeers(fr.prima.omiscid.control.message.answer.Peers peers)
    {
        this._peers = peers;
    } //-- void setPeers(fr.prima.omiscid.control.message.answer.Peers) 

    /**
     * Sets the value of field 'require'.
     * 
     * @param require the value of field 'require'.
     */
    public void setRequire(int require)
    {
        this._require = require;
        this._has_require = true;
    } //-- void setRequire(int) 

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
     * fr.prima.omiscid.control.message.answer.CA_InOutputType
     */
    public static fr.prima.omiscid.control.message.answer.CA_InOutputType unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.prima.omiscid.control.message.answer.CA_InOutputType) Unmarshaller.unmarshal(fr.prima.omiscid.control.message.answer.CA_InOutputType.class, reader);
    } //-- fr.prima.omiscid.control.message.answer.CA_InOutputType unmarshal(java.io.Reader) 

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
