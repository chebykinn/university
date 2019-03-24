package lab1;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOMFeature;
import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class WebServiceClient {
    private static String readLine() throws IOException {
        BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
        String line = buffer.readLine().trim();
        return line;
    }

    private static PersonFilter readFilter(String message, String prompt) throws IOException {
        PersonFilter filter = new PersonFilter();
        PersonFilter.Parameters params = new PersonFilter.Parameters();
        params.entry = new ArrayList<>();
        String field;
        do {
            System.out.print(message + " (empty to execute)");
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
        return filter;
    }

    private static int readId(String message, String prompt) throws IOException {
        int id;
        System.out.print(message + " (empty to cancel)");
        System.out.print(prompt);
        String idStr = readLine();
        if(idStr.isEmpty()) {
            return -1;
        }
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("Failed to parse id");
            return -1;
        }
        return id;
    }

    public static void main(String[] args) throws IOException {
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
                System.out.println("update");
                System.out.println("upload");
                System.out.println("add");
                System.out.println("delete");
                continue;
            }
            if(cmd.equals("connect")) {
                try {
                    personService = new PersonService(url);
                } catch (WebServiceException e) {
                    System.out.println("Failed to connect to: " + url);
                }
                continue;
            }
            if(cmd.equals("disconnect")) {
                personService = null;
                continue;
            }
            if(cmd.equals("add")) {
                if(personService == null) {
                    System.out.println("run connect first");
                    continue;
                }
                PersonFilter filter = readFilter("Enter field name to set:", prompt);
                try {
                    System.out.println(personService.getPersonWebServicePort().addPerson(filter));
                } catch (InvalidFilterException e) {
                    System.out.println("error: " + e.getMessage() + ", info: " + e.getFaultInfo().getMessage());
                } catch (SqlException e) {
                    System.out.println("error: " + e.getMessage() + ", info: " + e.getFaultInfo().getMessage());
                }
                continue;
            }
            if(cmd.equals("update")) {
                if(personService == null) {
                    System.out.println("run connect first");
                    continue;
                }
                int id = readId("Enter id to update:", prompt);
                if(id < 0) {
                    continue;
                }
                PersonFilter filter = readFilter("Enter field name to update:", prompt);
                try {
                    System.out.println(personService.getPersonWebServicePort().updatePerson(id, filter));
                } catch (InvalidFilterException e) {
                    System.out.println("error: " + e.getMessage() + ", info: " + e.getFaultInfo().getMessage());
                } catch (SqlException e) {
                    System.out.println("error: " + e.getMessage() + ", info: " + e.getFaultInfo().getMessage());
                }
                continue;
            }
            if(cmd.equals("upload")) {
                if(personService == null) {
                    System.out.println("run connect first");
                    continue;
                }
                int id = readId("Enter id for avatar:", prompt);
                if(id < 0) {
                    continue;
                }
                final String avatarPath = "avatar.png";
                File file = new File(avatarPath);

                try {
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream inputStream = new BufferedInputStream(fis);
                    byte[] imageBytes = new byte[(int) file.length()];
                    inputStream.read(imageBytes);

                    personService.getPersonWebServicePort(new MTOMFeature()).uploadAvatar(id, imageBytes);

                    inputStream.close();
                    System.out.println("Avatar uploaded");
                } catch (IOException ex) {
                    System.out.println("error: " + ex.getMessage());
                } catch (SqlException ex) {
                    System.out.println("error: " + ex.getMessage() + ", info: " + ex.getFaultInfo().getMessage());
                }

                continue;
            }
            if(cmd.equals("delete")) {
                if(personService == null) {
                    System.out.println("run connect first");
                    continue;
                }
                int id = readId("Enter id to delete:", prompt);
                if(id < 0) {
                    continue;
                }
                try {
                    System.out.println(personService.getPersonWebServicePort().deletePerson(id));
                } catch (SqlException e) {
                    System.out.println("error: " + e.getMessage() + ", info: " + e.getFaultInfo().getMessage());
                }
                continue;
            }
            if(cmd.equals("get")) {
                if(personService == null) {
                    System.out.println("run connect first");
                    continue;
                }
                PersonFilter filter = readFilter("Add field to filter:", prompt);
                List<Person> persons = null;
                try {
                    persons = personService.getPersonWebServicePort().getPersons(filter);
                } catch (InvalidFilterException e) {
                    System.out.println("error: " + e.getMessage() + ", info: " + e.getFaultInfo().getMessage());
                } catch (SqlException e) {
                    System.out.println("error: " + e.getMessage() + ", info: " + e.getFaultInfo().getMessage());
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
