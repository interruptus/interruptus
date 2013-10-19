package org.control_alt_del.interruptus.rest;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.espertech.esper.client.ConfigurationException;
import org.control_alt_del.interruptus.core.ZookeeperConfiguration;
import org.control_alt_del.interruptus.core.esper.TypeConfiguration;
import org.control_alt_del.interruptus.entity.Type;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
@Path("/type")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class TypeHandler
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

    @DELETE
    public Boolean destroy(Type type) throws ConfigurationException, Exception
    {
        return zookeeper.remove(type);
    }
}
