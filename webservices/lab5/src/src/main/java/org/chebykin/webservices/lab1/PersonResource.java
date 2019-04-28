package org.chebykin.webservices.lab1;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/persons")
@Produces({MediaType.APPLICATION_JSON})
public class PersonResource {
    @Resource(lookup = "jdbc/ifmo-ws")
    private DataSource dataSource;
    private Connection getConnection() {
        Connection result = null;
        try {
            result = dataSource == null ? ConnectionUtil.getConnection() : dataSource.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(PersonResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    @GET
    public List<Person> getPersons(
            @QueryParam("name") String name,
            @QueryParam("surname") String surname,
            @QueryParam("job") String job,
            @QueryParam("city") String city,
            @QueryParam("age") int age
    ) throws SQLException, InvalidFilterException {
        List<Person> persons = new PostgreSQLDAO(getConnection()).getPersons();
        PersonFilter filter = new PersonFilter();
        filter.parameters = new HashMap<>();
        if (name != null && !name.isEmpty()) {
            filter.parameters.put("name", name);
        }
        if (job != null && !job.isEmpty()) {
            filter.parameters.put("job", job);
        }
        if (city != null && !city.isEmpty()) {
            filter.parameters.put("city", city);
        }
        if (age > 0) {
            filter.parameters.put("age", Integer.toString(age));
        }
        List<Person> outPersons = new ArrayList<>(persons);
        for(Person p : persons) {
            for (Map.Entry<String, String> entry : filter.parameters.entrySet()) {
                try {
                    String val = p.getFieldValue(entry.getKey());
                    if(val.equals(entry.getValue())) {
                        continue;
                    }
                    outPersons.remove(p);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new InvalidFilterException("Failed to filter persons:" + e.getMessage());
                }
            }
        }
        return outPersons;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPerson(Person person) throws SQLException {
        PostgreSQLDAO dao = new PostgreSQLDAO(getConnection());
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("name", person.getName());
        parameters.put("surname", person.getSurname());
        parameters.put("job", person.getJob());
        parameters.put("city", person.getCity());
        parameters.put("age", Integer.toString(person.getAge()));
        int id = (int)dao.addPerson(parameters);
        return new Response(id, id > 0);
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePerson(@PathParam("id") int id, Person person) throws SQLException {
        PostgreSQLDAO dao = new PostgreSQLDAO(getConnection());
        HashMap<String, String> parameters = new HashMap<>();
        if(person.getName() != null && !person.getName().isEmpty()) {
            parameters.put("name", person.getName());
        }
        if(person.getSurname() != null && !person.getSurname().isEmpty()) {
            parameters.put("surname", person.getSurname());
        }
        if(person.getJob() != null && !person.getJob().isEmpty()) {
            parameters.put("job", person.getJob());
        }
        if(person.getCity() != null && !person.getCity().isEmpty()) {
            parameters.put("city", person.getCity());
        }
        if(person.getAge() > 0) {
            parameters.put("age", Integer.toString(person.getAge()));
        }
        boolean status = dao.updatePerson(id, parameters);
        return new Response(id, status);
    }

    @DELETE
    @Path("{id}")
    public Response deletePerson(@PathParam("id") int id) throws SQLException {
        PostgreSQLDAO dao = new PostgreSQLDAO(getConnection());
        boolean status = dao.deletePerson(id);
        return new Response(id, status);
    }
}
