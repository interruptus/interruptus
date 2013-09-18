package org.control_alt_del.interruptus.rest;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.dataflow.EPDataFlowDescriptor;
import com.espertech.esper.client.dataflow.EPDataFlowInstance;
import com.espertech.esper.client.dataflow.EPDataFlowRuntime;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.control_alt_del.interruptus.entity.Flow;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
@Path("/flow")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class FlowHandler
{
    @Autowired
    private Configuration config;

    @Autowired
    private EPServiceProvider epService;

    @Autowired
    private EPAdministrator epAdministrator;

// @TODO:  It would be nice if the flow state was retrieved and pushed into the entity. Hrmmm....

    @GET
    public List<Flow> listFlows()
    {
        EPDataFlowRuntime flowRuntime = epService.getEPRuntime().getDataFlowRuntime();
        String[] dataFlowsNames       = flowRuntime.getDataFlows();
        List<Flow> list               = new ArrayList<Flow>();

        for (String name : dataFlowsNames) {
            EPDataFlowDescriptor descriptor = flowRuntime.getDataFlow(name);

            list.add(new Flow(name, descriptor.getStatementName()));
        }

        return list;
    }

    @POST
    public Flow createFlow(Flow flow)
    {
        epAdministrator.createEPL(flow.getQuery(), flow.getName());

        EPRuntime epRuntime             = epService.getEPRuntime();
        EPDataFlowRuntime flowRuntime   = epRuntime.getDataFlowRuntime();
        EPDataFlowInstance instance     = flowRuntime.instantiate(flow.getName());
        instance.start();
        return flow;
    }

// @TODO Kind of lame here... did it work? who knows!
    @DELETE
    public Boolean cancelFlow(Flow flow)
    {
        epService.getEPRuntime().getDataFlowRuntime().instantiate(flow.getName()).cancel();
        return true;
    }
    
}
