/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0M2</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.prima.omiscid.control.message.servicexml;

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
        _items = new Vector();
    } //-- fr.prima.omiscid.control.message.servicexml.Service()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addServiceItem
     * 
     * 
     * 
     * @param vServiceItem
     */
    public void addServiceItem(fr.prima.omiscid.control.message.servicexml.ServiceItem vServiceItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.addElement(vServiceItem);
    } //-- void addServiceItem(fr.prima.omiscid.control.message.servicexml.ServiceItem) 

    /**
     * Method addServiceItem
     * 
     * 
     * 
     * @param index
     * @param vServiceItem
     */
    public void addServiceItem(int index, fr.prima.omiscid.control.message.servicexml.ServiceItem vServiceItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.insertElementAt(vServiceItem, index);
    } //-- void addServiceItem(int, fr.prima.omiscid.control.message.servicexml.ServiceItem) 

    /**
     * Method enumerateServiceItem
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateServiceItem()
    {
        return _items.elements();
    } //-- java.util.Enumeration enumerateServiceItem() 

    /**
     * Returns the value of field 'clazz'.
     * 
     * @return String
     * @return the value of field 'clazz'.
     */
    public java.lang.String getClazz()
    {
        return this._clazz;
    } //-- java.lang.String getClazz() 

    /**
     * Returns the value of field 'docURL'.
     * 
     * @return String
     * @return the value of field 'docURL'.
     */
    public java.lang.String getDocURL()
    {
        return this._docURL;
    } //-- java.lang.String getDocURL() 

    /**
     * Returns the value of field 'name'.
     * 
     * @return String
     * @return the value of field 'name'.
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
     * @return ServiceItem
     */
    public fr.prima.omiscid.control.message.servicexml.ServiceItem getServiceItem(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException("getServiceItem: Index value '"+index+"' not in range [0.."+_items.size()+ "]");
        }
        
        return (fr.prima.omiscid.control.message.servicexml.ServiceItem) _items.elementAt(index);
    } //-- fr.prima.omiscid.control.message.servicexml.ServiceItem getServiceItem(int) 

    /**
     * Method getServiceItem
     * 
     * 
     * 
     * @return ServiceItem
     */
    public fr.prima.omiscid.control.message.servicexml.ServiceItem[] getServiceItem()
    {
        int size = _items.size();
        fr.prima.omiscid.control.message.servicexml.ServiceItem[] mArray = new fr.prima.omiscid.control.message.servicexml.ServiceItem[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (fr.prima.omiscid.control.message.servicexml.ServiceItem) _items.elementAt(index);
        }
        return mArray;
    } //-- fr.prima.omiscid.control.message.servicexml.ServiceItem[] getServiceItem() 

    /**
     * Method getServiceItemCount
     * 
     * 
     * 
     * @return int
     */
    public int getServiceItemCount()
    {
        return _items.size();
    } //-- int getServiceItemCount() 

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
     * Method removeAllServiceItem
     * 
     */
    public void removeAllServiceItem()
    {
        _items.removeAllElements();
    } //-- void removeAllServiceItem() 

    /**
     * Method removeServiceItem
     * 
     * 
     * 
     * @param index
     * @return ServiceItem
     */
    public fr.prima.omiscid.control.message.servicexml.ServiceItem removeServiceItem(int index)
    {
        java.lang.Object obj = _items.elementAt(index);
        _items.removeElementAt(index);
        return (fr.prima.omiscid.control.message.servicexml.ServiceItem) obj;
    } //-- fr.prima.omiscid.control.message.servicexml.ServiceItem removeServiceItem(int) 

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
     * Method setServiceItem
     * 
     * 
     * 
     * @param index
     * @param vServiceItem
     */
    public void setServiceItem(int index, fr.prima.omiscid.control.message.servicexml.ServiceItem vServiceItem)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException("setServiceItem: Index value '"+index+"' not in range [0.."+_items.size()+ "]");
        }
        _items.setElementAt(vServiceItem, index);
    } //-- void setServiceItem(int, fr.prima.omiscid.control.message.servicexml.ServiceItem) 

    /**
     * Method setServiceItem
     * 
     * 
     * 
     * @param serviceItemArray
     */
    public void setServiceItem(fr.prima.omiscid.control.message.servicexml.ServiceItem[] serviceItemArray)
    {
        //-- copy array
        _items.removeAllElements();
        for (int i = 0; i < serviceItemArray.length; i++) {
            _items.addElement(serviceItemArray[i]);
        }
    } //-- void setServiceItem(fr.prima.omiscid.control.message.servicexml.ServiceItem) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return Service
     */
    public static fr.prima.omiscid.control.message.servicexml.Service unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.prima.omiscid.control.message.servicexml.Service) Unmarshaller.unmarshal(fr.prima.omiscid.control.message.servicexml.Service.class, reader);
    } //-- fr.prima.omiscid.control.message.servicexml.Service unmarshal(java.io.Reader) 

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
