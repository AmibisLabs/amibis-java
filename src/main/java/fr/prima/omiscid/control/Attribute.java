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

/**
 * Represents a service attribute: input/output connectors and variables. Abstract
 * base class for all services attributes (connectors and variables).
 *
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
public abstract class Attribute {
    /** Name of the attribute */
    private String name = null;

    /** Description of the attribute */
    private String description = null;

    /**
     * Creates a new instance of Attribute.
     *
     * @param name
     *            name for the attribute
     */
    protected Attribute(String name) {
        this.name = name;
    }

    /**
     * Sets the description of the attribute
     *
     * @param description
     *            the new description to affect to the attribute
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Accesses to the attribute name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Accesses to the attribute description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }



//    /**
//     * Generates a brief description for the attribute (in XML string).
//     *
//     * @return a short XML description of the attribute
//     */
//    public abstract String generateShortDescription();

//    /**
//     * Generates a complete description for the attribute (in XML string).
//     *
//     * @return a long XML description of the attribute
//     */
//    public abstract String generateLongDescription();


    /**
     * Generates the topmost tag of the XML description.
     *
     * @param kind
     *            the kind of attribute
     * @param childless
     *            indicate if the tag have children or not. So the tag is
     *            finished by '>' or '/>'
     * @return <kind name="...">
     */
    protected String generateHeaderDescription(String kind, boolean childless) {
        String str = "<";
        str += kind + " name=\"" + name + "\"";
        if (childless) {
            str += "/>";
        } else {
            str += ">";
        }
        return str;
    }

    /**
     * String representation of the object.
     *
     * @return "Name : " + name +"\nDescr : " + description +"\n"
     */
    public String toString() {
        return "Name : " + getName() + "\nDescr : " + getDescription() + "\n";
    }

    /**
     * Compares two attributes.
     *
     * @return true if they have the same name
     */
    public boolean equals(Object o) {
        return (o == this) || (o instanceof Attribute && (getName().equals(((Attribute) o).getName())));
    }

    protected void setName(String name) {
        this.name = name;
    }
}
