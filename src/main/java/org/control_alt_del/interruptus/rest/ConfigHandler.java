package org.control_alt_del.interruptus.rest;

import com.espertech.esper.client.ConfigurationOperations;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EventType;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import org.control_alt_del.interruptus.entity.Statement;
import org.control_alt_del.interruptus.entity.Flow;
import org.control_alt_del.interruptus.entity.Type;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.control_alt_del.interruptus.core.esper.FlowConfiguration;
import org.control_alt_del.interruptus.core.esper.StatementConfiguration;
import org.control_alt_del.interruptus.core.esper.TypeConfiguration;

@Component
@Path("/config")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ConfigHandler
{
    final private static Log log = LogFactory.getLog(ConfigHandler.class);

    @Autowired
    private EPServiceProvider epService;

    @Autowired
    private FlowConfiguration flowConfig;

    @Autowired
    private StatementConfiguration statementConfig;

    @Autowired
    private TypeConfiguration typeConfig;

    @GET
    public HashMap getConfig()
    {
        HashMap configMap           = new HashMap();
        List<Statement> statements  = statementConfig.list();
        List<Type> types            = typeConfig.list();
        List<Flow> flows            = flowConfig.list();

        configMap.put("statements", statements);
        configMap.put("types", types);
        configMap.put("flows", flows);

	return configMap;
    }

    @POST
    public Boolean applyConfig (HashMap newConfig) {
        epService.getEPAdministrator().destroyAllStatements();
        ConfigurationOperations config = epService.getEPAdministrator().getConfiguration();
        List<Type> typeList            = new ArrayList<Type>();
        EventType[] eventTypes         = config.getEventTypes();

        for (EventType eventType : eventTypes) {
            String eventName    = eventType.getName();
            config.removeEventType(eventName, true);
        }

	// @TODO Transform inbound hashmap into a set of config entities, push the config to the engine, persist the config (zookeeper would probably be a good idea to get engine synched across fail-over nodes) 
	return true;
    }
}
