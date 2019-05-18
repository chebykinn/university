package org.chebykin.webservices.lab1;
import javax.xml.ws.WebFault;

@WebFault(faultBean = "org.chebykin.webservices.lab1.PersonServiceFault")
public class AuthException extends Exception {
    private PersonServiceFault fault;
    public AuthException(String message, PersonServiceFault fault) {
        super(message);
        this.fault = fault;
    }
    public AuthException(String message, PersonServiceFault fault, Throwable course) {
        super(message, course);
        this.fault = fault;
    }

    public PersonServiceFault getFaultInfo() {
        return fault;
    }
}

