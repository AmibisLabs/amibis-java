/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.prima.omiscid.control.message.answer.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class CA_LockResultType.
 * 
 * @version $Revision$ $Date$
 */
public class CA_LockResultType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The ok type
     */
    public static final int OK_TYPE = 0;

    /**
     * The instance of the ok type
     */
    public static final CA_LockResultType OK = new CA_LockResultType(OK_TYPE, "ok");

    /**
     * The failed type
     */
    public static final int FAILED_TYPE = 1;

    /**
     * The instance of the failed type
     */
    public static final CA_LockResultType FAILED = new CA_LockResultType(FAILED_TYPE, "failed");

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

    private CA_LockResultType(int type, java.lang.String value) 
     {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- fr.prima.omiscid.control.message.answer.types.CA_LockResultType(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate
     * 
     * Returns an enumeration of all possible instances of
     * CA_LockResultType
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
     * Returns the type of this CA_LockResultType
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
        members.put("ok", OK);
        members.put("failed", FAILED);
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
     * Returns the String representation of this CA_LockResultType
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
     * Returns a new CA_LockResultType based on the given String
     * value.
     * 
     * @param string
     * @return CA_LockResultType
     */
    public static fr.prima.omiscid.control.message.answer.types.CA_LockResultType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid CA_LockResultType";
            throw new IllegalArgumentException(err);
        }
        return (CA_LockResultType) obj;
    } //-- fr.prima.omiscid.control.message.answer.types.CA_LockResultType valueOf(java.lang.String) 

}
