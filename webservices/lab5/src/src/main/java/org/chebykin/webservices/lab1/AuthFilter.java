package org.chebykin.webservices.lab1;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class AuthFilter implements ContainerRequestFilter {
    private static String LIST_PATH = "/rest/persons";
    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) throws WebApplicationException {
        if (containerRequest.getAbsolutePath().getPath().equals(LIST_PATH)
            && containerRequest.getMethod().equals("GET")) {
            return containerRequest;
        }
        String authCreds = containerRequest.getHeaderValue("Authorization");
        if(!AuthService.authenticate(authCreds)) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("AuthorizationFailure").build());
        }
        return containerRequest;
    }
}
