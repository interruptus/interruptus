package org.cad.interruptus.core.zookeeper;

import com.google.gson.Gson;
import org.cad.interruptus.entity.Flow;
import org.cad.interruptus.core.esper.FlowConfiguration;

public class FlowConfigurationListener extends AbstractConfigurationListener<Flow>
{
    public FlowConfigurationListener(String path, FlowConfiguration configuration, final Gson gson)
    {
        super(path, configuration, gson);
    }
}
