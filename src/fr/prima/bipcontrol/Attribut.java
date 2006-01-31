package fr.prima.bipcontrol;

/**
 * Mother class for Attribute of Service
 * 
 * @author Sebastien Pesnel
 * Refactoring by Patrick Reignier
 */
public abstract class Attribut {
    /** Name of the attribute */
    private String name = null;

    /** Description of the attribute */
    private String description = null;

    /** Format description of the attribute */
    private String formatDescription = null;

    /**
     * Create a new instance of Attribut
     * 
     * @param aName
     *            name for the attribute
     */
    protected Attribut(String aName) {
        name = aName;
    }

    /**
     * Define the description of the attribute
     * 
     * @param descr
     *            the description to affect
     */
    public void setDescription(String descr) {
        description = descr;
    }

    /**
     * Define the format description of the attribute
     * 
     * @param descr
     *            the description to affect
     */
    public void setFormatDescription(String descr) {
        formatDescription = descr;
    }

    /**
     * Access to the attribute name
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Access to the attribut description
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Access to the attribut description of format
     * 
     * @return formatDescription
     */
    public String getFormatDescription() {
        return formatDescription;
    }

    /**
     * Generate a brief description of the attribute
     * 
     * @return a short XML description of the attribute
     */
    public abstract String generateShortDescription();

    /**
     * Generate a complete description of the attribute
     * 
     * @return a long XML description of the attribute
     */
    public abstract String generateLongDescription();

    /**
     * Generate the tags about the description and format description for the
     * XML description. Generate tag when the description are not empty.
     * 
     * @return <description>+ getDescription() +</description> +
     *         <formatDescription>+ getFormatDescription() +</formatDescription>
     */
    protected String generateTagDescriptionToStr() {
        String str = "";
        if (getDescription() != null && !getDescription().equals(""))
            str = "<description>" + XmlUtils.generateCDataSection(getDescription())
                    + "</description>";
        if (getFormatDescription() != null
                && !getFormatDescription().equals(""))
            str += "<formatDescription>"
                    + XmlUtils.generateCDataSection(getFormatDescription())
                    + "</formatDescription>";
        return str;
    }

    /**
     * Generate the first tag of the XML description
     * 
     * @param kind
     *            the kind of attribute
     * @param end
     *            indicate if the tag have children or not. So the tag is
     *            finished by '>' or '/>'
     * @return <kind name="...">
     */
    protected String generateHeaderDescription(String kind, boolean end) {
        String str = "<";
        str += kind + " name=\"" + name + "\"";
        if (end)
            str += "/>";
        else
            str += ">";
        return str;
    }



    /**
     * String representation of the object
     * 
     * @return "Name : " + name +"\nDescr : " + description +"\n"
     */
    public String toString() {
        return "Name : " + getName() + "\nDescr : " + getDescription() + "\n";
    }

    /**
     * Compare two attributes
     * 
     * @return true if they have the same name
     */
    public boolean equals(Object o) {
        return (o == this)
                || (Attribut.class.isInstance(o) && (getName()
                        .equals((Attribut) o)));
    }
}
