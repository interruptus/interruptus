package org.cad.interruptus.repository.zookeeper;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.data.Stat;
import org.cad.interruptus.core.GsonSerializer;
import org.cad.interruptus.entity.Configuration;
import org.cad.interruptus.entity.Entity;

public class ConfigurationManager
{
    final Log logger = LogFactory.getLog(getClass());
    final GsonSerializer<Configuration> serializer;
    final AtomicReference<Configuration> reference;
    final CuratorFramework client;
    final String path;

    public ConfigurationManager(final CuratorFramework client, final AtomicReference<Configuration> reference, final Gson gson, final String path)
    {
        this(client, reference, new GsonSerializer(Configuration.class, gson), path);
    }

    public ConfigurationManager(final CuratorFramework client, final AtomicReference<Configuration> reference, final GsonSerializer<Configuration> serializer, final String path)
    {
        this.client      = client;
        this.reference   = reference;
        this.serializer  = serializer;
        this.path        = path ;
    }

    protected <R> R mutex(final Callable<R> callable, final InterProcessLock mutex) throws Exception
    {
        try {
            mutex.acquire();

            return callable.call();
        } finally {
            mutex.release();
        }
    }

    protected <R> R mutex(final Callable<R> callable) throws Exception
    {
        return mutex(callable, new InterProcessMutex(client, this.path + ".lock"));
    }

    protected void flush() throws Exception
    {
        mutex(new Callable<Boolean>()
        {
            @Override
            public Boolean call() throws Exception
            {
                final String json = serializer.toJson(get());
                final Stat status = client.checkExists().forPath(path);

                if (status != null) {
                    client.setData()
                        .compressed()
                        .forPath(path, json.getBytes());

                    return true;
                }

                client.create()
                    .compressed()
                    .creatingParentsIfNeeded()
                    .forPath(path, json.getBytes());

                return true;
            }
        });
    }

    public synchronized void save(final Entity entity) throws Exception
    {
        if (entity == null) {
            throw new NullPointerException("Entity cannot be null");
        }

        get().put(entity);
    }

    public synchronized void remove(final Class<? extends Entity> clazz, final String id) throws Exception
    {
        if (id == null) {
            throw new NullPointerException("Entity identifier cannot be null");
        }

        get().remove(clazz, id);
    }

    public Configuration get() throws Exception
    {
        if (reference.get() == null) {
            return load();
        }

        return reference.get();
    }

    protected synchronized Configuration load() throws Exception
    {
        logger.debug("Loading configuration");

        if (reference.get() != null) {
            return reference.get();
        }

        if (client.checkExists().forPath(path) == null) {
            logger.debug("Config file not found");
            reference.set(new Configuration());

            return reference.get();
        }

        final byte[] data        = client.getData().forPath(path);
        final Configuration item = serializer.fromJson(new String(data));

        if (item == null) {
            logger.debug("Invalid config file");
            reference.set(new Configuration());

            return reference.get();
        }

        logger.debug("Storing configuration : " + item);
        reference.set(item);

        return item;
    }

    public <T> List<T> list(final Class<? extends T> clazz) throws Exception
    {
        return new ArrayList<>(map(clazz).values());
    }

    public <T> Map<String, T> map(Class<? extends T> clazz) throws Exception
    {
        final Configuration config  = get();
        final Map<String, T> result = (Map<String, T>) config.mapOf((Class<? extends Entity>) clazz);

        return result;
    }
}