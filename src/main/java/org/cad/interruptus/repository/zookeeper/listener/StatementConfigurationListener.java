package org.cad.interruptus.repository.zookeeper.listener;

import java.util.concurrent.atomic.AtomicBoolean;
import org.cad.interruptus.core.esper.EsperConfiguration;
import org.cad.interruptus.entity.Statement;

public class StatementConfigurationListener extends AbstractZookeeperListener<String, Statement>
{
    public StatementConfigurationListener(final EsperConfiguration<String, Statement> configuration, final AtomicBoolean isLeader)
    {
        super(configuration, isLeader);
    }
}
