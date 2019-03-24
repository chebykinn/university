package org.chebykin.webservices.lab1;

public class PersonServiceFault {
    private static final String DEFAULT_MESSAGE = "personName cannot be null or empty";
    protected String message;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public static PersonServiceFault defaultInstance() {
        PersonServiceFault fault = new PersonServiceFault();
        fault.message = DEFAULT_MESSAGE;
        return fault;
    }
}
