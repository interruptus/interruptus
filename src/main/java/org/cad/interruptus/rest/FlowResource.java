package org.cad.interruptus.rest;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import org.cad.interruptus.core.esper.FlowConfiguration;
import org.cad.interruptus.entity.Flow;
import org.cad.interruptus.repository.EntityRepository;
import org.cad.interruptus.repository.FlowRepository;

@Singleton
@Path("/flow")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class FlowResource extends AbstractResource<String, Flow>
{
    @Inject
    private FlowRepository repository;

    @Inject
    private FlowConfiguration configuration;

    @POST
    @Path("/start")
    public Boolean start(Flow flow) throws Exception
    {
        return configuration.start(flow);
    }

    @Override
    protected EntityRepository<String, Flow> getRepository()
    {
        return repository;
    }
}