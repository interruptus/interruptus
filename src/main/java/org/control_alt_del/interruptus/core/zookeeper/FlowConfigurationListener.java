package org.control_alt_del.interruptus.core.zookeeper;

import org.control_alt_del.interruptus.entity.Flow;
import org.control_alt_del.interruptus.core.esper.FlowConfiguration;

public class FlowConfigurationListener extends AbstractConfigurationListener<Flow>
{
    public FlowConfigurationListener(String path, FlowConfiguration configuration)
    {
        super(path, configuration);
    }
}
