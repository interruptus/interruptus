package org.cad.interruptus.repository.zookeeper.listener;

import java.util.concurrent.atomic.AtomicBoolean;
import org.cad.interruptus.core.esper.EsperConfiguration;
import org.cad.interruptus.entity.Type;

public class TypeConfigurationListener extends AbstractZookeeperListener<Type>
{
    public TypeConfigurationListener(final EsperConfiguration<Type> configuration, final AtomicBoolean isLeader)
    {
        super(configuration, isLeader);
    }
}
