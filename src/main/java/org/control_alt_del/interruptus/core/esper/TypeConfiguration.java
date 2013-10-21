package org.control_alt_del.interruptus.core.esper;

import com.espertech.esper.client.ConfigurationOperations;
import com.espertech.esper.client.ConfigurationException;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EventType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.control_alt_del.interruptus.entity.Type;
import org.control_alt_del.interruptus.entity.TypeProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author fabio
 */
@Service("typeConfiguration")
public class TypeConfiguration implements EsperConfiguration<Type>
{
    @Autowired
    private EPServiceProvider epService;

    @Override
    public List<Type> list()
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

    @Override
    public Type create(Type type)
    {
        ConfigurationOperations config  = epService.getEPAdministrator().getConfiguration();
        Map<String, Object> map         = new HashMap<String, Object>();

        for (TypeProperty property : type.getProperties()) {
            map.put(property.getName(), property.getType());
        }

        config.addEventType(type.getName(), map);

        return type;
    }

    @Override
    public Boolean destroy(Type type) throws ConfigurationException
    {
        ConfigurationOperations config  = epService.getEPAdministrator().getConfiguration();

        return config.removeEventType(type.getName(), true);
    }

    @Override
    public Boolean exists(Type flow)
    {
        EPAdministrator administrator         = epService.getEPAdministrator();
        ConfigurationOperations configuration = administrator.getConfiguration();
        EventType eventType = configuration.getEventType(flow.getName());

        return eventType != null;
    }
}
