package org.cad.interruptus.core.esper;

import com.espertech.esper.client.ConfigurationOperations;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EventType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cad.interruptus.entity.Type;

public class TypeConfiguration implements EsperConfiguration<Type>
{
    final EPServiceProvider epService;
    final Log logger = LogFactory.getLog(getClass());

    public TypeConfiguration(final EPServiceProvider epService)
    {
        this.epService = epService;
    }

    @Override
    public List<String> list()
    {
        final EPAdministrator admin           = epService.getEPAdministrator();
        final ConfigurationOperations config  = admin.getConfiguration();
        final EventType[] eventTypes          = config.getEventTypes();
        final List<String> list               = new ArrayList<>();

        for (final EventType type : eventTypes) {
            list.add(type.getName());
        }

        return list;
    }

    @Override
    public void save(final Type type)
    {
        final Map<String, Object> map         = new HashMap<String, Object>(type.getProperties());
        final EPAdministrator admin           = epService.getEPAdministrator();
        final ConfigurationOperations config  = admin.getConfiguration();
        final String name                     = type.getName();

        logger.info("Saving type : " + name);

        if (config.isEventTypeExists(name)) {
            config.updateMapEventType(name, map);

            return;
        }

        config.addEventType(name, map);
    }

    @Override
    public Boolean remove(final String name)
    {
        final EPAdministrator administrator   = epService.getEPAdministrator();
        final ConfigurationOperations config  = administrator.getConfiguration();
        
        if ( ! config.isEventTypeExists(name)) {
            return true;
        }

        logger.info("Removing type : " + name);

        return config.removeEventType(name, true);
    }

    @Override
    public Boolean start(String name)
    {
        return false;
    }

    @Override
    public Boolean stop(String name)
    {
        return false;
    }
}
