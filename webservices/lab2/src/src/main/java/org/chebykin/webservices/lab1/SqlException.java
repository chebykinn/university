package org.chebykin.webservices.lab1;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "org.chebykin.webservices.lab1.PersonServiceFault")
public class SqlException extends Exception {
    private PersonServiceFault fault;
    public SqlException(String message, PersonServiceFault fault) {
        super(message);
        this.fault = fault;
    }
    public SqlException(String message, PersonServiceFault fault, Throwable course) {
        super(message, course);
        this.fault = fault;
    }

    public PersonServiceFault getFaultInfo() {
        return fault;
    }
}

