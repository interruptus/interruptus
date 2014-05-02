package org.cad.interruptus.repository.zookeeper;

import com.google.common.cache.Cache;
import org.apache.curator.framework.CuratorFramework;
import com.google.gson.Gson;
import org.cad.interruptus.core.GsonSerializer;
import org.cad.interruptus.entity.Statement;
import org.cad.interruptus.repository.StatementRepository;

public class StatementRepositoryZookeeper extends AbstractZookeeperRepository<String, Statement> implements StatementRepository
{
    public StatementRepositoryZookeeper(CuratorFramework client, Cache<String, Statement> cache, Gson gson, String path)
    {
        super(client, cache, new GsonSerializer<>(Statement.class, gson), path);
    }

    public StatementRepositoryZookeeper(CuratorFramework client, Cache<String, Statement> cache, GsonSerializer<Statement> serializer, String path)
    {
        super(client, cache, serializer, path);
    }

    @Override
    public void save(Statement entity) throws Exception
    {
        save(entity.getName(), entity);
    }
}