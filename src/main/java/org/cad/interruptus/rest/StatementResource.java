package org.cad.interruptus.rest;

import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EPStatementState;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import org.cad.interruptus.core.EntityNotFoundException;
import org.cad.interruptus.core.esper.StatementConfiguration;
import org.cad.interruptus.entity.Statement;
import org.cad.interruptus.repository.EntityRepository;
import org.cad.interruptus.repository.StatementRepository;

@Singleton
@Path("/statement")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class StatementResource extends AbstractResource<String, Statement>
{
    @Inject
    private StatementRepository repository;

    @Inject
    private StatementConfiguration configuration;

    @Override
    protected EntityRepository<String, Statement> getRepository()
    {
        return repository;
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

        if (state == null) {
            throw new EntityNotFoundException(name);
        }

        map.put("name", name);
        map.put("status", state.toString());

	return map;
    }

    @POST
    @Path("/{name}/destroy")
    public Boolean destroyStatement(@PathParam("name") String name) throws Exception
    {
        return configuration.remove(repository.findById(name));
    }
}