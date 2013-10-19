package org.control_alt_del.interruptus.rest;

import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EPStatementException;
import com.espertech.esper.client.EPStatementState;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.stereotype.Component;
import org.control_alt_del.interruptus.entity.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.control_alt_del.interruptus.core.ZookeeperConfiguration;
import org.control_alt_del.interruptus.core.esper.StatementConfiguration;

@Component
@Path("/statement")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class StatementHandler
{
    final private static Log log = LogFactory.getLog(StatementHandler.class);

    @Autowired
    private ZookeeperConfiguration zookeeper;

    @Autowired
    private StatementConfiguration configuration;

    @GET
    public List<Statement> listStatements() throws Exception
    {
        return zookeeper.list(Statement.class);
    }

    @POST
    public Statement createStatement(Statement statement) throws EPStatementException, Exception
    {
        configuration.create(statement);
        zookeeper.save(statement);

        return statement;
    }

    @DELETE
    public Statement remoteStatement(Statement statement) throws EPStatementException, Exception
    {
        configuration.create(statement);
        zookeeper.remove(statement);

        return statement;
    }


    @POST
    @Path("/start")
    public Boolean startStatement(Statement statement)
    {
	return configuration.start(statement);
    }

    @GET
    @Path("/startAll")
    public Boolean startAllStatements()
    {
	return configuration.startAll();
    }

    @POST
    @Path("/stop")
    public Boolean stopStatement(Statement statement)
    {
	return configuration.stop(statement);
    }

    @GET
    @Path("/stopAll")
    public Boolean stopAllStatements()
    {
	return configuration.stopAll();
    }

    @Path("/destroyAll")
    @GET
    public Boolean destroyAllStatements() throws EPException
    {
	return configuration.destroyAll();
    }

    @POST
    @Path("/state")
    public EPStatementState getStatementState(Statement statement) throws EPStatementException
    {
	return configuration.getStatementState(statement);
    }

    @POST
    @Path("/destroy")
    public Boolean destroyStatement(Statement statement)
    {
        return configuration.destroy(statement);
    }
}
