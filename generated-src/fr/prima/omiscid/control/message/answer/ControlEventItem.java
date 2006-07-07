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

import java.io.Serializable;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class ControlEventItem.
 * 
 * @version $Revision$ $Date$
 */
public class ControlEventItem implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Internal choice value storage
     */
    private java.lang.Object _choiceValue;

    /**
     * Field _variable
     */
    private fr.prima.omiscid.control.message.answer.Variable _variable;


      //----------------/
     //- Constructors -/
    //----------------/

    public ControlEventItem() 
     {
        super();
    } //-- fr.prima.omiscid.control.message.answer.ControlEventItem()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'choiceValue'. The field
     * 'choiceValue' has the following description: Internal choice
     * value storage
     * 
     * @return Object
     * @return the value of field 'choiceValue'.
     */
    public java.lang.Object getChoiceValue()
    {
        return this._choiceValue;
    } //-- java.lang.Object getChoiceValue() 

    /**
     * Returns the value of field 'variable'.
     * 
     * @return Variable
     * @return the value of field 'variable'.
     */
    public fr.prima.omiscid.control.message.answer.Variable getVariable()
    {
        return this._variable;
    } //-- fr.prima.omiscid.control.message.answer.Variable getVariable() 

    /**
     * Sets the value of field 'variable'.
     * 
     * @param variable the value of field 'variable'.
     */
    public void setVariable(fr.prima.omiscid.control.message.answer.Variable variable)
    {
        this._variable = variable;
        this._choiceValue = variable;
    } //-- void setVariable(fr.prima.omiscid.control.message.answer.Variable) 

}
