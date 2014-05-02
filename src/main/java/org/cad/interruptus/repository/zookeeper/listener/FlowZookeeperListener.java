package org.cad.interruptus.repository.zookeeper.listener;

import com.google.gson.Gson;
import com.google.common.cache.Cache;
import org.cad.interruptus.core.GsonSerializer;
import org.cad.interruptus.core.esper.EsperConfiguration;
import org.cad.interruptus.entity.Flow;

public class FlowZookeeperListener extends AbstractZookeeperListener<Flow>
{
    public FlowZookeeperListener(final String path, final Cache<String, Flow> entities, final Gson gson, final EsperConfiguration<Flow> configuration)
    {
        super(path, entities, new GsonSerializer<>(Flow.class, gson), configuration);
    }
}