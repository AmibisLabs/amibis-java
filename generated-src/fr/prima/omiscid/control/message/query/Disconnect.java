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
 * Class Disconnect.
 * 
 * @version $Revision$ $Date$
 */
public class Disconnect implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _peerList
     */
    private java.util.Vector _peerList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Disconnect() 
     {
        super();
        this._peerList = new java.util.Vector();
    } //-- fr.prima.omiscid.control.message.query.Disconnect()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vPeer
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPeer(java.lang.String vPeer)
        throws java.lang.IndexOutOfBoundsException
    {
        this._peerList.addElement(vPeer);
    } //-- void addPeer(java.lang.String) 

    /**
     * 
     * 
     * @param index
     * @param vPeer
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPeer(int index, java.lang.String vPeer)
        throws java.lang.IndexOutOfBoundsException
    {
        this._peerList.add(index, vPeer);
    } //-- void addPeer(int, java.lang.String) 

    /**
     * Method enumeratePeer
     * 
     * 
     * 
     * @return an Enumeration over all java.lang.String elements
     */
    public java.util.Enumeration enumeratePeer()
    {
        return this._peerList.elements();
    } //-- java.util.Enumeration enumeratePeer() 

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
     * Method getPeer
     * 
     * 
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public java.lang.String getPeer(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._peerList.size()) {
            throw new IndexOutOfBoundsException("getPeer: Index value '" + index + "' not in range [0.." + (this._peerList.size() - 1) + "]");
        }
        
        return (String)_peerList.get(index);
    } //-- java.lang.String getPeer(int) 

    /**
     * Method getPeer
     * 
     * 
     * 
     * @return this collection as an Array
     */
    public java.lang.String[] getPeer()
    {
        int size = this._peerList.size();
        java.lang.String[] array = new java.lang.String[size];
        for (int index = 0; index < size; index++){
            array[index] = (String)_peerList.get(index);
        }
        
        return array;
    } //-- java.lang.String[] getPeer() 

    /**
     * Method getPeerCount
     * 
     * 
     * 
     * @return the size of this collection
     */
    public int getPeerCount()
    {
        return this._peerList.size();
    } //-- int getPeerCount() 

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
    public void removeAllPeer()
    {
        this._peerList.clear();
    } //-- void removeAllPeer() 

    /**
     * Method removePeer
     * 
     * 
     * 
     * @param vPeer
     * @return true if the object was removed from the collection.
     */
    public boolean removePeer(java.lang.String vPeer)
    {
        boolean removed = _peerList.remove(vPeer);
        return removed;
    } //-- boolean removePeer(java.lang.String) 

    /**
     * Method removePeerAt
     * 
     * 
     * 
     * @param index
     * @return the element removed from the collection
     */
    public java.lang.String removePeerAt(int index)
    {
        Object obj = this._peerList.remove(index);
        return (String)obj;
    } //-- java.lang.String removePeerAt(int) 

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
     * @param vPeer
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPeer(int index, java.lang.String vPeer)
        throws java.lang.IndexOutOfBoundsException
    {
        // check bounds for index
        if (index < 0 || index >= this._peerList.size()) {
            throw new IndexOutOfBoundsException("setPeer: Index value '" + index + "' not in range [0.." + (this._peerList.size() - 1) + "]");
        }
        
        this._peerList.set(index, vPeer);
    } //-- void setPeer(int, java.lang.String) 

    /**
     * 
     * 
     * @param vPeerArray
     */
    public void setPeer(java.lang.String[] vPeerArray)
    {
        //-- copy array
        _peerList.clear();
        
        for (int i = 0; i < vPeerArray.length; i++) {
                this._peerList.add(vPeerArray[i]);
        }
    } //-- void setPeer(java.lang.String) 

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
     * fr.prima.omiscid.control.message.query.Disconnect
     */
    public static fr.prima.omiscid.control.message.query.Disconnect unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.prima.omiscid.control.message.query.Disconnect) Unmarshaller.unmarshal(fr.prima.omiscid.control.message.query.Disconnect.class, reader);
    } //-- fr.prima.omiscid.control.message.query.Disconnect unmarshal(java.io.Reader) 

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
