package org.control_alt_del.interruptus.rest;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.ConfigurationOperations;
import com.espertech.esper.client.ConfigurationException;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EPStatementException;
import com.espertech.esper.client.EPStatementState;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import com.espertech.esper.client.dataflow.EPDataFlowDescriptor;
import com.espertech.esper.client.dataflow.EPDataFlowInstance;
import com.espertech.esper.client.dataflow.EPDataFlowRuntime;

import java.util.HashMap;
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
import org.springframework.beans.factory.annotation.Autowired;

import org.control_alt_del.interruptus.entity.Statement;
import org.control_alt_del.interruptus.entity.Flow;
import org.control_alt_del.interruptus.entity.Type;
import org.control_alt_del.interruptus.entity.TypeProperty;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

@Component
@Path("/config")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ConfigHandler
{
    final private static Log log = LogFactory.getLog(ConfigHandler.class);

    @Autowired
    private EPServiceProvider epService;

    @GET
    public HashMap saveConfig()
    {
        String[] statementNames        = epService.getEPAdministrator().getStatementNames();
        List<Statement> statementList  = new ArrayList<Statement>();

        ConfigurationOperations config = epService.getEPAdministrator().getConfiguration();
        List<Type> typeList            = new ArrayList<Type>();
        EventType[] eventTypes         = config.getEventTypes();

        EPDataFlowRuntime flowRuntime  = epService.getEPRuntime().getDataFlowRuntime();
        String[] dataFlowsNames        = flowRuntime.getDataFlows();
        List<Flow> flowList            = new ArrayList<Flow>();

        HashMap configMap = new HashMap();

        // Grab statements
        for (String name : statementNames) {
            EPStatement epStatement = epService.getEPAdministrator().getStatement(name);
            statementList.add(new Statement(name, epStatement.getText(), false));
        }

        configMap.put("statements", statementList);

        // Grab types
        for (EventType eventType : eventTypes) {

            String eventName    = eventType.getName();
            String[] properties = eventType.getPropertyNames();

            Type type = new Type(eventName, new ArrayList<TypeProperty>());

            for (String propertyName : properties) {
                String propertyType = eventType.getPropertyType(propertyName).getName();

                type.addProperties(new TypeProperty(propertyName, propertyType));
            }
            typeList.add(type);
        }

        configMap.put("types", typeList);

        // Grab flows
        for (String name : dataFlowsNames) {
            EPDataFlowDescriptor descriptor = flowRuntime.getDataFlow(name);
            flowList.add(new Flow(name, descriptor.getStatementName()));
        }
        configMap.put("flows", flowList);
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
