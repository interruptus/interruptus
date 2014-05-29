package org.cad.interruptus.core.esper;

import com.espertech.esper.client.ConfigurationOperations;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EventType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cad.interruptus.entity.Type;

public class TypeConfiguration implements EsperConfiguration<String, Type>
{
    private final EPServiceProvider epService;

    public TypeConfiguration(final EPServiceProvider epService)
    {
        this.epService = epService;
    }

    @Override
    public List<Type> list()
    {
        final ConfigurationOperations config  = epService.getEPAdministrator().getConfiguration();
        final List<Type> list                 = new ArrayList<>();
        final EventType[] eventTypes          = config.getEventTypes();


        for (EventType eventType : eventTypes) {

            final String eventName    = eventType.getName();
            final String[] properties = eventType.getPropertyNames();
            final Type type           = new Type(eventName, new HashMap<String, String>());

            for (String propertyName : properties) {
                String propertyType = eventType.getPropertyType(propertyName).getName();

                type.setProperty(propertyName, propertyType);
            }

            list.add(type);

        }

        return list;
    }

    @Override
    public void save(final Type type)
    {
        final ConfigurationOperations config  = epService.getEPAdministrator().getConfiguration();
        final Map<String, Object> map         = new HashMap<String, Object>(type.getProperties());
        final String name                     = type.getName();

        if (config.isEventTypeExists(name)) {
            config.updateMapEventType(name, map);

            return;
        }

        config.addEventType(name, map);
    }

    @Override
    public Boolean remove(final String name)
    {
        final ConfigurationOperations config  = epService.getEPAdministrator().getConfiguration();

        return config.removeEventType(name, true);
    }

    @Override
    public Boolean remove(final Type e)
    {
        return remove(e.getName());
    }

    @Override
    public Boolean exists(final String name)
    {
        final EPAdministrator administrator         = epService.getEPAdministrator();
        final ConfigurationOperations configuration = administrator.getConfiguration();
        final EventType eventType                   = configuration.getEventType(name);

        return eventType != null;
    }

    @Override
    public Boolean start(String id)
    {
        return false;
    }

    @Override
    public Boolean stop(String id)
    {
        return false;
    }
}
