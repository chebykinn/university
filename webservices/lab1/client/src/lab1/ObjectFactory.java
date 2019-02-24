
package lab1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the lab1 package. 
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

    private final static QName _GetPersonsResponse_QNAME = new QName("http://lab1.webservices.chebykin.org/", "getPersonsResponse");
    private final static QName _GetPersons_QNAME = new QName("http://lab1.webservices.chebykin.org/", "getPersons");
    private final static QName _InvalidFilterException_QNAME = new QName("http://lab1.webservices.chebykin.org/", "InvalidFilterException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: lab1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PersonFilter }
     * 
     */
    public PersonFilter createPersonFilter() {
        return new PersonFilter();
    }

    /**
     * Create an instance of {@link PersonFilter.Parameters }
     * 
     */
    public PersonFilter.Parameters createPersonFilterParameters() {
        return new PersonFilter.Parameters();
    }

    /**
     * Create an instance of {@link PersonServiceFault }
     * 
     */
    public PersonServiceFault createPersonServiceFault() {
        return new PersonServiceFault();
    }

    /**
     * Create an instance of {@link GetPersons }
     * 
     */
    public GetPersons createGetPersons() {
        return new GetPersons();
    }

    /**
     * Create an instance of {@link GetPersonsResponse }
     * 
     */
    public GetPersonsResponse createGetPersonsResponse() {
        return new GetPersonsResponse();
    }

    /**
     * Create an instance of {@link Person }
     * 
     */
    public Person createPerson() {
        return new Person();
    }

    /**
     * Create an instance of {@link PersonFilter.Parameters.Entry }
     * 
     */
    public PersonFilter.Parameters.Entry createPersonFilterParametersEntry() {
        return new PersonFilter.Parameters.Entry();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPersonsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://lab1.webservices.chebykin.org/", name = "getPersonsResponse")
    public JAXBElement<GetPersonsResponse> createGetPersonsResponse(GetPersonsResponse value) {
        return new JAXBElement<GetPersonsResponse>(_GetPersonsResponse_QNAME, GetPersonsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPersons }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://lab1.webservices.chebykin.org/", name = "getPersons")
    public JAXBElement<GetPersons> createGetPersons(GetPersons value) {
        return new JAXBElement<GetPersons>(_GetPersons_QNAME, GetPersons.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PersonServiceFault }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://lab1.webservices.chebykin.org/", name = "InvalidFilterException")
    public JAXBElement<PersonServiceFault> createInvalidFilterException(PersonServiceFault value) {
        return new JAXBElement<PersonServiceFault>(_InvalidFilterException_QNAME, PersonServiceFault.class, null, value);
    }

}
