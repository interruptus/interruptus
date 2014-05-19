package org.cad.interruptus.rest;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cad.interruptus.core.EntityNotFoundException;
import org.cad.interruptus.core.esper.TypeConfiguration;
import org.cad.interruptus.entity.Type;
import org.cad.interruptus.repository.TypeRepository;

@Singleton
@Path("/type")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class TypeResource
{
    @Inject
    TypeRepository repository;
    
    @Inject
    TypeConfiguration configuration;

    Log logger = LogFactory.getLog(getClass());

    @GET
    public List<Type> list()
    {
        try {
            return repository.findAll();
        } catch (Exception ex) {
            logger.error(this, ex);
            throw new ResourceException(Response.Status.SERVICE_UNAVAILABLE, ex.getMessage());
        }
    }

    @POST
    public Boolean save(Type entity)
    {
        try {

            configuration.save(entity);
            repository.save(entity);

            return Boolean.TRUE;
        } catch (Exception ex) {
            logger.error(this, ex);
            throw new ResourceException(Response.Status.SERVICE_UNAVAILABLE, ex.getMessage());
        }
    }

    @GET
    @Path("/{name}")
    public Type show(@PathParam("name") String name)
    {
        try {
            return repository.findById(name);
        } catch (EntityNotFoundException ex) {
            throw new ResourceException(ex);
        } catch (Exception ex) {
            logger.error(this, ex);
            throw new ResourceException(Response.Status.SERVICE_UNAVAILABLE, ex.getMessage());
        }
    }

    @DELETE
    @Path("/{name}")
    public Boolean remove(@PathParam("name") String name)
    {
        try {

            configuration.remove(name);
            repository.remove(name);

            return true;
        } catch (EntityNotFoundException ex) {
            throw new ResourceException(ex);
        } catch (Exception ex) {
            logger.error(this, ex);
            throw new ResourceException(Response.Status.SERVICE_UNAVAILABLE, ex.getMessage());
        }
    }
}