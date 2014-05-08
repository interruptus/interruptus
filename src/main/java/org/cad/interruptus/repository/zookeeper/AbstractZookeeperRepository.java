package org.cad.interruptus.repository.zookeeper;

import com.google.common.cache.Cache;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.concurrent.Callable;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.cad.interruptus.core.EntityNotFoundException;
import org.cad.interruptus.core.GsonSerializer;
import org.cad.interruptus.repository.EntityRepository;

abstract public class AbstractZookeeperRepository<ID extends Serializable, E> implements EntityRepository<ID, E>
{
    final Log logger = LogFactory.getLog(getClass());
    final GsonSerializer<E> serializer;
    final Cache<String, E> cache;
    final CuratorFramework client;
    final String rootPath;

    long lastCacheHashCode = -1;

    public AbstractZookeeperRepository(CuratorFramework client, Cache<String, E> cache, GsonSerializer<E> serializer, String path)
    {
        this.rootPath    = path;
        this.cache       = cache;
        this.client      = client;
        this.serializer  = serializer;
    }

    protected String getEntityPath(final ID id)
    {
        return rootPath + "/" + id;
    }

    protected E createEntity(final byte[] data)
    {
        return serializer.fromJson(new String(data));
    }

    protected long getCacheHashCode()
    {
        long hashCode = 0;

        for (E item : cache.asMap().values()) {
            hashCode += System.identityHashCode(item);
        }

        return hashCode;
    }

    protected <R> R mutex(final Callable<R> callable, InterProcessLock mutex) throws Exception
    {
        try {
            mutex.acquire();

            return callable.call();
        } finally {
            mutex.release();
        }
    }

    protected <R> R mutex(final String path, final Callable<R> callable) throws Exception
    {
        return mutex(callable, new InterProcessMutex(client, path));
    }

    protected void save(final ID id, final E entity) throws Exception
    {
        final String json = serializer.toJson(entity);
        final String path = getEntityPath(id);

        mutex(path, new Callable<Boolean>()
        {
            @Override
            public Boolean call() throws Exception
            {
                lastCacheHashCode = -1;

                if (client.checkExists().forPath(path) != null) {
                    client.setData()
                        .forPath(path, json.getBytes());

                    return true;
                }

                client.create()
                    .creatingParentsIfNeeded()
                    .forPath(path, json.getBytes());

                return true;
            }
        });
    }

    @Override
    public Boolean remove(final ID id) throws Exception
    {
        final String path = getEntityPath(id);

        return mutex(path, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                if (client.checkExists().forPath(path) == null) {
                    throw new EntityNotFoundException(id.toString());
                }

                client.delete()
                    .guaranteed()
                    .deletingChildrenIfNeeded()
                    .forPath(path);

                lastCacheHashCode = -1;

                return true;
            }
        });
    }

    @Override
    public List<E> findAll() throws Exception
    {
        if (getCacheHashCode() == lastCacheHashCode) {
            return new ArrayList<>(cache.asMap().values());
        }

        return loadAll();
    }

    @Override
    public E findById(final ID id) throws Exception
    {
        final String path = getEntityPath(id);
        final E entity    = cache.getIfPresent(path);

        if (entity == null) {
            return load(path);
        }

        return entity;
    }

    protected synchronized List<E> loadAll() throws Exception
    {
        if (getCacheHashCode() == lastCacheHashCode) {
            return new ArrayList<>(cache.asMap().values());
        }

        final List<E> result = new ArrayList<>();

        if (client.checkExists().forPath(rootPath) == null) {
            return result;
        }

        logger.debug("Listing entities from zookeper");

        for (String name : client.getChildren().forPath(rootPath)) {
            final String path = rootPath + "/" + name;
            final E item      = load(path);

            if ( ! isValid(item)) {
                logger.debug("Ignoring entity : " + item);
                continue;
            }

            result.add(item);
        }

        long cacheHashCode = getCacheHashCode();
        lastCacheHashCode  = cacheHashCode > 0 ? cacheHashCode : -1;

        return result;
    }

    protected synchronized E load(final String path) throws Exception
    {
        if (client.checkExists().forPath(path) == null) {
            throw new EntityNotFoundException(path);
        }

        final E cached = cache.getIfPresent(path);

        if (isValid(cached)) {
            return cached;
        }

        cache.invalidate(path);

        final byte[] data = client.getData().forPath(path);
        final E item      = createEntity(data);

        if (isValid(item)) {
            logger.debug("Storing entity : " + item);
            cache.put(path, item);
        }

        return item;
    }

    protected boolean isValid(final E e)
    {
        return (e != null);
    }
}