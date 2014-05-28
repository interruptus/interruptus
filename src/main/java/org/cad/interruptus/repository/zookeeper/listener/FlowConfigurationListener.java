package org.cad.interruptus.repository.zookeeper.listener;

import java.util.concurrent.atomic.AtomicBoolean;
import org.cad.interruptus.core.esper.EsperConfiguration;
import org.cad.interruptus.entity.Flow;

public class FlowConfigurationListener extends AbstractZookeeperListener<String, Flow>
{
    public FlowConfigurationListener(final EsperConfiguration<String, Flow> configuration, final AtomicBoolean isLeader)
    {
        super(configuration, isLeader);
    }
}
