package org.cad.interruptus.repository.zookeeper;

import com.google.common.cache.Cache;
import org.apache.curator.framework.CuratorFramework;
import com.google.gson.Gson;
import org.cad.interruptus.core.GsonSerializer;
import org.cad.interruptus.entity.Type;
import org.cad.interruptus.repository.TypeRepository;

public class TypeRepositoryZookeeper extends AbstractZookeeperRepository<String, Type> implements TypeRepository
{
    public TypeRepositoryZookeeper(CuratorFramework client, Cache<String, Type> cache, Gson gson, String path)
    {
        super(client, cache, new GsonSerializer<>(Type.class, gson), path);
    }

    public TypeRepositoryZookeeper(CuratorFramework client, Cache<String, Type> cache, GsonSerializer<Type> serializer, String path)
    {
        super(client, cache, serializer, path);
    }

    @Override
    public void save(Type entity) throws Exception
    {
        save(entity.getName(), entity);
    }
}