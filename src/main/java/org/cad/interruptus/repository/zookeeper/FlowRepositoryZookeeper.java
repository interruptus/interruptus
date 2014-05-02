package org.cad.interruptus.repository.zookeeper;

import com.google.common.cache.Cache;
import org.apache.curator.framework.CuratorFramework;
import com.google.gson.Gson;
import org.cad.interruptus.core.GsonSerializer;
import org.cad.interruptus.entity.Flow;
import org.cad.interruptus.repository.FlowRepository;

public class FlowRepositoryZookeeper extends AbstractZookeeperRepository<String, Flow> implements FlowRepository
{
    public FlowRepositoryZookeeper(CuratorFramework client, Cache<String, Flow> cache, Gson gson, String path)
    {
        super(client, cache, new GsonSerializer<>(Flow.class, gson), path);
    }

    public FlowRepositoryZookeeper(CuratorFramework client, Cache<String, Flow> cache, GsonSerializer<Flow> serializer, String path)
    {
        super(client, cache, serializer, path);
    }

    @Override
    public void save(Flow entity) throws Exception
    {
        save(entity.getName(), entity);
    }
}