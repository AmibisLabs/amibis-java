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
        this._items = new java.util.Vector();
    } //-- fr.prima.omiscid.control.message.query.ControlQuery()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vControlQueryItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addControlQueryItem(fr.prima.omiscid.control.message.query.ControlQueryItem vControlQueryItem)
        throws java.lang.IndexOutOfBoundsException
    {
        this._items.addElement(vControlQueryItem);
    } //-- void addControlQueryItem(fr.prima.omiscid.control.message.query.ControlQueryItem) 

    /**
     * 
     * 
     * @param index
     * @param vControlQueryItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addControlQueryItem(int index, fr.prima.omiscid.control.message.query.ControlQueryItem vControlQueryItem)
        throws java.lang.IndexOutOfBoundsException
    {
        this._items.add(index, vControlQueryItem);
    } //-- void addControlQueryItem(int, fr.prima.omiscid.control.message.query.ControlQueryItem) 

    /**
     * Method enumerateControlQueryItem
     * 
     * 
     * 
     * @return an Enumeration over all
     * fr.prima.omiscid.control.message.query.ControlQueryItem
     * elements
     */
    public java.util.Enumeration enumerateControlQueryItem()
    {
        return this._items.elements();
    } //-- java.util.Enumeration enumerateControlQueryItem() 

    /**
     * Method getControlQueryItem
     * 
     * 
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * fr.prima.omiscid.control.message.query.ControlQueryItem at
     * the given index
     */
    public fr.prima.omiscid.control.message.query.ControlQueryItem getControlQueryItem(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._items.size()) {
            throw new IndexOutOfBoundsException("getControlQueryItem: Index value '" + index + "' not in range [0.." + (this._items.size() - 1) + "]");
        }
        
        return (fr.prima.omiscid.control.message.query.ControlQueryItem) _items.get(index);
    } //-- fr.prima.omiscid.control.message.query.ControlQueryItem getControlQueryItem(int) 

    /**
     * Method getControlQueryItem
     * 
     * 
     * 
     * @return this collection as an Array
     */
    public fr.prima.omiscid.control.message.query.ControlQueryItem[] getControlQueryItem()
    {
        int size = this._items.size();
        fr.prima.omiscid.control.message.query.ControlQueryItem[] array = new fr.prima.omiscid.control.message.query.ControlQueryItem[size];
        for (int index = 0; index < size; index++){
            array[index] = (fr.prima.omiscid.control.message.query.ControlQueryItem) _items.get(index);
        }
        
        return array;
    } //-- fr.prima.omiscid.control.message.query.ControlQueryItem[] getControlQueryItem() 

    /**
     * Method getControlQueryItemCount
     * 
     * 
     * 
     * @return the size of this collection
     */
    public int getControlQueryItemCount()
    {
        return this._items.size();
    } //-- int getControlQueryItemCount() 

    /**
     * Returns the value of field 'id'.
     * 
     * @return the value of field 'Id'.
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
     */
    public void removeAllControlQueryItem()
    {
        this._items.clear();
    } //-- void removeAllControlQueryItem() 

    /**
     * Method removeControlQueryItem
     * 
     * 
     * 
     * @param vControlQueryItem
     * @return true if the object was removed from the collection.
     */
    public boolean removeControlQueryItem(fr.prima.omiscid.control.message.query.ControlQueryItem vControlQueryItem)
    {
        boolean removed = _items.remove(vControlQueryItem);
        return removed;
    } //-- boolean removeControlQueryItem(fr.prima.omiscid.control.message.query.ControlQueryItem) 

    /**
     * Method removeControlQueryItemAt
     * 
     * 
     * 
     * @param index
     * @return the element removed from the collection
     */
    public fr.prima.omiscid.control.message.query.ControlQueryItem removeControlQueryItemAt(int index)
    {
        Object obj = this._items.remove(index);
        return (fr.prima.omiscid.control.message.query.ControlQueryItem) obj;
    } //-- fr.prima.omiscid.control.message.query.ControlQueryItem removeControlQueryItemAt(int) 

    /**
     * 
     * 
     * @param index
     * @param vControlQueryItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setControlQueryItem(int index, fr.prima.omiscid.control.message.query.ControlQueryItem vControlQueryItem)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._items.size()) {
            throw new IndexOutOfBoundsException("setControlQueryItem: Index value '" + index + "' not in range [0.." + (this._items.size() - 1) + "]");
        }
        
        this._items.set(index, vControlQueryItem);
    } //-- void setControlQueryItem(int, fr.prima.omiscid.control.message.query.ControlQueryItem) 

    /**
     * 
     * 
     * @param vControlQueryItemArray
     */
    public void setControlQueryItem(fr.prima.omiscid.control.message.query.ControlQueryItem[] vControlQueryItemArray)
    {
        //-- copy array
        _items.clear();
        
        for (int i = 0; i < vControlQueryItemArray.length; i++) {
                this._items.add(vControlQueryItemArray[i]);
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
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled
     * fr.prima.omiscid.control.message.query.ControlQuery
     */
    public static fr.prima.omiscid.control.message.query.ControlQuery unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.prima.omiscid.control.message.query.ControlQuery) Unmarshaller.unmarshal(fr.prima.omiscid.control.message.query.ControlQuery.class, reader);
    } //-- fr.prima.omiscid.control.message.query.ControlQuery unmarshal(java.io.Reader) 

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
