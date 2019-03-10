package org.chebykin.webservices.lab1;

import javax.xml.ws.Endpoint;
import java.util.logging.*;

public class App {
    public static void main(String[] args) {
        //disable stacktraces in soap-message
        System.setProperty("com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace",
                "false");
        String url = "http://0.0.0.0:8080/PersonService";
        Handler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter());
        Logger.getGlobal().addHandler(handler);
        PersonWebService ws = new PersonWebService();
        ws.setStandalone();
        Endpoint.publish(url, ws);
    }
}
