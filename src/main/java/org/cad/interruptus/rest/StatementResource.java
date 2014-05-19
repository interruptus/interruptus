package org.cad.interruptus.rest;

import com.espertech.esper.client.EPException;
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
import org.cad.interruptus.core.esper.StatementConfiguration;
import org.cad.interruptus.entity.Statement;
import org.cad.interruptus.repository.StatementRepository;

@Singleton
@Path("/statement")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class StatementResource
{
    @Inject
    StatementRepository repository;

    @Inject
    StatementConfiguration configuration;

    Log logger = LogFactory.getLog(getClass());

    @GET
    public List<Statement> list()
    {
        try {
            return repository.findAll();
        } catch (Exception ex) {
            logger.error(this, ex);
            throw new ResourceException(Response.Status.SERVICE_UNAVAILABLE, ex.getMessage());
        }
    }

    @POST
    public Boolean save(Statement entity)
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
    public Statement show(@PathParam("name") String name)
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

    @GET
    @Path("/startAll")
    public Boolean startAllStatements()
    {
	return configuration.startAll();
    }
    
    @POST
    @Path("/{name}/start")
    public Boolean startStatement(@PathParam("name") String name) throws Exception
    {
	return configuration.start(repository.findById(name));
    }

    @POST
    @Path("/{name}/stop")
    public Boolean stopStatement(@PathParam("name") String name) throws Exception
    {
	return configuration.stop(repository.findById(name));
    }

    @POST
    @Path("/stopAll")
    public Boolean stopAllStatements()
    {
	return configuration.stopAll();
    }

    @POST
    @Path("/destroyAll")
    public Boolean destroyAllStatements() throws EPException
    {
	return configuration.destroyAll();
    }

    @GET
    @Path("/{name}/state")
    public Map<String, String> getStatementState(@PathParam("name") String name) throws Exception
    {
        final Map<String, String> map = new HashMap<>();
        final EPStatementState state  = configuration.getStatementState(name);

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

    @POST
    @Path("/{name}/destroy")
    public Boolean destroyStatement(@PathParam("name") String name) throws Exception
    {
        return configuration.remove(name);
    }
}