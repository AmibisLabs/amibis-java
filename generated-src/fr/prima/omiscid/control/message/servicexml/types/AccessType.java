/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0M2</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.prima.omiscid.control.message.servicexml.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class AccessType.
 * 
 * @version $Revision$ $Date$
 */
public class AccessType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The constant type
     */
    public static final int CONSTANT_TYPE = 0;

    /**
     * The instance of the constant type
     */
    public static final AccessType CONSTANT = new AccessType(CONSTANT_TYPE, "constant");

    /**
     * The read type
     */
    public static final int READ_TYPE = 1;

    /**
     * The instance of the read type
     */
    public static final AccessType READ = new AccessType(READ_TYPE, "read");

    /**
     * The readWrite type
     */
    public static final int READWRITE_TYPE = 2;

    /**
     * The instance of the readWrite type
     */
    public static final AccessType READWRITE = new AccessType(READWRITE_TYPE, "readWrite");

    /**
     * Field _memberTable
     */
    private static java.util.Hashtable _memberTable = init();

    /**
     * Field type
     */
    private int type = -1;

    /**
     * Field stringValue
     */
    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private AccessType(int type, java.lang.String value) 
     {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- fr.prima.omiscid.control.message.servicexml.types.AccessType(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate
     * 
     * Returns an enumeration of all possible instances of
     * AccessType
     * 
     * @return Enumeration
     */
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Method getType
     * 
     * Returns the type of this AccessType
     * 
     * @return int
     */
    public int getType()
    {
        return this.type;
    } //-- int getType() 

    /**
     * Method init
     * 
     * 
     * 
     * @return Hashtable
     */
    private static java.util.Hashtable init()
    {
        Hashtable members = new Hashtable();
        members.put("constant", CONSTANT);
        members.put("read", READ);
        members.put("readWrite", READWRITE);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Method readResolve
     * 
     *  will be called during deserialization to replace the
     * deserialized object with the correct constant instance.
     * <br/>
     * 
     * @return Object
     */
    private java.lang.Object readResolve()
    {
        return valueOf(this.stringValue);
    } //-- java.lang.Object readResolve() 

    /**
     * Method toString
     * 
     * Returns the String representation of this AccessType
     * 
     * @return String
     */
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Method valueOf
     * 
     * Returns a new AccessType based on the given String value.
     * 
     * @param string
     * @return AccessType
     */
    public static fr.prima.omiscid.control.message.servicexml.types.AccessType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid AccessType";
            throw new IllegalArgumentException(err);
        }
        return (AccessType) obj;
    } //-- fr.prima.omiscid.control.message.servicexml.types.AccessType valueOf(java.lang.String) 

}
