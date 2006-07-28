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
 * Class PeersType.
 * 
 * @version $Revision$ $Date$
 */
public class PeersType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _peerList
     */
    private java.util.Vector _peerList;


      //----------------/
     //- Constructors -/
    //----------------/

    public PeersType() 
     {
        super();
        _peerList = new Vector();
    } //-- fr.prima.omiscid.control.message.answer.PeersType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addPeer
     * 
     * 
     * 
     * @param vPeer
     */
    public void addPeer(java.lang.String vPeer)
        throws java.lang.IndexOutOfBoundsException
    {
        _peerList.addElement(vPeer);
    } //-- void addPeer(java.lang.String) 

    /**
     * Method addPeer
     * 
     * 
     * 
     * @param index
     * @param vPeer
     */
    public void addPeer(int index, java.lang.String vPeer)
        throws java.lang.IndexOutOfBoundsException
    {
        _peerList.insertElementAt(vPeer, index);
    } //-- void addPeer(int, java.lang.String) 

    /**
     * Method enumeratePeer
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumeratePeer()
    {
        return _peerList.elements();
    } //-- java.util.Enumeration enumeratePeer() 

    /**
     * Method getPeer
     * 
     * 
     * 
     * @param index
     * @return String
     */
    public java.lang.String getPeer(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index >= _peerList.size())) {
            throw new IndexOutOfBoundsException("getPeer: Index value '"+index+"' not in range [0.."+(_peerList.size() - 1) + "]");
        }
        
        return (String)_peerList.elementAt(index);
    } //-- java.lang.String getPeer(int) 

    /**
     * Method getPeer
     * 
     * 
     * 
     * @return String
     */
    public java.lang.String[] getPeer()
    {
        int size = _peerList.size();
        java.lang.String[] mArray = new java.lang.String[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (String)_peerList.elementAt(index);
        }
        return mArray;
    } //-- java.lang.String[] getPeer() 

    /**
     * Method getPeerCount
     * 
     * 
     * 
     * @return int
     */
    public int getPeerCount()
    {
        return _peerList.size();
    } //-- int getPeerCount() 

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
     * Method removeAllPeer
     * 
     */
    public void removeAllPeer()
    {
        _peerList.removeAllElements();
    } //-- void removeAllPeer() 

    /**
     * Method removePeer
     * 
     * 
     * 
     * @param index
     * @return String
     */
    public java.lang.String removePeer(int index)
    {
        java.lang.Object obj = _peerList.elementAt(index);
        _peerList.removeElementAt(index);
        return (String)obj;
    } //-- java.lang.String removePeer(int) 

    /**
     * Method setPeer
     * 
     * 
     * 
     * @param index
     * @param vPeer
     */
    public void setPeer(int index, java.lang.String vPeer)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index >= _peerList.size())) {
            throw new IndexOutOfBoundsException("setPeer: Index value '"+index+"' not in range [0.." + (_peerList.size() - 1) + "]");
        }
        _peerList.setElementAt(vPeer, index);
    } //-- void setPeer(int, java.lang.String) 

    /**
     * Method setPeer
     * 
     * 
     * 
     * @param peerArray
     */
    public void setPeer(java.lang.String[] peerArray)
    {
        //-- copy array
        _peerList.removeAllElements();
        for (int i = 0; i < peerArray.length; i++) {
            _peerList.addElement(peerArray[i]);
        }
    } //-- void setPeer(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return PeersType
     */
    public static fr.prima.omiscid.control.message.answer.PeersType unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (fr.prima.omiscid.control.message.answer.PeersType) Unmarshaller.unmarshal(fr.prima.omiscid.control.message.answer.PeersType.class, reader);
    } //-- fr.prima.omiscid.control.message.answer.PeersType unmarshal(java.io.Reader) 

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
