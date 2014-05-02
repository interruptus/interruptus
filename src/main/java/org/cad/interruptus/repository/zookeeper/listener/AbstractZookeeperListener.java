package org.cad.interruptus.repository.zookeeper.listener;

import java.util.EnumSet;
import com.google.common.cache.Cache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.cad.interruptus.core.GsonSerializer;
import org.cad.interruptus.core.esper.EsperConfiguration;

abstract public class AbstractZookeeperListener<E> implements PathChildrenCacheListener
{
    final Log logger = LogFactory.getLog(getClass());
    final EsperConfiguration<E> configuration;
    final GsonSerializer<E> serializer;
    final Cache<String, E> cache;
    final String rootPath;

    private static final EnumSet<PathChildrenCacheEvent.Type> allowedEvents = EnumSet.of(
        PathChildrenCacheEvent.Type.CHILD_ADDED,
        PathChildrenCacheEvent.Type.CHILD_UPDATED,
        PathChildrenCacheEvent.Type.CHILD_REMOVED
    );

    public AbstractZookeeperListener(final String path, final Cache<String, E> cache, final GsonSerializer<E> serializer, final EsperConfiguration<E> configuration)
    {
        this.rootPath      = path;
        this.cache         = cache;
        this.serializer    = serializer;
        this.configuration = configuration;
    }

    protected boolean isValid(E c)
    {
        return true;
    }

    @Override
    public void childEvent(CuratorFramework cf, PathChildrenCacheEvent pcce) throws Exception
    {
        if ( ! allowedEvents.contains(pcce.getType())) {
            logger.debug("Ignoring event : " + pcce.getType());

            return;
        }

        final ChildData eData = pcce.getData();
        final String path     = eData.getPath();

        logger.debug(String.format("%s : %s",  pcce.getType(), eData.getPath()));

        if (PathChildrenCacheEvent.Type.CHILD_REMOVED == pcce.getType()) {

            logger.debug("Removing entity : " + path);

            cache.invalidate(path);
            //configuration.remove(e);

            return;
        }

        final String data = new String(eData.getData());
        final E item      = serializer.fromJson(data);

        if (item == null) {
            logger.warn(String.format("Ignoring entity for path '%s', It cannot be NULL", path));

            return;
        }

        if ( ! isValid(item)) {
            logger.warn("Ignoring entity invalid entity : " + item);
            cache.invalidate(path);
            configuration.remove(item);

            return;
        }

        logger.debug("Storing entity : " + item);

        cache.put(path, item);
        configuration.save(item);
    }
}