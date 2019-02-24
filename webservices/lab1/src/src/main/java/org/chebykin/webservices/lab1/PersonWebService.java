package org.chebykin.webservices.lab1;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebService(serviceName = "PersonService")
public class PersonWebService {
    private boolean isStandalone = false;
    public void setStandalone() {
        isStandalone = true;
    }
    @Resource(lookup = "jdbc/ifmo-ws")
    private DataSource dataSource;

    @WebMethod(operationName = "getPersons")
    public List<Person> getPersons(@WebParam(name="fieldsAndValues") PersonFilter fieldsAndValues) throws InvalidFilterException {
        PostgreSQLDAO dao = new PostgreSQLDAO(getConnection());
        List<Person> persons = dao.getPersons();
        if(fieldsAndValues.parameters == null) {
            return persons;
        }
        if(fieldsAndValues.parameters.isEmpty()) {
            return persons;
        }
        List<Person> outPersons = new ArrayList<>();
        for(Person p : persons) {
            for (Map.Entry<String, String> entry : fieldsAndValues.parameters.entrySet()) {
                try {
                    String val = p.getFieldValue(entry.getKey());
                    if(!val.equals(entry.getValue())) {
                        continue;
                    }
                    outPersons.add(p);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    PersonServiceFault fault = new PersonServiceFault();
                    fault.setMessage(e.getMessage());
                    throw new InvalidFilterException("Failed to filter persons", fault);
                }

            }
        }
        return outPersons;
    }
    private Connection getConnection() {
        Connection result = null;
        try {
            result = isStandalone ? ConnectionUtil.getConnection() : dataSource.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(PersonWebService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
