package org.cad.interruptus.rest;

import com.espertech.esper.client.EPStatementState;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
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
@Api(value = "/statement", description = "Statement operations")
public class StatementResource
{
    @Inject
    StatementRepository repository;

    @Inject
    StatementConfiguration configuration;

    Log logger = LogFactory.getLog(getClass());

    @GET
    @ApiOperation(
        value = "List all statements",
        notes = "List all statements, whether is runnig or not",
        response = Statement.class,
        responseContainer = "List"
    )
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
    @ApiOperation(
        value = "Save a statement configuration",
        notes = "Save a statement configuration, if the statement already exists will be overwritten",
        response = Boolean.class
    )
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
    @ApiOperation(
        value = "Retreives a statement configuration",
        notes = "Retreives a statement configuration, throws exception if does not exists",
        response = Statement.class
    )
    @ApiResponses( {
        @ApiResponse(code = 404, message = "Flow doesn't exists")
    })
    public Statement show(@ApiParam(value = "Flow name to lookup for", required = true) @PathParam("name") String name)
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
    @ApiOperation(
        value = "Removes a statement configuration",
        notes = "Removes a statement configuration, throws exception if does not exists",
        response = Statement.class
    )
    @ApiResponses( {
        @ApiResponse(code = 404, message = "Flow doesn't exists")
    })
    public Boolean remove(@ApiParam(value = "Flow name to lookup for", required = true) @PathParam("name") String name)
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
    @ApiOperation(
        value = "Start a statement in esper",
        notes = "Stop a existing in esper, throws exception if does not exists",
        response = Statement.class
    )
    @ApiResponses( {
        @ApiResponse(code = 404, message = "Flow doesn't exists")
    })
    public Boolean startStatement(@ApiParam(value = "Flow name to lookup for", required = true) @PathParam("name") String name) throws Exception
    {
	return configuration.start(repository.findById(name));
    }

    @POST
    @Path("/{name}/stop")
    @ApiOperation(
        value = "Stop a statement in esper",
        notes = "Stop a existing statement in esper, throws exception if does not exists",
        response = Statement.class
    )
    @ApiResponses( {
        @ApiResponse(code = 404, message = "Flow doesn't exists")
    })
    public Boolean stopStatement(@ApiParam(value = "Flow name to lookup for", required = true) @PathParam("name") String name) throws Exception
    {
	return configuration.stop(repository.findById(name));
    }

    @GET
    @Path("/{name}/state")
    @ApiOperation(
        value = "Retrives the state for a statement",
        notes = "Retrives the state for a statement, throws exception if does not exists",
        response = Statement.class
    )
    @ApiResponses( {
        @ApiResponse(code = 404, message = "Flow doesn't exists")
    })
    public Map<String, String> getStatementState(@ApiParam(value = "Flow name to lookup for", required = true) @PathParam("name") String name) throws Exception
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
}