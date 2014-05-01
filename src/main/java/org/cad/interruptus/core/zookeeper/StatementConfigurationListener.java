package org.cad.interruptus.core.zookeeper;

import com.google.gson.Gson;
import org.cad.interruptus.entity.Statement;
import org.cad.interruptus.core.esper.StatementConfiguration;

public class StatementConfigurationListener extends AbstractConfigurationListener<Statement>
{
    public StatementConfigurationListener(final String path, final StatementConfiguration configuration, final Gson gson)
    {
       super(path, configuration, gson);
    }
}
