/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.prima.omiscid.control.message.answer;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import fr.prima.omiscid.control.message.answer.types.CA_LockResultType;
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
 * Class CA_LockType.
 * 
 * @version $Revision$ $Date$
 */
public class CA_LockType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _result
     */
    private fr.prima.omiscid.control.message.answer.types.CA_LockResultType _result;

    /**
     * Field _peer
     */
    private java.lang.String _peer;


      //----------------/
     //- Constructors -/
    //----------------/

    public CA_LockType() 
     {
        super();
    } //-- fr.prima.omiscid.control.message.answer.CA_LockType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'peer'.
     * 
     * @return String
     * @return the value of field 'peer'.
     */
    public java.lang.String getPeer()
    {
        return this._peer;
    } //-- java.lang.String getPeer() 

    /**
     * Returns the value of field 'result'.
     * 
     * @return CA_LockResultType
     * @return the value of field 'result'.
     */
    public fr.prima.omiscid.control.message.answer.types.CA_LockResultType getResult()
    {
        return this._result;
    } //-- fr.prima.omiscid.control.message.answer.types.CA_LockResultType getResult() 

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
     * Sets the value of field 'peer'.
     * 
     * @param peer the value of field 'peer'.
     */
    public void setPeer(java.lang.String peer)
    {
        this._peer = peer;
    } //-- void setPeer(java.lang.String) 

    /**
     * Sets the value of field 'result'.
     * 
     * @param result the value of field 'result'.
     */
    public void setResult(fr.prima.omiscid.control.message.answer.types.CA_LockResultType result)
    {
        this._result = result;
    } //-- void setResult(fr.prima.omiscid.control.message.answer.types.CA_LockResultType) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return CA_LockType
     */
    public static fr.prima.omiscid.control.message.answer.CA_LockType unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.prima.omiscid.control.message.answer.CA_LockType) Unmarshaller.unmarshal(fr.prima.omiscid.control.message.answer.CA_LockType.class, reader);
    } //-- fr.prima.omiscid.control.message.answer.CA_LockType unmarshal(java.io.Reader) 

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
