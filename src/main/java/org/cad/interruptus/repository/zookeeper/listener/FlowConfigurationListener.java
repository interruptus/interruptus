package org.cad.interruptus.repository.zookeeper.listener;

import org.cad.interruptus.core.esper.EsperConfiguration;
import org.cad.interruptus.entity.Flow;

public class FlowConfigurationListener extends AbstractZookeeperListener<String, Flow>
{
    public FlowConfigurationListener(final EsperConfiguration<String, Flow> configuration)
    {
        super(configuration);
    }
}
