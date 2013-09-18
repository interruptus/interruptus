package org.control_alt_del.interruptus.rest;

import com.espertech.esper.client.ConfigurationOperations;
import com.espertech.esper.client.ConfigurationException;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EventType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.control_alt_del.interruptus.entity.Type;
import org.control_alt_del.interruptus.entity.TypeProperty;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
@Path("/type")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class TypeHandler
{
    @Autowired
    private EPServiceProvider epService;

    @GET
    public List<Type> listEventTypes()
    {
        ConfigurationOperations config  = epService.getEPAdministrator().getConfiguration();
        List<Type> list                 = new ArrayList<Type>();
        EventType[] eventTypes          = config.getEventTypes();


        for (EventType eventType : eventTypes) {

            String eventName    = eventType.getName();
            String[] properties = eventType.getPropertyNames();

            Type type = new Type(eventName, new ArrayList<TypeProperty>());

            for (String propertyName : properties) {
                String propertyType = eventType.getPropertyType(propertyName).getName();

                type.addProperties(new TypeProperty(propertyName, propertyType));
            }

            list.add(type);

        }

        return list;
    }

    @POST
    public Type createEventType(Type type)
    {
        ConfigurationOperations config  = epService.getEPAdministrator().getConfiguration();
        Map<String, Object> map         = new HashMap<String, Object>();

        for (TypeProperty property : type.getProperties()) {
            map.put(property.getName(), property.getType());
        }

        config.addEventType(type.getName(), map);

        return type;
    }

    @DELETE
    public Boolean removeEventType(Type type) throws ConfigurationException
    {
        ConfigurationOperations config  = epService.getEPAdministrator().getConfiguration();
        return config.removeEventType(type.getName(), true);
    }
}
