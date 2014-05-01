package org.cad.interruptus.rest;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.espertech.esper.client.ConfigurationException;
import javax.ws.rs.PathParam;
import org.cad.interruptus.core.ZookeeperConfiguration;
import org.cad.interruptus.entity.Type;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
@Path("/type")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class TypeResource
{
    @Autowired
    private ZookeeperConfiguration zookeeper;

    @GET
    public List<Type> listEventTypes() throws Exception
    {
        return zookeeper.list(Type.class);
    }

    @POST
    public Type create(Type type) throws Exception
    {
        zookeeper.save(type);

        return type;
    }

    @GET
    @Path("/{name}")
    public Type get(@PathParam("name") String name) throws Exception
    {
        return zookeeper.get(Type.class, name);
    }

    @DELETE
    public Boolean destroy(Type type) throws ConfigurationException, Exception
    {
        return zookeeper.remove(type);
    }
}
