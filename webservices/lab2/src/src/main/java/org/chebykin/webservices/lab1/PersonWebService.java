package org.chebykin.webservices.lab1;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.MTOM;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@WebService(serviceName = "PersonService")
@MTOM
public class PersonWebService {
    @Resource
    private WebServiceContext context;
    PostgreSQLDAO getDAO() throws SqlException {
        PostgreSQLDAO dao;
        try {
            dao = new PostgreSQLDAO(getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
            PersonServiceFault fault = new PersonServiceFault();
            fault.setMessage("SQL Error: " + e.getMessage());
            throw new SqlException("SQL Error occurred", fault);
        }
        return dao;
    }

    private void doAuthentication() throws AuthException {
        MessageContext mctx = context.getMessageContext();

        //get detail from request headers
        Map<String, Object> http_headers = (Map<String, Object>) mctx.get(MessageContext.HTTP_REQUEST_HEADERS);
        List<String> credentials = (List<String>) http_headers.get("Authorization");

        if (credentials == null ||
                credentials.isEmpty() ||
                !(new String(Base64.getDecoder().decode(credentials.get(0)))).equals("test:test")) {
            throw new AuthException("Invalid credentials", PersonServiceFault.defaultInstance());
        }
    }

    @WebMethod(operationName = "getPersons")
    public List<Person> getPersons(@WebParam(name="fieldsAndValues") PersonFilter fieldsAndValues) throws InvalidFilterException, SqlException {
        PostgreSQLDAO dao = getDAO();
        List<Person> persons = dao.getPersons();
        if(fieldsAndValues.parameters == null || fieldsAndValues.parameters.isEmpty()) {
            return persons;
        }
        List<Person> outPersons = new ArrayList<>(persons);
        for(Person p : persons) {
            for (Map.Entry<String, String> entry : fieldsAndValues.parameters.entrySet()) {
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

    @WebMethod(operationName = "addPerson")
    public long addPerson(@WebParam(name="fieldsAndValues") PersonFilter fieldsAndValues) throws InvalidFilterException, SqlException, AuthException {
        doAuthentication();
        PostgreSQLDAO dao = getDAO();
        checkParams(fieldsAndValues);
        return dao.addPerson(fieldsAndValues.parameters);
    }

    @WebMethod(operationName = "updatePerson")
    public boolean updatePerson(@WebParam(name="id") int id, @WebParam(name="fieldsAndValues") PersonFilter fieldsAndValues) throws InvalidFilterException, SqlException, AuthException {
        doAuthentication();
        PostgreSQLDAO dao = getDAO();
        checkParams(fieldsAndValues);
        return dao.updatePerson(id, fieldsAndValues.parameters);
    }

    @WebMethod(operationName = "deletePerson")
    public boolean deletePerson(@WebParam(name="id") int id) throws SqlException, AuthException {
        doAuthentication();
        PostgreSQLDAO dao = getDAO();
        return dao.deletePerson(id);
    }

    @WebMethod(operationName = "uploadAvatar")
    public boolean uploadAvatar(@WebParam(name="id") int id, @WebParam(name="image") byte[] image) throws SqlException, AuthException {
        doAuthentication();
        PostgreSQLDAO dao = getDAO();
        return dao.uploadAvatar(id, image);
    }

    private void checkParams(@WebParam(name = "fieldsAndValues") PersonFilter fieldsAndValues) throws InvalidFilterException {
        if(fieldsAndValues.parameters == null || fieldsAndValues.parameters.isEmpty()) {
            PersonServiceFault fault = new PersonServiceFault();
            fault.setMessage("field parameters are null or empty");
            throw new InvalidFilterException("Failed to add person", fault);
        }
    }

    private Connection getConnection() {
        return ConnectionUtil.getConnection();
    }
}
