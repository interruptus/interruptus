package org.cad.interruptus.repository.zookeeper.listener;

import org.cad.interruptus.core.esper.EsperConfiguration;
import org.cad.interruptus.entity.Statement;

public class StatementConfigurationListener extends AbstractZookeeperListener<String, Statement>
{
    public StatementConfigurationListener(final EsperConfiguration<String, Statement> configuration)
    {
        super(configuration);
    }
}
