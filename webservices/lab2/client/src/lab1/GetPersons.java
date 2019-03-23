
package lab1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getPersons complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getPersons">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fieldsAndValues" type="{http://lab1.webservices.chebykin.org/}personFilter" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getPersons", propOrder = {
    "fieldsAndValues"
})
public class GetPersons {

    protected PersonFilter fieldsAndValues;

    /**
     * Gets the value of the fieldsAndValues property.
     * 
     * @return
     *     possible object is
     *     {@link PersonFilter }
     *     
     */
    public PersonFilter getFieldsAndValues() {
        return fieldsAndValues;
    }

    /**
     * Sets the value of the fieldsAndValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonFilter }
     *     
     */
    public void setFieldsAndValues(PersonFilter value) {
        this.fieldsAndValues = value;
    }

}
