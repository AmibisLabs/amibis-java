package fr.prima.omiscid.control;

/**
 * Represents a service attribute: input/output channels and variables. Abstract
 * base class for all services attributes (channels and variables).
 * 
 * @author Sebastien Pesnel Refactoring by Patrick Reignier and emonet
 */
public abstract class Attribute {
    /** Name of the attribute */
    private String name = null;

    /** Description of the attribute */
    private String description = null;

    /** Format description of the attribute */
    private String formatDescription = null;

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
     * Sets the format description of the attribute.
     * 
     * @param formatDescription
     *            the description to affect to the format
     */
    public void setFormatDescription(String formatDescription) {
        this.formatDescription = formatDescription;
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

    /**
     * Accesses to the format description of the attribute.
     * 
     * @return formatDescription
     */
    public String getFormatDescription() {
        return formatDescription;
    }

    /**
     * Generates a brief description for the attribute (in XML string).
     * 
     * @return a short XML description of the attribute
     */
    public abstract String generateShortDescription();

    /**
     * Generates a complete description for the attribute (in XML string).
     * 
     * @return a long XML description of the attribute
     */
    public abstract String generateLongDescription();

    /**
     * Generates the tags about the description and format description for the
     * XML description. Generates tags only when the descriptions are not empty.
     * 
     * @return
     * 
     * <pre>
     * &lt;description&gt;+ getDescription() +&lt;/description&gt; +
     *          &lt;formatDescription&gt;+ getFormatDescription() +&lt;/formatDescription&gt;
     * </pre>
     */
    protected String generateTagDescriptionToStr() {
        String str = "";
        if (getDescription() != null && !getDescription().equals("")) {
            str = "<description>" + XmlUtils.generateCDataSection(getDescription()) + "</description>";
        }
        if (getFormatDescription() != null && !getFormatDescription().equals("")) {
            str += "<formatDescription>" + XmlUtils.generateCDataSection(getFormatDescription()) + "</formatDescription>";
        }
        return str;
    }

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
}
