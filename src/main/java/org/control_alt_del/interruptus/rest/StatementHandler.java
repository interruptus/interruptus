package org.control_alt_del.interruptus.rest;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EPStatementException;
import com.espertech.esper.client.EPStatementState;
import java.util.ArrayList;
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

@Component
@Path("/statement")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class StatementHandler
{
    final private static Log log = LogFactory.getLog(StatementHandler.class);

    @Autowired
    private EPAdministrator epAdministrator;

    // @TODO: Would be nice if statement state was pulled and pushed into the entity.... hrmmmm.....

    @GET
    public List<Statement> listStatements()
    {
        String[] statementNames     = epAdministrator.getStatementNames();
        List<Statement> statements  = new ArrayList<Statement>();

        for (String name : statementNames) {
            EPStatement epStatement = epAdministrator.getStatement(name);
            statements.add(new Statement(name, epStatement.getText(), false));
        }

        return statements;
    }

    @POST
    public Statement createStatement(Statement statement) throws EPStatementException
    {
        EPStatement epStatement = epAdministrator.createEPL(statement.getQuery(), statement.getName());
        statement.setName(epStatement.getName());

        log.info("Create statement : " + statement.getName());
        log.info("Statement debug  : " + statement.getDebug());

        if (statement.getDebug()) {
            epStatement.addListener(new UpdateListener()
            {
                public void update(EventBean[] newEvents, EventBean[] oldEvents)
                {
                    for (int i = 0; i < newEvents.length; i++) {
                        log.info(newEvents[i].getUnderlying());
                    }
                }
            });
        }

        return statement;
    }


    @Path("/start")
    @POST
    public Boolean startStatement(Statement statement)
    {
	try {
	        EPStatement epStatement = epAdministrator.getStatement(statement.getName());
        	if (epStatement != null) {
	            epStatement.start();
        	}
		return true;
	} catch (EPStatementException e) {
                log.info(e.getMessage());
		return false;
	}
    }
    @Path("/startAll")
    @GET
    public Boolean startAllStatements()
    {
	try {
	        epAdministrator.startAllStatements();
		return true;
	} catch (EPException e) {
		log.info(e.getMessage());
		return false;
	}
    }
    @Path("/stop")
    @POST
    public Boolean stopStatement(Statement statement)
    {
	try {
	        EPStatement epStatement = epAdministrator.getStatement(statement.getName());
        	if (epStatement != null) {
	            epStatement.stop();
        	}
		return true;
	} catch (EPStatementException e) {
                log.info(e.getMessage());
		return false;
	}
    }
    @Path("/stopAll")
    @GET
    public Boolean stopAllStatements()
    {
	try {
	        epAdministrator.stopAllStatements();
		return true;
	} catch (EPException e) {
		log.info(e.getMessage());
		return false;
	}
    }
    @Path("/destroyAll")
    @GET
    public Boolean destroyAllStatements() throws EPException
    {
	try {
	        epAdministrator.destroyAllStatements();
		return true;
	} catch (EPException e) {
                log.info(e.getMessage());
		return false;
	}
    }
    @Path("/state")
    @POST
    public EPStatementState getStatementState(Statement statement) throws EPStatementException
    {
	try {
	        EPStatement epStatement = epAdministrator.getStatement(statement.getName());
		if (epStatement != null) {
	        	return epStatement.getState();
		}
	} catch (EPStatementException e) {
		log.info(e.getMessage());
	}
	log.info("Statement: " +statement.getName()+" not found");
	return null;
    }

    @Path("/destroy")
    @POST
    public Boolean destroyStatement(Statement statement)
    {
        try {
	        EPStatement epStatement = epAdministrator.getStatement(statement.getName());
        	if (epStatement != null) {
            		epStatement.destroy();
        	}
		return true;
        } catch (EPStatementException e) {
                log.info(e.getMessage());
		return false;
        }
    }

}
