package org.cad.interruptus.rest;

import com.espertech.esper.client.EPStatementState;
import com.espertech.esper.client.dataflow.EPDataFlowState;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import org.cad.interruptus.core.EntityNotFoundException;
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
    @Path("/{name}/start")
    public Boolean start(@PathParam("name") String name) throws Exception
    {
        return configuration.start(name);
    }

    @POST
    @Path("/{name}/stop")
    public Boolean stop(@PathParam("name") String name) throws Exception
    {
        return configuration.stop(name);
    }

    @GET
    @Path("/{name}/state")
    public Map<String, String> state(@PathParam("name") String name) throws Exception
    {
        final Map<String, String> map = new HashMap<>();
        final EPDataFlowState state   = configuration.getFlowState(name);

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

    @Override
    protected EntityRepository<String, Flow> getRepository()
    {
        return repository;
    }
}