/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0M2</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.prima.omiscid.control.message.answer;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class ControlAnswer.
 * 
 * @version $Revision$ $Date$
 */
public class ControlAnswer implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _id
     */
    private java.lang.String _id;

    /**
     * Field _items
     */
    private java.util.Vector _items;


      //----------------/
     //- Constructors -/
    //----------------/

    public ControlAnswer() 
     {
        super();
        _items = new Vector();
    } //-- fr.prima.omiscid.control.message.answer.ControlAnswer()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addControlAnswerItem
     * 
     * 
     * 
     * @param vControlAnswerItem
     */
    public void addControlAnswerItem(fr.prima.omiscid.control.message.answer.ControlAnswerItem vControlAnswerItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.addElement(vControlAnswerItem);
    } //-- void addControlAnswerItem(fr.prima.omiscid.control.message.answer.ControlAnswerItem) 

    /**
     * Method addControlAnswerItem
     * 
     * 
     * 
     * @param index
     * @param vControlAnswerItem
     */
    public void addControlAnswerItem(int index, fr.prima.omiscid.control.message.answer.ControlAnswerItem vControlAnswerItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.insertElementAt(vControlAnswerItem, index);
    } //-- void addControlAnswerItem(int, fr.prima.omiscid.control.message.answer.ControlAnswerItem) 

    /**
     * Method enumerateControlAnswerItem
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateControlAnswerItem()
    {
        return _items.elements();
    } //-- java.util.Enumeration enumerateControlAnswerItem() 

    /**
     * Method getControlAnswerItem
     * 
     * 
     * 
     * @param index
     * @return ControlAnswerItem
     */
    public fr.prima.omiscid.control.message.answer.ControlAnswerItem getControlAnswerItem(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException("getControlAnswerItem: Index value '"+index+"' not in range [0.."+_items.size()+ "]");
        }
        
        return (fr.prima.omiscid.control.message.answer.ControlAnswerItem) _items.elementAt(index);
    } //-- fr.prima.omiscid.control.message.answer.ControlAnswerItem getControlAnswerItem(int) 

    /**
     * Method getControlAnswerItem
     * 
     * 
     * 
     * @return ControlAnswerItem
     */
    public fr.prima.omiscid.control.message.answer.ControlAnswerItem[] getControlAnswerItem()
    {
        int size = _items.size();
        fr.prima.omiscid.control.message.answer.ControlAnswerItem[] mArray = new fr.prima.omiscid.control.message.answer.ControlAnswerItem[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.prima.omiscid.control.message.answer.ControlAnswerItem) _items.elementAt(index);
        }
        return mArray;
    } //-- fr.prima.omiscid.control.message.answer.ControlAnswerItem[] getControlAnswerItem() 

    /**
     * Method getControlAnswerItemCount
     * 
     * 
     * 
     * @return int
     */
    public int getControlAnswerItemCount()
    {
        return _items.size();
    } //-- int getControlAnswerItemCount() 

    /**
     * Returns the value of field 'id'.
     * 
     * @return String
     * @return the value of field 'id'.
     */
    public java.lang.String getId()
    {
        return this._id;
    } //-- java.lang.String getId() 

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
     * Method removeAllControlAnswerItem
     * 
     */
    public void removeAllControlAnswerItem()
    {
        _items.removeAllElements();
    } //-- void removeAllControlAnswerItem() 

    /**
     * Method removeControlAnswerItem
     * 
     * 
     * 
     * @param index
     * @return ControlAnswerItem
     */
    public fr.prima.omiscid.control.message.answer.ControlAnswerItem removeControlAnswerItem(int index)
    {
        java.lang.Object obj = _items.elementAt(index);
        _items.removeElementAt(index);
        return (fr.prima.omiscid.control.message.answer.ControlAnswerItem) obj;
    } //-- fr.prima.omiscid.control.message.answer.ControlAnswerItem removeControlAnswerItem(int) 

    /**
     * Method setControlAnswerItem
     * 
     * 
     * 
     * @param index
     * @param vControlAnswerItem
     */
    public void setControlAnswerItem(int index, fr.prima.omiscid.control.message.answer.ControlAnswerItem vControlAnswerItem)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException("setControlAnswerItem: Index value '"+index+"' not in range [0.."+_items.size()+ "]");
        }
        _items.setElementAt(vControlAnswerItem, index);
    } //-- void setControlAnswerItem(int, fr.prima.omiscid.control.message.answer.ControlAnswerItem) 

    /**
     * Method setControlAnswerItem
     * 
     * 
     * 
     * @param controlAnswerItemArray
     */
    public void setControlAnswerItem(fr.prima.omiscid.control.message.answer.ControlAnswerItem[] controlAnswerItemArray)
    {
        //-- copy array
        _items.removeAllElements();
        for (int i = 0; i < controlAnswerItemArray.length; i++) {
            _items.addElement(controlAnswerItemArray[i]);
        }
    } //-- void setControlAnswerItem(fr.prima.omiscid.control.message.answer.ControlAnswerItem) 

    /**
     * Sets the value of field 'id'.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(java.lang.String id)
    {
        this._id = id;
    } //-- void setId(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return ControlAnswer
     */
    public static fr.prima.omiscid.control.message.answer.ControlAnswer unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.prima.omiscid.control.message.answer.ControlAnswer) Unmarshaller.unmarshal(fr.prima.omiscid.control.message.answer.ControlAnswer.class, reader);
    } //-- fr.prima.omiscid.control.message.answer.ControlAnswer unmarshal(java.io.Reader) 

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
