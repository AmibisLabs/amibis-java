//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-463 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.04.18 at 02:29:19 AM CEST 
//


package fr.prima.omiscid.generated.controlanswer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the fr.prima.omiscid.generated.controlanswer package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ControlAnswerInput_QNAME = new QName("", "input");
    private final static QName _ControlAnswerUnlock_QNAME = new QName("", "unlock");
    private final static QName _ControlAnswerLock_QNAME = new QName("", "lock");
    private final static QName _ControlAnswerOutput_QNAME = new QName("", "output");
    private final static QName _ControlAnswerInoutput_QNAME = new QName("", "inoutput");
    private final static QName _ControlAnswerVariable_QNAME = new QName("", "variable");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fr.prima.omiscid.generated.controlanswer
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link IOType }
     * 
     */
    public IOType createIOType() {
        return new IOType();
    }

    /**
     * Create an instance of {@link InOutput }
     * 
     */
    public InOutput createInOutput() {
        return new InOutput();
    }

    /**
     * Create an instance of {@link ControlAnswer }
     * 
     */
    public ControlAnswer createControlAnswer() {
        return new ControlAnswer();
    }

    /**
     * Create an instance of {@link Output }
     * 
     */
    public Output createOutput() {
        return new Output();
    }

    /**
     * Create an instance of {@link Variable }
     * 
     */
    public Variable createVariable() {
        return new Variable();
    }

    /**
     * Create an instance of {@link Lock }
     * 
     */
    public Lock createLock() {
        return new Lock();
    }

    /**
     * Create an instance of {@link Input }
     * 
     */
    public Input createInput() {
        return new Input();
    }

    /**
     * Create an instance of {@link ControlEvent }
     * 
     */
    public ControlEvent createControlEvent() {
        return new ControlEvent();
    }

    /**
     * Create an instance of {@link Peers }
     * 
     */
    public Peers createPeers() {
        return new Peers();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Input }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "input", scope = ControlAnswer.class)
    public JAXBElement<Input> createControlAnswerInput(Input value) {
        return new JAXBElement<Input>(_ControlAnswerInput_QNAME, Input.class, ControlAnswer.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Lock }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "unlock", scope = ControlAnswer.class)
    public JAXBElement<Lock> createControlAnswerUnlock(Lock value) {
        return new JAXBElement<Lock>(_ControlAnswerUnlock_QNAME, Lock.class, ControlAnswer.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Lock }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "lock", scope = ControlAnswer.class)
    public JAXBElement<Lock> createControlAnswerLock(Lock value) {
        return new JAXBElement<Lock>(_ControlAnswerLock_QNAME, Lock.class, ControlAnswer.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Output }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "output", scope = ControlAnswer.class)
    public JAXBElement<Output> createControlAnswerOutput(Output value) {
        return new JAXBElement<Output>(_ControlAnswerOutput_QNAME, Output.class, ControlAnswer.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InOutput }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "inoutput", scope = ControlAnswer.class)
    public JAXBElement<InOutput> createControlAnswerInoutput(InOutput value) {
        return new JAXBElement<InOutput>(_ControlAnswerInoutput_QNAME, InOutput.class, ControlAnswer.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Variable }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "variable", scope = ControlAnswer.class)
    public JAXBElement<Variable> createControlAnswerVariable(Variable value) {
        return new JAXBElement<Variable>(_ControlAnswerVariable_QNAME, Variable.class, ControlAnswer.class, value);
    }

}