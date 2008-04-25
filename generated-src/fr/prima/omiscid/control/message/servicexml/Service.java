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
 * Class Service.
 * 
 * @version $Revision$ $Date$
 */
public class Service implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _docURL
     */
    private java.lang.String _docURL;

    /**
     * Field _clazz
     */
    private java.lang.String _clazz;

    /**
     * Field _items
     */
    private java.util.Vector _items;


      //----------------/
     //- Constructors -/
    //----------------/

    public Service() 
     {
        super();
        this._items = new java.util.Vector();
    } //-- fr.prima.omiscid.control.message.servicexml.Service()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vServiceItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addServiceItem(fr.prima.omiscid.control.message.servicexml.ServiceItem vServiceItem)
        throws java.lang.IndexOutOfBoundsException
    {
        this._items.addElement(vServiceItem);
    } //-- void addServiceItem(fr.prima.omiscid.control.message.servicexml.ServiceItem) 

    /**
     * 
     * 
     * @param index
     * @param vServiceItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addServiceItem(int index, fr.prima.omiscid.control.message.servicexml.ServiceItem vServiceItem)
        throws java.lang.IndexOutOfBoundsException
    {
        this._items.add(index, vServiceItem);
    } //-- void addServiceItem(int, fr.prima.omiscid.control.message.servicexml.ServiceItem) 

    /**
     * Method enumerateServiceItem
     * 
     * 
     * 
     * @return an Enumeration over all
     * fr.prima.omiscid.control.message.servicexml.ServiceItem
     * elements
     */
    public java.util.Enumeration enumerateServiceItem()
    {
        return this._items.elements();
    } //-- java.util.Enumeration enumerateServiceItem() 

    /**
     * Returns the value of field 'clazz'.
     * 
     * @return the value of field 'Clazz'.
     */
    public java.lang.String getClazz()
    {
        return this._clazz;
    } //-- java.lang.String getClazz() 

    /**
     * Returns the value of field 'docURL'.
     * 
     * @return the value of field 'DocURL'.
     */
    public java.lang.String getDocURL()
    {
        return this._docURL;
    } //-- java.lang.String getDocURL() 

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
     * Method getServiceItem
     * 
     * 
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * fr.prima.omiscid.control.message.servicexml.ServiceItem at
     * the given index
     */
    public fr.prima.omiscid.control.message.servicexml.ServiceItem getServiceItem(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._items.size()) {
            throw new IndexOutOfBoundsException("getServiceItem: Index value '" + index + "' not in range [0.." + (this._items.size() - 1) + "]");
        }
        
        return (fr.prima.omiscid.control.message.servicexml.ServiceItem) _items.get(index);
    } //-- fr.prima.omiscid.control.message.servicexml.ServiceItem getServiceItem(int) 

    /**
     * Method getServiceItem
     * 
     * 
     * 
     * @return this collection as an Array
     */
    public fr.prima.omiscid.control.message.servicexml.ServiceItem[] getServiceItem()
    {
        int size = this._items.size();
        fr.prima.omiscid.control.message.servicexml.ServiceItem[] array = new fr.prima.omiscid.control.message.servicexml.ServiceItem[size];
        for (int index = 0; index < size; index++){
            array[index] = (fr.prima.omiscid.control.message.servicexml.ServiceItem) _items.get(index);
        }
        
        return array;
    } //-- fr.prima.omiscid.control.message.servicexml.ServiceItem[] getServiceItem() 

    /**
     * Method getServiceItemCount
     * 
     * 
     * 
     * @return the size of this collection
     */
    public int getServiceItemCount()
    {
        return this._items.size();
    } //-- int getServiceItemCount() 

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
    public void removeAllServiceItem()
    {
        this._items.clear();
    } //-- void removeAllServiceItem() 

    /**
     * Method removeServiceItem
     * 
     * 
     * 
     * @param vServiceItem
     * @return true if the object was removed from the collection.
     */
    public boolean removeServiceItem(fr.prima.omiscid.control.message.servicexml.ServiceItem vServiceItem)
    {
        boolean removed = _items.remove(vServiceItem);
        return removed;
    } //-- boolean removeServiceItem(fr.prima.omiscid.control.message.servicexml.ServiceItem) 

    /**
     * Method removeServiceItemAt
     * 
     * 
     * 
     * @param index
     * @return the element removed from the collection
     */
    public fr.prima.omiscid.control.message.servicexml.ServiceItem removeServiceItemAt(int index)
    {
        Object obj = this._items.remove(index);
        return (fr.prima.omiscid.control.message.servicexml.ServiceItem) obj;
    } //-- fr.prima.omiscid.control.message.servicexml.ServiceItem removeServiceItemAt(int) 

    /**
     * Sets the value of field 'clazz'.
     * 
     * @param clazz the value of field 'clazz'.
     */
    public void setClazz(java.lang.String clazz)
    {
        this._clazz = clazz;
    } //-- void setClazz(java.lang.String) 

    /**
     * Sets the value of field 'docURL'.
     * 
     * @param docURL the value of field 'docURL'.
     */
    public void setDocURL(java.lang.String docURL)
    {
        this._docURL = docURL;
    } //-- void setDocURL(java.lang.String) 

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
     * 
     * 
     * @param index
     * @param vServiceItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setServiceItem(int index, fr.prima.omiscid.control.message.servicexml.ServiceItem vServiceItem)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._items.size()) {
            throw new IndexOutOfBoundsException("setServiceItem: Index value '" + index + "' not in range [0.." + (this._items.size() - 1) + "]");
        }
        
        this._items.set(index, vServiceItem);
    } //-- void setServiceItem(int, fr.prima.omiscid.control.message.servicexml.ServiceItem) 

    /**
     * 
     * 
     * @param vServiceItemArray
     */
    public void setServiceItem(fr.prima.omiscid.control.message.servicexml.ServiceItem[] vServiceItemArray)
    {
        //-- copy array
        _items.clear();
        
        for (int i = 0; i < vServiceItemArray.length; i++) {
                this._items.add(vServiceItemArray[i]);
        }
    } //-- void setServiceItem(fr.prima.omiscid.control.message.servicexml.ServiceItem) 

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
     * fr.prima.omiscid.control.message.servicexml.Service
     */
    public static fr.prima.omiscid.control.message.servicexml.Service unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.prima.omiscid.control.message.servicexml.Service) Unmarshaller.unmarshal(fr.prima.omiscid.control.message.servicexml.Service.class, reader);
    } //-- fr.prima.omiscid.control.message.servicexml.Service unmarshal(java.io.Reader) 

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
