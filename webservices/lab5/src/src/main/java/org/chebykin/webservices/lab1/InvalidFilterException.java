package org.chebykin.webservices.lab1;

import javax.xml.ws.WebFault;

public class InvalidFilterException extends Exception {
    private static final long serialVersionUID = -6647544772732631047L;
    public static InvalidFilterException DEFAULT_INSTANCE = new InvalidFilterException("Failed to filter persons");
    public InvalidFilterException(String message) {
        super(message);
    }

}
