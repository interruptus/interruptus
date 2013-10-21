package org.control_alt_del.interruptus.core.zookeeper;

import org.control_alt_del.interruptus.entity.Statement;
import org.control_alt_del.interruptus.core.esper.StatementConfiguration;

public class StatementConfigurationListener extends AbstractConfigurationListener<Statement>
{
    public StatementConfigurationListener(String path, StatementConfiguration configuration)
    {
       super(path, configuration);
    }
}
