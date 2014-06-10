package org.cad.interruptus.repository.zookeeper.listener;

import java.util.concurrent.atomic.AtomicBoolean;
import org.cad.interruptus.core.esper.EsperConfiguration;
import org.cad.interruptus.entity.Statement;

public class StatementConfigurationListener extends AbstractZookeeperListener<Statement>
{
    public StatementConfigurationListener(final EsperConfiguration<Statement> configuration, final AtomicBoolean isLeader)
    {
        super(configuration, isLeader);
    }
}
