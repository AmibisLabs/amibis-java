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
        this._items = new java.util.Vector();
    } //-- fr.prima.omiscid.control.message.answer.ControlAnswer()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vControlAnswerItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addControlAnswerItem(fr.prima.omiscid.control.message.answer.ControlAnswerItem vControlAnswerItem)
        throws java.lang.IndexOutOfBoundsException
    {
        this._items.addElement(vControlAnswerItem);
    } //-- void addControlAnswerItem(fr.prima.omiscid.control.message.answer.ControlAnswerItem) 

    /**
     * 
     * 
     * @param index
     * @param vControlAnswerItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addControlAnswerItem(int index, fr.prima.omiscid.control.message.answer.ControlAnswerItem vControlAnswerItem)
        throws java.lang.IndexOutOfBoundsException
    {
        this._items.add(index, vControlAnswerItem);
    } //-- void addControlAnswerItem(int, fr.prima.omiscid.control.message.answer.ControlAnswerItem) 

    /**
     * Method enumerateControlAnswerItem
     * 
     * 
     * 
     * @return an Enumeration over all
     * fr.prima.omiscid.control.message.answer.ControlAnswerItem
     * elements
     */
    public java.util.Enumeration enumerateControlAnswerItem()
    {
        return this._items.elements();
    } //-- java.util.Enumeration enumerateControlAnswerItem() 

    /**
     * Method getControlAnswerItem
     * 
     * 
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * fr.prima.omiscid.control.message.answer.ControlAnswerItem at
     * the given index
     */
    public fr.prima.omiscid.control.message.answer.ControlAnswerItem getControlAnswerItem(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._items.size()) {
            throw new IndexOutOfBoundsException("getControlAnswerItem: Index value '" + index + "' not in range [0.." + (this._items.size() - 1) + "]");
        }
        
        return (fr.prima.omiscid.control.message.answer.ControlAnswerItem) _items.get(index);
    } //-- fr.prima.omiscid.control.message.answer.ControlAnswerItem getControlAnswerItem(int) 

    /**
     * Method getControlAnswerItem
     * 
     * 
     * 
     * @return this collection as an Array
     */
    public fr.prima.omiscid.control.message.answer.ControlAnswerItem[] getControlAnswerItem()
    {
        int size = this._items.size();
        fr.prima.omiscid.control.message.answer.ControlAnswerItem[] array = new fr.prima.omiscid.control.message.answer.ControlAnswerItem[size];
        for (int index = 0; index < size; index++){
            array[index] = (fr.prima.omiscid.control.message.answer.ControlAnswerItem) _items.get(index);
        }
        
        return array;
    } //-- fr.prima.omiscid.control.message.answer.ControlAnswerItem[] getControlAnswerItem() 

    /**
     * Method getControlAnswerItemCount
     * 
     * 
     * 
     * @return the size of this collection
     */
    public int getControlAnswerItemCount()
    {
        return this._items.size();
    } //-- int getControlAnswerItemCount() 

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
    public void removeAllControlAnswerItem()
    {
        this._items.clear();
    } //-- void removeAllControlAnswerItem() 

    /**
     * Method removeControlAnswerItem
     * 
     * 
     * 
     * @param vControlAnswerItem
     * @return true if the object was removed from the collection.
     */
    public boolean removeControlAnswerItem(fr.prima.omiscid.control.message.answer.ControlAnswerItem vControlAnswerItem)
    {
        boolean removed = _items.remove(vControlAnswerItem);
        return removed;
    } //-- boolean removeControlAnswerItem(fr.prima.omiscid.control.message.answer.ControlAnswerItem) 

    /**
     * Method removeControlAnswerItemAt
     * 
     * 
     * 
     * @param index
     * @return the element removed from the collection
     */
    public fr.prima.omiscid.control.message.answer.ControlAnswerItem removeControlAnswerItemAt(int index)
    {
        Object obj = this._items.remove(index);
        return (fr.prima.omiscid.control.message.answer.ControlAnswerItem) obj;
    } //-- fr.prima.omiscid.control.message.answer.ControlAnswerItem removeControlAnswerItemAt(int) 

    /**
     * 
     * 
     * @param index
     * @param vControlAnswerItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setControlAnswerItem(int index, fr.prima.omiscid.control.message.answer.ControlAnswerItem vControlAnswerItem)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._items.size()) {
            throw new IndexOutOfBoundsException("setControlAnswerItem: Index value '" + index + "' not in range [0.." + (this._items.size() - 1) + "]");
        }
        
        this._items.set(index, vControlAnswerItem);
    } //-- void setControlAnswerItem(int, fr.prima.omiscid.control.message.answer.ControlAnswerItem) 

    /**
     * 
     * 
     * @param vControlAnswerItemArray
     */
    public void setControlAnswerItem(fr.prima.omiscid.control.message.answer.ControlAnswerItem[] vControlAnswerItemArray)
    {
        //-- copy array
        _items.clear();
        
        for (int i = 0; i < vControlAnswerItemArray.length; i++) {
                this._items.add(vControlAnswerItemArray[i]);
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
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled
     * fr.prima.omiscid.control.message.answer.ControlAnswer
     */
    public static fr.prima.omiscid.control.message.answer.ControlAnswer unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.prima.omiscid.control.message.answer.ControlAnswer) Unmarshaller.unmarshal(fr.prima.omiscid.control.message.answer.ControlAnswer.class, reader);
    } //-- fr.prima.omiscid.control.message.answer.ControlAnswer unmarshal(java.io.Reader) 

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
