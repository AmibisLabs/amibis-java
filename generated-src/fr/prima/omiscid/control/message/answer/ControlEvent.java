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
 * Class ControlEvent.
 * 
 * @version $Revision$ $Date$
 */
public class ControlEvent implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _variable
     */
    private fr.prima.omiscid.control.message.answer.Variable _variable;


      //----------------/
     //- Constructors -/
    //----------------/

    public ControlEvent() 
     {
        super();
    } //-- fr.prima.omiscid.control.message.answer.ControlEvent()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'variable'.
     * 
     * @return the value of field 'Variable'.
     */
    public fr.prima.omiscid.control.message.answer.Variable getVariable()
    {
        return this._variable;
    } //-- fr.prima.omiscid.control.message.answer.Variable getVariable() 

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
     * Sets the value of field 'variable'.
     * 
     * @param variable the value of field 'variable'.
     */
    public void setVariable(fr.prima.omiscid.control.message.answer.Variable variable)
    {
        this._variable = variable;
    } //-- void setVariable(fr.prima.omiscid.control.message.answer.Variable) 

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
     * fr.prima.omiscid.control.message.answer.ControlEvent
     */
    public static fr.prima.omiscid.control.message.answer.ControlEvent unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.prima.omiscid.control.message.answer.ControlEvent) Unmarshaller.unmarshal(fr.prima.omiscid.control.message.answer.ControlEvent.class, reader);
    } //-- fr.prima.omiscid.control.message.answer.ControlEvent unmarshal(java.io.Reader) 

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
