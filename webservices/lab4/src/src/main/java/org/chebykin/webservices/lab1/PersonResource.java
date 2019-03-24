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
                    PersonServiceFault fault = new PersonServiceFault();
                    fault.setMessage(e.getMessage());
                    throw new InvalidFilterException("Failed to filter persons", fault);
                }
            }
        }
        return outPersons;
    }
}