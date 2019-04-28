package org.chebykin.webservices.lab1;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidFilterExceptionMapper implements ExceptionMapper<InvalidFilterException> {
    @Override
    public Response toResponse(InvalidFilterException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
}
