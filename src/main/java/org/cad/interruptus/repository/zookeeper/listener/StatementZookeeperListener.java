package org.cad.interruptus.repository.zookeeper.listener;

import com.google.gson.Gson;
import com.google.common.cache.Cache;
import org.cad.interruptus.core.GsonSerializer;
import org.cad.interruptus.core.esper.EsperConfiguration;
import org.cad.interruptus.entity.Statement;

public class StatementZookeeperListener extends AbstractZookeeperListener<String, Statement>
{
    public StatementZookeeperListener(final String path, final Cache<String, Statement> entities, final Gson gson, final EsperConfiguration<String, Statement> configuration)
    {
        super(path, entities, new GsonSerializer<>(Statement.class, gson), configuration);
    }
}