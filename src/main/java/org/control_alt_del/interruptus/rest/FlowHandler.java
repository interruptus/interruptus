package org.control_alt_del.interruptus.rest;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.control_alt_del.interruptus.core.ZookeeperConfiguration;
import org.control_alt_del.interruptus.core.esper.FlowConfiguration;
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
    private ZookeeperConfiguration zookeeper;

    @Autowired
    private FlowConfiguration configuration;

    @GET
    public List<Flow> list()
    {
        return configuration.list();
    }

    @POST
    public Flow create(Flow flow) throws Exception
    {
        zookeeper.save(flow);

        return flow;
    }

    @DELETE
    public Boolean destroy(Flow flow) throws Exception
    {
        zookeeper.remove(flow);

        return true;
    }
    
}
