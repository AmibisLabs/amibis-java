/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0.5</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.prima.omiscid.control.message.servicexml;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class ConnectorType.
 * 
 * @version $Revision$ $Date$
 */
public class ConnectorType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _description
     */
    private java.lang.String _description;

    /**
     * Field _formatDescription
     */
    private java.lang.String _formatDescription;

    /**
     * Field _schemaURL
     */
    private java.lang.String _schemaURL;

    /**
     * Field _messageExample
     */
    private java.lang.String _messageExample;


      //----------------/
     //- Constructors -/
    //----------------/

    public ConnectorType() 
     {
        super();
    } //-- fr.prima.omiscid.control.message.servicexml.ConnectorType()


      //-----------/
     //- Methods -/
    //-----------/

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
     * Returns the value of field 'messageExample'.
     * 
     * @return the value of field 'MessageExample'.
     */
    public java.lang.String getMessageExample()
    {
        return this._messageExample;
    } //-- java.lang.String getMessageExample() 

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
     * Returns the value of field 'schemaURL'.
     * 
     * @return the value of field 'SchemaURL'.
     */
    public java.lang.String getSchemaURL()
    {
        return this._schemaURL;
    } //-- java.lang.String getSchemaURL() 

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
     * Sets the value of field 'messageExample'.
     * 
     * @param messageExample the value of field 'messageExample'.
     */
    public void setMessageExample(java.lang.String messageExample)
    {
        this._messageExample = messageExample;
    } //-- void setMessageExample(java.lang.String) 

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
     * Sets the value of field 'schemaURL'.
     * 
     * @param schemaURL the value of field 'schemaURL'.
     */
    public void setSchemaURL(java.lang.String schemaURL)
    {
        this._schemaURL = schemaURL;
    } //-- void setSchemaURL(java.lang.String) 

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
     * fr.prima.omiscid.control.message.servicexml.ConnectorType
     */
    public static fr.prima.omiscid.control.message.servicexml.ConnectorType unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.prima.omiscid.control.message.servicexml.ConnectorType) Unmarshaller.unmarshal(fr.prima.omiscid.control.message.servicexml.ConnectorType.class, reader);
    } //-- fr.prima.omiscid.control.message.servicexml.ConnectorType unmarshal(java.io.Reader) 

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
