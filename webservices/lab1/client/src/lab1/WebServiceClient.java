package lab1;

import javax.xml.ws.WebServiceException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WebServiceClient {
    private static String readLine() throws IOException {
        BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
        String line = buffer.readLine().trim();
        return line;
    }

    public static void main(String[] args) throws IOException {
        URL url_j2ee = new URL("http://localhost:8080/j2ee_war_exploded/PersonService?wsdl");
        URL url = new URL("http://localhost:8080/PersonService?wsdl");

        String prompt = "> ";
        PersonService personService = null;
        while(true) {
            System.out.print(prompt);
            String cmd = readLine();
            if (cmd.equals("quit")) {
                break;
            }
            if(cmd.equals("help")) {
                System.out.println("Commands:");
                System.out.println("connect");
                System.out.println("disconnect");
                System.out.println("help");
                System.out.println("quit");
                System.out.println("get");
                continue;
            }
            if(cmd.equals("connect")) {
                System.out.println("is standalone?:");
                boolean isStandalone = Boolean.parseBoolean(readLine());
                try {
                    personService = new PersonService(isStandalone ? url : url_j2ee);
                } catch (WebServiceException e) {
                    System.out.println("Failed to connect to: " + (isStandalone ? url : url_j2ee));
                }
                continue;
            }
            if(cmd.equals("disconnect")) {
                personService = null;
                continue;
            }
            if(cmd.equals("get")) {
                if(personService == null) {
                    System.out.println("run connect first");
                    continue;
                }
                PersonFilter filter = new PersonFilter();
                PersonFilter.Parameters params = new PersonFilter.Parameters();
                params.entry = new ArrayList<>();
                String field;
                do {
                    System.out.print("Add field to filter: (empty to execute)");
                    System.out.print(prompt);
                    field = readLine();
                    if (field.isEmpty()) break;
                    System.out.print("Value:");
                    String value = readLine();
                    PersonFilter.Parameters.Entry ent = new PersonFilter.Parameters.Entry();
                    ent.setKey(field);
                    ent.setValue(value);
                    params.entry.add(ent);
                } while (true);
                filter.setParameters(params);
                List<Person> persons;
                try {
                    persons = personService.getPersonWebServicePort().getPersons(filter);
                } catch (InvalidFilterException e) {
                    System.out.println("error: " + e.getMessage() + ", info: " + e.getFaultInfo().getMessage());
                    continue;
                }
                for (Person person : persons) {
                    System.out.println("name: " + person.getName() +
                            ", surname: " + person.getSurname() + ", job: " + person.getJob()
                            + ", city: " + person.getCity() + ", age: " + person.getAge());
                }
                System.out.println("Total persons: " + persons.size());


                continue;
            }
            System.out.println("No such command");


        }
    }
}
