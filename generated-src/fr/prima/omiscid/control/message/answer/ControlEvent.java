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
 * Class ControlEvent.
 * 
 * @version $Revision$ $Date$
 */
public class ControlEvent implements java.io.Serializable {


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

    public ControlEvent() 
     {
        super();
        _items = new Vector();
    } //-- fr.prima.omiscid.control.message.answer.ControlEvent()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addControlEventItem
     * 
     * 
     * 
     * @param vControlEventItem
     */
    public void addControlEventItem(fr.prima.omiscid.control.message.answer.ControlEventItem vControlEventItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.addElement(vControlEventItem);
    } //-- void addControlEventItem(fr.prima.omiscid.control.message.answer.ControlEventItem) 

    /**
     * Method addControlEventItem
     * 
     * 
     * 
     * @param index
     * @param vControlEventItem
     */
    public void addControlEventItem(int index, fr.prima.omiscid.control.message.answer.ControlEventItem vControlEventItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.insertElementAt(vControlEventItem, index);
    } //-- void addControlEventItem(int, fr.prima.omiscid.control.message.answer.ControlEventItem) 

    /**
     * Method enumerateControlEventItem
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateControlEventItem()
    {
        return _items.elements();
    } //-- java.util.Enumeration enumerateControlEventItem() 

    /**
     * Method getControlEventItem
     * 
     * 
     * 
     * @param index
     * @return ControlEventItem
     */
    public fr.prima.omiscid.control.message.answer.ControlEventItem getControlEventItem(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException("getControlEventItem: Index value '"+index+"' not in range [0.."+_items.size()+ "]");
        }
        
        return (fr.prima.omiscid.control.message.answer.ControlEventItem) _items.elementAt(index);
    } //-- fr.prima.omiscid.control.message.answer.ControlEventItem getControlEventItem(int) 

    /**
     * Method getControlEventItem
     * 
     * 
     * 
     * @return ControlEventItem
     */
    public fr.prima.omiscid.control.message.answer.ControlEventItem[] getControlEventItem()
    {
        int size = _items.size();
        fr.prima.omiscid.control.message.answer.ControlEventItem[] mArray = new fr.prima.omiscid.control.message.answer.ControlEventItem[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.prima.omiscid.control.message.answer.ControlEventItem) _items.elementAt(index);
        }
        return mArray;
    } //-- fr.prima.omiscid.control.message.answer.ControlEventItem[] getControlEventItem() 

    /**
     * Method getControlEventItemCount
     * 
     * 
     * 
     * @return int
     */
    public int getControlEventItemCount()
    {
        return _items.size();
    } //-- int getControlEventItemCount() 

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
     * Method removeAllControlEventItem
     * 
     */
    public void removeAllControlEventItem()
    {
        _items.removeAllElements();
    } //-- void removeAllControlEventItem() 

    /**
     * Method removeControlEventItem
     * 
     * 
     * 
     * @param index
     * @return ControlEventItem
     */
    public fr.prima.omiscid.control.message.answer.ControlEventItem removeControlEventItem(int index)
    {
        java.lang.Object obj = _items.elementAt(index);
        _items.removeElementAt(index);
        return (fr.prima.omiscid.control.message.answer.ControlEventItem) obj;
    } //-- fr.prima.omiscid.control.message.answer.ControlEventItem removeControlEventItem(int) 

    /**
     * Method setControlEventItem
     * 
     * 
     * 
     * @param index
     * @param vControlEventItem
     */
    public void setControlEventItem(int index, fr.prima.omiscid.control.message.answer.ControlEventItem vControlEventItem)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException("setControlEventItem: Index value '"+index+"' not in range [0.."+_items.size()+ "]");
        }
        _items.setElementAt(vControlEventItem, index);
    } //-- void setControlEventItem(int, fr.prima.omiscid.control.message.answer.ControlEventItem) 

    /**
     * Method setControlEventItem
     * 
     * 
     * 
     * @param controlEventItemArray
     */
    public void setControlEventItem(fr.prima.omiscid.control.message.answer.ControlEventItem[] controlEventItemArray)
    {
        //-- copy array
        _items.removeAllElements();
        for (int i = 0; i < controlEventItemArray.length; i++) {
            _items.addElement(controlEventItemArray[i]);
        }
    } //-- void setControlEventItem(fr.prima.omiscid.control.message.answer.ControlEventItem) 

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
     * @return ControlEvent
     */
    public static fr.prima.omiscid.control.message.answer.ControlEvent unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.prima.omiscid.control.message.answer.ControlEvent) Unmarshaller.unmarshal(fr.prima.omiscid.control.message.answer.ControlEvent.class, reader);
    } //-- fr.prima.omiscid.control.message.answer.ControlEvent unmarshal(java.io.Reader) 

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
