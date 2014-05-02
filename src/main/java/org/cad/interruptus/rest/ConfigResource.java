package org.cad.interruptus.rest;

import com.espertech.esper.client.ConfigurationOperations;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EventType;

import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import org.cad.interruptus.entity.Statement;
import org.cad.interruptus.entity.Flow;
import org.cad.interruptus.entity.Type;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.cad.interruptus.repository.TypeRepository;
import org.cad.interruptus.repository.FlowRepository;
import org.cad.interruptus.repository.StatementRepository;

@Component
@Path("/config")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ConfigResource
{
    final private static Log log = LogFactory.getLog(ConfigResource.class);

    @Inject
    private EPServiceProvider epService;

    @Inject
    private TypeRepository typeRepository;
    
    @Inject
    private FlowRepository flowRepository;

    @Inject
    private StatementRepository statementRepository;

    @GET
    public HashMap getConfig() throws Exception
    {
        HashMap configMap           = new HashMap();
        List<Statement> statements  = statementRepository.findAll();
        List<Type> types            = typeRepository.findAll();
        List<Flow> flows            = flowRepository.findAll();

        configMap.put("statements", statements);
        configMap.put("types", types);
        configMap.put("flows", flows);

	return configMap;
    }

    @POST
    public Boolean applyConfig (HashMap newConfig) 
    {
        epService.getEPAdministrator().destroyAllStatements();

        ConfigurationOperations config = epService.getEPAdministrator().getConfiguration();
        EventType[] eventTypes         = config.getEventTypes();

        for (EventType eventType : eventTypes) {
            String eventName    = eventType.getName();
            config.removeEventType(eventName, true);
        }

	// @TODO Transform inbound hashmap into a set of config entities, push the config to the engine, persist the config (zookeeper would probably be a good idea to get engine synched across fail-over nodes) 
	return true;
    }
}
