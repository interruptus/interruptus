package org.cad.interruptus.rest;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.cad.interruptus.core.ZookeeperConfiguration;
import org.cad.interruptus.core.esper.FlowConfiguration;
import org.cad.interruptus.entity.Flow;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
@Path("/flow")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class FlowResource
{
    @Autowired
    private ZookeeperConfiguration zookeeper;

    @Autowired
    private FlowConfiguration configuration;

    @GET
    public List<Flow> list() throws Exception
    {
        return zookeeper.list(Flow.class);
    }

    @POST
    public Flow create(Flow flow) throws Exception
    {
        zookeeper.save(flow);

        return flow;
    }
    
    @GET
    @Path("/{name}")
    public Flow get(@PathParam("name") String name) throws Exception
    {
        return zookeeper.get(Flow.class, name);
    }

    @POST
    @Path("/start")
    public Boolean start(Flow flow) throws Exception
    {
        return configuration.start(flow);
    }

    @DELETE
    public Boolean destroy(Flow flow) throws Exception
    {
        zookeeper.remove(flow);

        return true;
    }
    
}
