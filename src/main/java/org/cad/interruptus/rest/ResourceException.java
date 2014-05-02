package org.cad.interruptus.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.WebApplicationException;
import org.cad.interruptus.core.EntityNotFoundException;

public class ResourceException extends WebApplicationException
{
    public ResourceException(Status status, String message)
    {
        super(Response.status(status).entity(message)
            .type(MediaType.APPLICATION_JSON).build());
    }

    public ResourceException(EntityNotFoundException ex)
    {
        super(Response.status(Status.NOT_FOUND).entity(ex.getMessage())
            .type(MediaType.APPLICATION_JSON).build());
    }
}