package org.cad.interruptus.repository.zookeeper.listener;

import org.cad.interruptus.core.esper.EsperConfiguration;
import org.cad.interruptus.entity.Type;

public class TypeConfigurationListener extends AbstractZookeeperListener<String, Type>
{
    public TypeConfigurationListener(final EsperConfiguration<String, Type> configuration)
    {
        super(configuration);
    }
}
