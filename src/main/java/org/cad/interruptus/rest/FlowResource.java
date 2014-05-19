package org.cad.interruptus.rest;

import com.espertech.esper.client.EPStatementState;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.cad.interruptus.core.esper.FlowConfiguration;
import org.cad.interruptus.entity.Flow;
import org.cad.interruptus.repository.FlowRepository;

@Singleton
@Path("/flow")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class FlowResource
{
    @Inject
    FlowRepository repository;

    @Inject
    FlowConfiguration configuration;

    Log logger = LogFactory.getLog(getClass());

    @GET
    public List<Flow> list()
    {
        try {
            return repository.findAll();
        } catch (Exception ex) {
            logger.error(this, ex);
            throw new ResourceException(Response.Status.SERVICE_UNAVAILABLE, ex.getMessage());
        }
    }

    @POST
    public Boolean save(Flow entity)
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
    public Flow show(@PathParam("name") String name)
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

            configuration.stop(name);
            repository.remove(name);

            return true;
        } catch (EntityNotFoundException ex) {
            throw new ResourceException(ex);
        } catch (Exception ex) {
            logger.error(this, ex);
            throw new ResourceException(Response.Status.SERVICE_UNAVAILABLE, ex.getMessage());
        }
    }

    @POST
    @Path("/{name}/start")
    public Boolean start(@PathParam("name") String name) throws Exception
    {
        return configuration.start(name);
    }

    @POST
    @Path("/{name}/stop")
    public Boolean stop(@PathParam("name") String name) throws Exception
    {
        return configuration.stop(name);
    }

    @GET
    @Path("/{name}/state")
    public Map<String, String> state(@PathParam("name") String name) throws Exception
    {
        final Map<String, String> map = new HashMap<>();
        final EPStatementState state  = configuration.getFlowState(name);

        map.put("name", name);
        map.put("status", EPStatementState.STOPPED.toString());

        if (state != null) {
            map.put("status", state.toString());
        }

        if (repository.findById(name) == null) {
            throw new EntityNotFoundException(name);
        }

        return map;
    }
}