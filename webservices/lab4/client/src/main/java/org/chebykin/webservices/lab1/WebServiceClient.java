package org.chebykin.webservices.lab1;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebServiceClient {
    private static String readLine() throws IOException {
        BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
        String line = buffer.readLine().trim();
        return line;
    }

    private static PersonFilter readFilter(String message, String prompt) throws IOException {
        PersonFilter filter = new PersonFilter();
        filter.parameters = new HashMap<>();
        String field;
        do {
            System.out.print(message + " (empty to execute)");
            System.out.print(prompt);
            field = readLine();
            if (field.isEmpty()) break;
            System.out.print("Value:");
            String value = readLine();
            filter.parameters.put(field, value);
        } while (true);
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
    private static final String URL = "http://localhost:8080/rest/persons";

    public static void main(String[] args) throws IOException {
        Client client = Client.create();

        String prompt = "> ";
        while(true) {
            System.out.print(prompt);
            String cmd = readLine();
            if (cmd.equals("quit")) {
                break;
            }
            if(cmd.equals("help")) {
                System.out.println("Commands:");
                System.out.println("help");
                System.out.println("quit");
                System.out.println("get");
                continue;
            }
            if(cmd.equals("get")) {
                PersonFilter filter = readFilter("Add field to filter:", prompt);
                WebResource webResource = client.resource(URL);
                for(Map.Entry<String, String> ent : filter.parameters.entrySet()) {
                    if (ent.getValue().isEmpty()) continue;
                    webResource = webResource.queryParam(ent.getKey(), ent.getValue());
                }
                ClientResponse response =
                        webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
                if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
                    throw new IllegalStateException("Request failed");
                }
                GenericType<List<Person>> type = new GenericType<List<Person>>() {};
                List<Person> persons = response.getEntity(type);
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
