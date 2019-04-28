package org.chebykin.webservices.lab1;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.sql.SQLException;

@Provider
public class SqlExceptionMapper implements ExceptionMapper<SQLException> {
    @Override
    public Response toResponse(SQLException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
}
