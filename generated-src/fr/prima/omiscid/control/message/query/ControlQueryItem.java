/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0.5</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.prima.omiscid.control.message.query;

/**
 * Class ControlQueryItem.
 * 
 * @version $Revision$ $Date$
 */
public class ControlQueryItem implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Internal choice value storage
     */
    private java.lang.Object _choiceValue;

    /**
     * Field _fullDescription
     */
    private fr.prima.omiscid.control.message.query.FullDescription _fullDescription;

    /**
     * Field _input
     */
    private fr.prima.omiscid.control.message.query.Input _input;

    /**
     * Field _output
     */
    private fr.prima.omiscid.control.message.query.Output _output;

    /**
     * Field _inoutput
     */
    private fr.prima.omiscid.control.message.query.Inoutput _inoutput;

    /**
     * Field _variable
     */
    private fr.prima.omiscid.control.message.query.Variable _variable;

    /**
     * Field _connect
     */
    private fr.prima.omiscid.control.message.query.Connect _connect;

    /**
     * Field _disconnect
     */
    private fr.prima.omiscid.control.message.query.Disconnect _disconnect;

    /**
     * Field _subscribe
     */
    private fr.prima.omiscid.control.message.query.Subscribe _subscribe;

    /**
     * Field _unsubscribe
     */
    private fr.prima.omiscid.control.message.query.Unsubscribe _unsubscribe;

    /**
     * Field _lock
     */
    private fr.prima.omiscid.control.message.query.Lock _lock;

    /**
     * Field _unlock
     */
    private fr.prima.omiscid.control.message.query.Unlock _unlock;


      //----------------/
     //- Constructors -/
    //----------------/

    public ControlQueryItem() 
     {
        super();
    } //-- fr.prima.omiscid.control.message.query.ControlQueryItem()


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
     * Returns the value of field 'connect'.
     * 
     * @return the value of field 'Connect'.
     */
    public fr.prima.omiscid.control.message.query.Connect getConnect()
    {
        return this._connect;
    } //-- fr.prima.omiscid.control.message.query.Connect getConnect() 

    /**
     * Returns the value of field 'disconnect'.
     * 
     * @return the value of field 'Disconnect'.
     */
    public fr.prima.omiscid.control.message.query.Disconnect getDisconnect()
    {
        return this._disconnect;
    } //-- fr.prima.omiscid.control.message.query.Disconnect getDisconnect() 

    /**
     * Returns the value of field 'fullDescription'.
     * 
     * @return the value of field 'FullDescription'.
     */
    public fr.prima.omiscid.control.message.query.FullDescription getFullDescription()
    {
        return this._fullDescription;
    } //-- fr.prima.omiscid.control.message.query.FullDescription getFullDescription() 

    /**
     * Returns the value of field 'inoutput'.
     * 
     * @return the value of field 'Inoutput'.
     */
    public fr.prima.omiscid.control.message.query.Inoutput getInoutput()
    {
        return this._inoutput;
    } //-- fr.prima.omiscid.control.message.query.Inoutput getInoutput() 

    /**
     * Returns the value of field 'input'.
     * 
     * @return the value of field 'Input'.
     */
    public fr.prima.omiscid.control.message.query.Input getInput()
    {
        return this._input;
    } //-- fr.prima.omiscid.control.message.query.Input getInput() 

    /**
     * Returns the value of field 'lock'.
     * 
     * @return the value of field 'Lock'.
     */
    public fr.prima.omiscid.control.message.query.Lock getLock()
    {
        return this._lock;
    } //-- fr.prima.omiscid.control.message.query.Lock getLock() 

    /**
     * Returns the value of field 'output'.
     * 
     * @return the value of field 'Output'.
     */
    public fr.prima.omiscid.control.message.query.Output getOutput()
    {
        return this._output;
    } //-- fr.prima.omiscid.control.message.query.Output getOutput() 

    /**
     * Returns the value of field 'subscribe'.
     * 
     * @return the value of field 'Subscribe'.
     */
    public fr.prima.omiscid.control.message.query.Subscribe getSubscribe()
    {
        return this._subscribe;
    } //-- fr.prima.omiscid.control.message.query.Subscribe getSubscribe() 

    /**
     * Returns the value of field 'unlock'.
     * 
     * @return the value of field 'Unlock'.
     */
    public fr.prima.omiscid.control.message.query.Unlock getUnlock()
    {
        return this._unlock;
    } //-- fr.prima.omiscid.control.message.query.Unlock getUnlock() 

    /**
     * Returns the value of field 'unsubscribe'.
     * 
     * @return the value of field 'Unsubscribe'.
     */
    public fr.prima.omiscid.control.message.query.Unsubscribe getUnsubscribe()
    {
        return this._unsubscribe;
    } //-- fr.prima.omiscid.control.message.query.Unsubscribe getUnsubscribe() 

    /**
     * Returns the value of field 'variable'.
     * 
     * @return the value of field 'Variable'.
     */
    public fr.prima.omiscid.control.message.query.Variable getVariable()
    {
        return this._variable;
    } //-- fr.prima.omiscid.control.message.query.Variable getVariable() 

    /**
     * Sets the value of field 'connect'.
     * 
     * @param connect the value of field 'connect'.
     */
    public void setConnect(fr.prima.omiscid.control.message.query.Connect connect)
    {
        this._connect = connect;
        this._choiceValue = connect;
    } //-- void setConnect(fr.prima.omiscid.control.message.query.Connect) 

    /**
     * Sets the value of field 'disconnect'.
     * 
     * @param disconnect the value of field 'disconnect'.
     */
    public void setDisconnect(fr.prima.omiscid.control.message.query.Disconnect disconnect)
    {
        this._disconnect = disconnect;
        this._choiceValue = disconnect;
    } //-- void setDisconnect(fr.prima.omiscid.control.message.query.Disconnect) 

    /**
     * Sets the value of field 'fullDescription'.
     * 
     * @param fullDescription the value of field 'fullDescription'.
     */
    public void setFullDescription(fr.prima.omiscid.control.message.query.FullDescription fullDescription)
    {
        this._fullDescription = fullDescription;
        this._choiceValue = fullDescription;
    } //-- void setFullDescription(fr.prima.omiscid.control.message.query.FullDescription) 

    /**
     * Sets the value of field 'inoutput'.
     * 
     * @param inoutput the value of field 'inoutput'.
     */
    public void setInoutput(fr.prima.omiscid.control.message.query.Inoutput inoutput)
    {
        this._inoutput = inoutput;
        this._choiceValue = inoutput;
    } //-- void setInoutput(fr.prima.omiscid.control.message.query.Inoutput) 

    /**
     * Sets the value of field 'input'.
     * 
     * @param input the value of field 'input'.
     */
    public void setInput(fr.prima.omiscid.control.message.query.Input input)
    {
        this._input = input;
        this._choiceValue = input;
    } //-- void setInput(fr.prima.omiscid.control.message.query.Input) 

    /**
     * Sets the value of field 'lock'.
     * 
     * @param lock the value of field 'lock'.
     */
    public void setLock(fr.prima.omiscid.control.message.query.Lock lock)
    {
        this._lock = lock;
        this._choiceValue = lock;
    } //-- void setLock(fr.prima.omiscid.control.message.query.Lock) 

    /**
     * Sets the value of field 'output'.
     * 
     * @param output the value of field 'output'.
     */
    public void setOutput(fr.prima.omiscid.control.message.query.Output output)
    {
        this._output = output;
        this._choiceValue = output;
    } //-- void setOutput(fr.prima.omiscid.control.message.query.Output) 

    /**
     * Sets the value of field 'subscribe'.
     * 
     * @param subscribe the value of field 'subscribe'.
     */
    public void setSubscribe(fr.prima.omiscid.control.message.query.Subscribe subscribe)
    {
        this._subscribe = subscribe;
        this._choiceValue = subscribe;
    } //-- void setSubscribe(fr.prima.omiscid.control.message.query.Subscribe) 

    /**
     * Sets the value of field 'unlock'.
     * 
     * @param unlock the value of field 'unlock'.
     */
    public void setUnlock(fr.prima.omiscid.control.message.query.Unlock unlock)
    {
        this._unlock = unlock;
        this._choiceValue = unlock;
    } //-- void setUnlock(fr.prima.omiscid.control.message.query.Unlock) 

    /**
     * Sets the value of field 'unsubscribe'.
     * 
     * @param unsubscribe the value of field 'unsubscribe'.
     */
    public void setUnsubscribe(fr.prima.omiscid.control.message.query.Unsubscribe unsubscribe)
    {
        this._unsubscribe = unsubscribe;
        this._choiceValue = unsubscribe;
    } //-- void setUnsubscribe(fr.prima.omiscid.control.message.query.Unsubscribe) 

    /**
     * Sets the value of field 'variable'.
     * 
     * @param variable the value of field 'variable'.
     */
    public void setVariable(fr.prima.omiscid.control.message.query.Variable variable)
    {
        this._variable = variable;
        this._choiceValue = variable;
    } //-- void setVariable(fr.prima.omiscid.control.message.query.Variable) 

}
