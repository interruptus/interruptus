package org.cad.interruptus.repository.zookeeper.listener;

import com.google.gson.Gson;
import com.google.common.cache.Cache;
import org.cad.interruptus.core.GsonSerializer;
import org.cad.interruptus.core.esper.EsperConfiguration;
import org.cad.interruptus.entity.Type;

public class TypeZookeeperListener extends AbstractZookeeperListener<Type>
{
    public TypeZookeeperListener(final String path, final Cache<String, Type> entities, final Gson gson, final EsperConfiguration<Type> configuration)
    {
        super(path, entities, new GsonSerializer<>(Type.class, gson), configuration);
    }
}