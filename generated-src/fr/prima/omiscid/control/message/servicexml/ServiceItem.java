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

import java.io.Serializable;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class ServiceItem.
 * 
 * @version $Revision$ $Date$
 */
public class ServiceItem implements java.io.Serializable {


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
    private fr.prima.omiscid.control.message.servicexml.Variable _variable;

    /**
     * Field _input
     */
    private fr.prima.omiscid.control.message.servicexml.Input _input;

    /**
     * Field _output
     */
    private fr.prima.omiscid.control.message.servicexml.Output _output;

    /**
     * Field _inoutput
     */
    private fr.prima.omiscid.control.message.servicexml.Inoutput _inoutput;


      //----------------/
     //- Constructors -/
    //----------------/

    public ServiceItem() 
     {
        super();
    } //-- fr.prima.omiscid.control.message.servicexml.ServiceItem()


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
     * Returns the value of field 'inoutput'.
     * 
     * @return Inoutput
     * @return the value of field 'inoutput'.
     */
    public fr.prima.omiscid.control.message.servicexml.Inoutput getInoutput()
    {
        return this._inoutput;
    } //-- fr.prima.omiscid.control.message.servicexml.Inoutput getInoutput() 

    /**
     * Returns the value of field 'input'.
     * 
     * @return Input
     * @return the value of field 'input'.
     */
    public fr.prima.omiscid.control.message.servicexml.Input getInput()
    {
        return this._input;
    } //-- fr.prima.omiscid.control.message.servicexml.Input getInput() 

    /**
     * Returns the value of field 'output'.
     * 
     * @return Output
     * @return the value of field 'output'.
     */
    public fr.prima.omiscid.control.message.servicexml.Output getOutput()
    {
        return this._output;
    } //-- fr.prima.omiscid.control.message.servicexml.Output getOutput() 

    /**
     * Returns the value of field 'variable'.
     * 
     * @return Variable
     * @return the value of field 'variable'.
     */
    public fr.prima.omiscid.control.message.servicexml.Variable getVariable()
    {
        return this._variable;
    } //-- fr.prima.omiscid.control.message.servicexml.Variable getVariable() 

    /**
     * Sets the value of field 'inoutput'.
     * 
     * @param inoutput the value of field 'inoutput'.
     */
    public void setInoutput(fr.prima.omiscid.control.message.servicexml.Inoutput inoutput)
    {
        this._inoutput = inoutput;
        this._choiceValue = inoutput;
    } //-- void setInoutput(fr.prima.omiscid.control.message.servicexml.Inoutput) 

    /**
     * Sets the value of field 'input'.
     * 
     * @param input the value of field 'input'.
     */
    public void setInput(fr.prima.omiscid.control.message.servicexml.Input input)
    {
        this._input = input;
        this._choiceValue = input;
    } //-- void setInput(fr.prima.omiscid.control.message.servicexml.Input) 

    /**
     * Sets the value of field 'output'.
     * 
     * @param output the value of field 'output'.
     */
    public void setOutput(fr.prima.omiscid.control.message.servicexml.Output output)
    {
        this._output = output;
        this._choiceValue = output;
    } //-- void setOutput(fr.prima.omiscid.control.message.servicexml.Output) 

    /**
     * Sets the value of field 'variable'.
     * 
     * @param variable the value of field 'variable'.
     */
    public void setVariable(fr.prima.omiscid.control.message.servicexml.Variable variable)
    {
        this._variable = variable;
        this._choiceValue = variable;
    } //-- void setVariable(fr.prima.omiscid.control.message.servicexml.Variable) 

}
