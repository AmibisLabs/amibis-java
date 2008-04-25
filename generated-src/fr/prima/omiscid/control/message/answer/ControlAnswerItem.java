/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0.5</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.prima.omiscid.control.message.answer;

/**
 * Class ControlAnswerItem.
 * 
 * @version $Revision$ $Date$
 */
public class ControlAnswerItem implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Internal choice value storage
     */
    private java.lang.Object _choiceValue;

    /**
     * Field _input
     */
    private fr.prima.omiscid.control.message.answer.Input _input;

    /**
     * Field _output
     */
    private fr.prima.omiscid.control.message.answer.Output _output;

    /**
     * Field _inoutput
     */
    private fr.prima.omiscid.control.message.answer.Inoutput _inoutput;

    /**
     * Field _variable
     */
    private fr.prima.omiscid.control.message.answer.Variable _variable;

    /**
     * Field _lock
     */
    private fr.prima.omiscid.control.message.answer.Lock _lock;

    /**
     * Field _unlock
     */
    private fr.prima.omiscid.control.message.answer.Unlock _unlock;


      //----------------/
     //- Constructors -/
    //----------------/

    public ControlAnswerItem() 
     {
        super();
    } //-- fr.prima.omiscid.control.message.answer.ControlAnswerItem()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'choiceValue'. The field
     * 'choiceValue' has the following description: Internal choice
     * value storage
     * 
     * @return the value of field 'ChoiceValue'.
     */
    public java.lang.Object getChoiceValue()
    {
        return this._choiceValue;
    } //-- java.lang.Object getChoiceValue() 

    /**
     * Returns the value of field 'inoutput'.
     * 
     * @return the value of field 'Inoutput'.
     */
    public fr.prima.omiscid.control.message.answer.Inoutput getInoutput()
    {
        return this._inoutput;
    } //-- fr.prima.omiscid.control.message.answer.Inoutput getInoutput() 

    /**
     * Returns the value of field 'input'.
     * 
     * @return the value of field 'Input'.
     */
    public fr.prima.omiscid.control.message.answer.Input getInput()
    {
        return this._input;
    } //-- fr.prima.omiscid.control.message.answer.Input getInput() 

    /**
     * Returns the value of field 'lock'.
     * 
     * @return the value of field 'Lock'.
     */
    public fr.prima.omiscid.control.message.answer.Lock getLock()
    {
        return this._lock;
    } //-- fr.prima.omiscid.control.message.answer.Lock getLock() 

    /**
     * Returns the value of field 'output'.
     * 
     * @return the value of field 'Output'.
     */
    public fr.prima.omiscid.control.message.answer.Output getOutput()
    {
        return this._output;
    } //-- fr.prima.omiscid.control.message.answer.Output getOutput() 

    /**
     * Returns the value of field 'unlock'.
     * 
     * @return the value of field 'Unlock'.
     */
    public fr.prima.omiscid.control.message.answer.Unlock getUnlock()
    {
        return this._unlock;
    } //-- fr.prima.omiscid.control.message.answer.Unlock getUnlock() 

    /**
     * Returns the value of field 'variable'.
     * 
     * @return the value of field 'Variable'.
     */
    public fr.prima.omiscid.control.message.answer.Variable getVariable()
    {
        return this._variable;
    } //-- fr.prima.omiscid.control.message.answer.Variable getVariable() 

    /**
     * Sets the value of field 'inoutput'.
     * 
     * @param inoutput the value of field 'inoutput'.
     */
    public void setInoutput(fr.prima.omiscid.control.message.answer.Inoutput inoutput)
    {
        this._inoutput = inoutput;
        this._choiceValue = inoutput;
    } //-- void setInoutput(fr.prima.omiscid.control.message.answer.Inoutput) 

    /**
     * Sets the value of field 'input'.
     * 
     * @param input the value of field 'input'.
     */
    public void setInput(fr.prima.omiscid.control.message.answer.Input input)
    {
        this._input = input;
        this._choiceValue = input;
    } //-- void setInput(fr.prima.omiscid.control.message.answer.Input) 

    /**
     * Sets the value of field 'lock'.
     * 
     * @param lock the value of field 'lock'.
     */
    public void setLock(fr.prima.omiscid.control.message.answer.Lock lock)
    {
        this._lock = lock;
        this._choiceValue = lock;
    } //-- void setLock(fr.prima.omiscid.control.message.answer.Lock) 

    /**
     * Sets the value of field 'output'.
     * 
     * @param output the value of field 'output'.
     */
    public void setOutput(fr.prima.omiscid.control.message.answer.Output output)
    {
        this._output = output;
        this._choiceValue = output;
    } //-- void setOutput(fr.prima.omiscid.control.message.answer.Output) 

    /**
     * Sets the value of field 'unlock'.
     * 
     * @param unlock the value of field 'unlock'.
     */
    public void setUnlock(fr.prima.omiscid.control.message.answer.Unlock unlock)
    {
        this._unlock = unlock;
        this._choiceValue = unlock;
    } //-- void setUnlock(fr.prima.omiscid.control.message.answer.Unlock) 

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
