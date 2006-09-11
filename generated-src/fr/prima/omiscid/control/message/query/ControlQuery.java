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
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class ControlQuery.
 * 
 * @version $Revision$ $Date$
 */
public class ControlQuery implements java.io.Serializable {


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

    public ControlQuery() 
     {
        super();
        _items = new Vector();
    } //-- fr.prima.omiscid.control.message.query.ControlQuery()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addControlQueryItem
     * 
     * 
     * 
     * @param vControlQueryItem
     */
    public void addControlQueryItem(fr.prima.omiscid.control.message.query.ControlQueryItem vControlQueryItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.addElement(vControlQueryItem);
    } //-- void addControlQueryItem(fr.prima.omiscid.control.message.query.ControlQueryItem) 

    /**
     * Method addControlQueryItem
     * 
     * 
     * 
     * @param index
     * @param vControlQueryItem
     */
    public void addControlQueryItem(int index, fr.prima.omiscid.control.message.query.ControlQueryItem vControlQueryItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.insertElementAt(vControlQueryItem, index);
    } //-- void addControlQueryItem(int, fr.prima.omiscid.control.message.query.ControlQueryItem) 

    /**
     * Method enumerateControlQueryItem
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateControlQueryItem()
    {
        return _items.elements();
    } //-- java.util.Enumeration enumerateControlQueryItem() 

    /**
     * Method getControlQueryItem
     * 
     * 
     * 
     * @param index
     * @return ControlQueryItem
     */
    public fr.prima.omiscid.control.message.query.ControlQueryItem getControlQueryItem(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index >= _items.size())) {
            throw new IndexOutOfBoundsException("getControlQueryItem: Index value '"+index+"' not in range [0.."+(_items.size() - 1) + "]");
        }
        
        return (fr.prima.omiscid.control.message.query.ControlQueryItem) _items.elementAt(index);
    } //-- fr.prima.omiscid.control.message.query.ControlQueryItem getControlQueryItem(int) 

    /**
     * Method getControlQueryItem
     * 
     * 
     * 
     * @return ControlQueryItem
     */
    public fr.prima.omiscid.control.message.query.ControlQueryItem[] getControlQueryItem()
    {
        int size = _items.size();
        fr.prima.omiscid.control.message.query.ControlQueryItem[] mArray = new fr.prima.omiscid.control.message.query.ControlQueryItem[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.prima.omiscid.control.message.query.ControlQueryItem) _items.elementAt(index);
        }
        return mArray;
    } //-- fr.prima.omiscid.control.message.query.ControlQueryItem[] getControlQueryItem() 

    /**
     * Method getControlQueryItemCount
     * 
     * 
     * 
     * @return int
     */
    public int getControlQueryItemCount()
    {
        return _items.size();
    } //-- int getControlQueryItemCount() 

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
     * Method removeAllControlQueryItem
     * 
     */
    public void removeAllControlQueryItem()
    {
        _items.removeAllElements();
    } //-- void removeAllControlQueryItem() 

    /**
     * Method removeControlQueryItem
     * 
     * 
     * 
     * @param index
     * @return ControlQueryItem
     */
    public fr.prima.omiscid.control.message.query.ControlQueryItem removeControlQueryItem(int index)
    {
        java.lang.Object obj = _items.elementAt(index);
        _items.removeElementAt(index);
        return (fr.prima.omiscid.control.message.query.ControlQueryItem) obj;
    } //-- fr.prima.omiscid.control.message.query.ControlQueryItem removeControlQueryItem(int) 

    /**
     * Method setControlQueryItem
     * 
     * 
     * 
     * @param index
     * @param vControlQueryItem
     */
    public void setControlQueryItem(int index, fr.prima.omiscid.control.message.query.ControlQueryItem vControlQueryItem)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index >= _items.size())) {
            throw new IndexOutOfBoundsException("setControlQueryItem: Index value '"+index+"' not in range [0.." + (_items.size() - 1) + "]");
        }
        _items.setElementAt(vControlQueryItem, index);
    } //-- void setControlQueryItem(int, fr.prima.omiscid.control.message.query.ControlQueryItem) 

    /**
     * Method setControlQueryItem
     * 
     * 
     * 
     * @param controlQueryItemArray
     */
    public void setControlQueryItem(fr.prima.omiscid.control.message.query.ControlQueryItem[] controlQueryItemArray)
    {
        //-- copy array
        _items.removeAllElements();
        for (int i = 0; i < controlQueryItemArray.length; i++) {
            _items.addElement(controlQueryItemArray[i]);
        }
    } //-- void setControlQueryItem(fr.prima.omiscid.control.message.query.ControlQueryItem) 

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
     * @return ControlQuery
     */
    public static fr.prima.omiscid.control.message.query.ControlQuery unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.prima.omiscid.control.message.query.ControlQuery) Unmarshaller.unmarshal(fr.prima.omiscid.control.message.query.ControlQuery.class, reader);
    } //-- fr.prima.omiscid.control.message.query.ControlQuery unmarshal(java.io.Reader) 

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
