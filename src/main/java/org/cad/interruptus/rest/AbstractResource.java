package org.cad.interruptus.rest;

import java.io.Serializable;
import java.util.List;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cad.interruptus.core.EntityNotFoundException;
import org.cad.interruptus.repository.EntityRepository;

public abstract class AbstractResource<ID extends Serializable, E>
{
    protected final Log logger = LogFactory.getLog(getClass());

    protected abstract EntityRepository<ID, E> getRepository();

    @GET
    public List<E> listAction()
    {
        try {
            return getRepository().findAll();
        } catch (Exception ex) {
            logger.error(this, ex);
            throw new ResourceException(Response.Status.SERVICE_UNAVAILABLE, ex.getMessage());
        }
    }

    @POST
    public Boolean saveAction(E entity)
    {
        try {
            getRepository().save(entity);

            return Boolean.TRUE;
        } catch (Exception ex) {
            logger.error(this, ex);
            throw new ResourceException(Response.Status.SERVICE_UNAVAILABLE, ex.getMessage());
        }
    }

    @GET
    @Path("/{id}")
    public E showAction(@PathParam("id") ID id)
    {
        try {
            return getRepository().findById(id);
        } catch (EntityNotFoundException ex) {
            throw new ResourceException(ex);
        } catch (Exception ex) {
            logger.error(this, ex);
            throw new ResourceException(Response.Status.SERVICE_UNAVAILABLE, ex.getMessage());
        }
    }

    @DELETE
    @Path("/{id}")
    public Boolean removeAction(@PathParam("id") ID id)
    {
        try {
            getRepository().remove(id);

            return true;
        } catch (EntityNotFoundException ex) {
            throw new ResourceException(ex);
        } catch (Exception ex) {
            logger.error(this, ex);
            throw new ResourceException(Response.Status.SERVICE_UNAVAILABLE, ex.getMessage());
        }
    }
}