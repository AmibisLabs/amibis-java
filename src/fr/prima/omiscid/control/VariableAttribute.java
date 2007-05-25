/**
 * Copyright© 2005-2006 INRIA/Université Pierre Mendès-France/Université Joseph Fourier.
 *
 * O3MiSCID (aka OMiSCID) Software written by Sebastien Pesnel, Dominique
 * Vaufreydaz, Patrick Reignier, Remi Emonet and Julien Letessier.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package fr.prima.omiscid.control;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import fr.prima.omiscid.control.interf.VariableChangeListener;
import fr.prima.omiscid.control.message.answer.ControlAnswerItem;
import fr.prima.omiscid.control.message.answer.Variable;
import fr.prima.omiscid.control.message.answer.types.AccessType;
import fr.prima.omiscid.user.variable.VariableAccessType;
import java.util.Vector;

/**
 * Stores a variable description. The variable description is composed of a
 * value, a default value, a type description, a description for the format, a
 * kind of access (Read, Read-Write, Read-Write before init). The class gives
 * methods to generate xml string with the description. These strings are used
 * in exhange with the Control Server. It also accepts several listeners
 * interested in the variable modification. Note: VariableAttribute does not
 * contain the variable but only a description. The user must change the
 * description when the variable change, for example when the value is modified.
 * This coherence can be maintened with objects such as
 * {@link IntVariableAttribute}. IntVariableAttribute puts together the
 * description for an int variable and its integer value. It provides accessors
 * that maintains the coherence between the description and the integer when the
 * integer is modified.
 *
 * @see fr.prima.omiscid.control.ControlServer
 * @see fr.prima.omiscid.control.IntVariableAttribute
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
public class VariableAttribute extends Attribute {

    /** the value for the variable (string representation) */
    private String valueStr = null;

    /** the type for the variable */
    private String type = null;

    /** the default value for the variable (string representation) */
    private String defaultValue = null;

    /** the kind of access on the variable */
    private VariableAccessType accessKind = VariableAccessType.READ;

    /**
     * A set of listener interested in variable modification. A set of object
     * implementing the {@link VariableChangeListener} interface.
     */
    private Set<VariableChangeListener> listenersSet = new HashSet<VariableChangeListener>();

    /**
     * A set of peer interested in the variable modification. A set of Integer
     * object.
     */
    private Set<Integer> peerInterestedIn = new HashSet<Integer>();

    /**
     * Creates a new instance of VariableAttribute
     *
     * @param name
     *            the name of the variable
     */
    public VariableAttribute(String name) {
        super(name);
    }
    
    public VariableAttribute(String name, VariableAccessType variableAccessType, String value) {
        super(name);
        this.accessKind = variableAccessType;
        this.valueStr = value;
    }

    public VariableAttribute(Variable variable) {
        super(variable.getName());
        init(variable);
    }

    public synchronized void init(Variable variable) {
        this.setName(variable.getName());
        switch (variable.getAccess().getType()) {
        case AccessType.CONSTANT_TYPE: this.setAccessType(VariableAccessType.CONSTANT); break;
        case AccessType.READ_TYPE: this.setAccessType(VariableAccessType.READ); break;
        case AccessType.READWRITE_TYPE: this.setAccessType(VariableAccessType.READ_WRITE); break;
        default: System.err.println("unhandled variable type in VariableAttribute.init "+variable.getAccess());
        }
        this.setValueStr(variable.getValue());
        this.setDescription(variable.getDescription());
        this.setType(variable.getType());
        this.setDefaultValue(variable.getDefault());
        this.setFormatDescription(variable.getFormatDescription());
    }

    public synchronized ControlAnswerItem generateControlAnswer() {
        ControlAnswerItem controlAnswerItem = new ControlAnswerItem();
        Variable variable = new Variable();
        switch (getAccess()) {
        case CONSTANT: variable.setAccess(AccessType.CONSTANT); break;
        case READ: variable.setAccess(AccessType.READ); break;
        case READ_WRITE: variable.setAccess(AccessType.READWRITE); break;
        default: System.err.println("unhandled access type in VariableAttribute.generateControlAnswer "+getAccess());
        }
        variable.setDefault(getDefaultValue());
        variable.setDescription(getDescription());
        variable.setFormatDescription(getFormatDescription());
        variable.setName(getName());
        variable.setType(getType());
        variable.setValue(getValueStr());
        controlAnswerItem.setVariable(variable);
        return controlAnswerItem;
    }

    public synchronized void init(fr.prima.omiscid.control.message.servicexml.Variable variable) {
        this.setName(variable.getName());
        switch (variable.getAccess().getType()) {
        case fr.prima.omiscid.control.message.servicexml.types.AccessType.CONSTANT_TYPE: this.setAccessType(VariableAccessType.CONSTANT); break;
        case fr.prima.omiscid.control.message.servicexml.types.AccessType.READ_TYPE: this.setAccessType(VariableAccessType.READ); break;
        case fr.prima.omiscid.control.message.servicexml.types.AccessType.READWRITE_TYPE: this.setAccessType(VariableAccessType.READ_WRITE); break;
        default: System.err.println("unhandled variable type in VariableAttribute.init "+variable.getAccess());
        }
        this.setDefaultValue(variable.getDefault());
        this.setDescription(variable.getDescription());
        this.setFormatDescription(variable.getFormatDescription());
        this.setType(variable.getType());
        this.setValueStr(variable.getValue());
    }


    public synchronized ControlAnswerItem generateShortControlAnswer() {
        ControlAnswerItem controlAnswerItem = new ControlAnswerItem();
        Variable variable = new Variable();
        variable.setName(getName());
        controlAnswerItem.setVariable(variable);
        return controlAnswerItem;
    }

    /**
     * Accesses the string value of the variable.
     *
     * @return the value contained in this VariableAttribute object
     */
    public synchronized String getValueStr() {
        return valueStr;
    }

    /**
     * Accesses the type of the variable.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Accesses the default value of the variable
     *
     * @return the default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Accesses the access rights set for the variable.
     *
     * @return the kind of access rights
     */
    public VariableAccessType getAccess() {
        return accessKind;
    }

    /**
     * Accesses the string representation of the access rights.
     *
     * @return the string associated to the kind of access
     */
    public String getAccessString() {
        return accessKind.getStringDescription();
    }

    /**
     * Tests whether the variable can be modified according to its access
     * rights. A variable can be modified if its kind of access is Read-Write.
     *
     *
     * @return whether the variable can be modified
     */
    public boolean canBeModified() {
        return (accessKind == VariableAccessType.READ_WRITE);
        //|| (accessKind == READ_WRITE_BEFORE_INIT && status != ControlServer.STATUS_RUNNING);
    }

    /**
     * Sets the value for the variable and notifies the listeners.
     *
     * @param str
     *            the new value for the variable
     */
    public synchronized void setValueStr(String str) {
        if (str == null ? valueStr != null : !str.equals(valueStr)) {
            valueStr = str;
            valueChanged();
        }
    }

    /**
     * Defines the type for the variable
     *
     * @param type
     *            the new type for the variable
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the default value for the variable.
     *
     * @param defaultValue
     *            the new default value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Sets the access rights for the variable.
     *
     * @param a
     *            the new kind of access
     */
    public void setAccessType(VariableAccessType a) {
        accessKind = a;
    }

    /**
     * Adds a listener interested in variable modification
     *
     * @param l
     *            the new listener
     */
    public void addListenerChange(VariableChangeListener l) {
        synchronized (listenersSet) {
            listenersSet.add(l);
        }
    }

    /**
     * Removes a listener no more interested in variable modification.
     *
     * @param l
     *            the listener to remove
     */
    public void removeListenerChange(VariableChangeListener l) {
        synchronized (listenersSet) {
            listenersSet.remove(l);
        }
    }

    /**
     * Signals to the listeners that the value has been modified. This method is
     * called in {@link VariableAttribute#setValueStr(String)}
     */
    private void valueChanged() {
        synchronized (listenersSet) {
            // make a copy to allow listeners to unregister themselve
            Vector<VariableChangeListener> copy = new Vector<VariableChangeListener>(listenersSet);
            for (VariableChangeListener listener : copy) {
                try {
                    listener.variableChanged(this);
                } catch (Exception e) {
                    System.err.println("Omiscid caught an exception thrown by a listener on variable modification notification, it is shown here:");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Adds the id of a peer interested in variable modification.
     *
     * @param pid
     *            the id of the peer
     */
    public void addPeer(int pid) {
        peerInterestedIn.add(new Integer(pid));
    }

    /**
     * Removes an id of a peer no more interested in variable modification.
     *
     * @param pid
     *            the id of the peer to remove
     */
    public void removePeer(int pid) {
        peerInterestedIn.remove(new Integer(pid));
    }

    /**
     * Removes an id of a peer no more interested in variable modification.
     *
     * @param p
     *            contains the id of the peer to remove
     */
    public void removePeer(Integer p) {
        peerInterestedIn.remove(p);
    }

    /**
     * Removes all specified ids of a peer no more interested in variable
     * modification.
     *
     * @param p
     *            contains the ids of the peers to remove
     */
    public boolean removeAllPeers(Collection<Integer> c) {
        return peerInterestedIn.removeAll(c);
    }

    /**
     * Accesses to a read only list of peer interested in the variable
     * modification.
     *
     * @return a read only set of Integer object. Their value are the ids of
     *         peers.
     */
    public Set<Integer> getPeerInterestedIn() {
        return Collections.unmodifiableSet(peerInterestedIn);
    }

    /**
     * Generates a XML message containing the variable name and its value. The
     * message has the following form: <br>
     * &lt;variable name=&quot;variable_name&quot;&gt;<br>
     * &lt;value&gt;&lt;![CDATA[variable_value]]&gt;&lt;/value&gt;<br>
     * &lt;/variable&gt;
     *
     * @return the generated XML message
     */
    public String generateValueMessage() {
        String str = generateHeaderDescription("variable", false);
        str += "<value>" + XmlUtils.generateCDataSection(getValueStr()) + "</value>";
        str += "</variable>";
        return str;
    }

    /**
     * Returns the name of the variable. To get a more complete description to
     * display, {@link VariableAttribute#generateLongDescription()} can be used.
     *
     * @return the name of the variable
     */
    public String toString() {
        return getName();
    }

    /**
     * Defines the kind of access according to the string representation.
     */
    protected void setAccess(String accessStr) {
        if (accessStr.equals(VariableAccessType.READ.toString())) {
            setAccessType(VariableAccessType.READ);
        } else if (accessStr.equals(VariableAccessType.READ_WRITE.toString())) {
            setAccessType(VariableAccessType.READ_WRITE);
        } else if (accessStr.equals(VariableAccessType.CONSTANT.toString())) {
            setAccessType(VariableAccessType.CONSTANT);
        }
    }

}
