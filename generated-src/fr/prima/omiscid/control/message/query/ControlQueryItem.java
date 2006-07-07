/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.0M2</a>, using an XML
 * Schema.
 * $Id$
 */

package fr.prima.omiscid.control.message.query;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Serializable;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

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
     * @return Object
     * @return the value of field 'choiceValue'.
     */
    public java.lang.Object getChoiceValue()
    {
        return this._choiceValue;
    } //-- java.lang.Object getChoiceValue() 

    /**
     * Returns the value of field 'connect'.
     * 
     * @return Connect
     * @return the value of field 'connect'.
     */
    public fr.prima.omiscid.control.message.query.Connect getConnect()
    {
        return this._connect;
    } //-- fr.prima.omiscid.control.message.query.Connect getConnect() 

    /**
     * Returns the value of field 'disconnect'.
     * 
     * @return Disconnect
     * @return the value of field 'disconnect'.
     */
    public fr.prima.omiscid.control.message.query.Disconnect getDisconnect()
    {
        return this._disconnect;
    } //-- fr.prima.omiscid.control.message.query.Disconnect getDisconnect() 

    /**
     * Returns the value of field 'inoutput'.
     * 
     * @return Inoutput
     * @return the value of field 'inoutput'.
     */
    public fr.prima.omiscid.control.message.query.Inoutput getInoutput()
    {
        return this._inoutput;
    } //-- fr.prima.omiscid.control.message.query.Inoutput getInoutput() 

    /**
     * Returns the value of field 'input'.
     * 
     * @return Input
     * @return the value of field 'input'.
     */
    public fr.prima.omiscid.control.message.query.Input getInput()
    {
        return this._input;
    } //-- fr.prima.omiscid.control.message.query.Input getInput() 

    /**
     * Returns the value of field 'output'.
     * 
     * @return Output
     * @return the value of field 'output'.
     */
    public fr.prima.omiscid.control.message.query.Output getOutput()
    {
        return this._output;
    } //-- fr.prima.omiscid.control.message.query.Output getOutput() 

    /**
     * Returns the value of field 'subscribe'.
     * 
     * @return Subscribe
     * @return the value of field 'subscribe'.
     */
    public fr.prima.omiscid.control.message.query.Subscribe getSubscribe()
    {
        return this._subscribe;
    } //-- fr.prima.omiscid.control.message.query.Subscribe getSubscribe() 

    /**
     * Returns the value of field 'unsubscribe'.
     * 
     * @return Unsubscribe
     * @return the value of field 'unsubscribe'.
     */
    public fr.prima.omiscid.control.message.query.Unsubscribe getUnsubscribe()
    {
        return this._unsubscribe;
    } //-- fr.prima.omiscid.control.message.query.Unsubscribe getUnsubscribe() 

    /**
     * Returns the value of field 'variable'.
     * 
     * @return Variable
     * @return the value of field 'variable'.
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
