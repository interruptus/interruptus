package org.cad.interruptus.repository.zookeeper.listener;

import java.util.EnumSet;
import com.google.common.cache.Cache;
import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.cad.interruptus.core.GsonSerializer;
import org.cad.interruptus.core.esper.EsperConfiguration;

abstract public class AbstractZookeeperListener<ID extends Serializable, E> implements PathChildrenCacheListener
{
    final Log logger = LogFactory.getLog(getClass());
    final EsperConfiguration<ID, E> configuration;
    final GsonSerializer<E> serializer;
    final Cache<String, E> cache;
    final String rootPath;

    private static final EnumSet<PathChildrenCacheEvent.Type> allowedEvents = EnumSet.of(
        PathChildrenCacheEvent.Type.CHILD_ADDED,
        PathChildrenCacheEvent.Type.CHILD_UPDATED,
        PathChildrenCacheEvent.Type.CHILD_REMOVED
    );

    public AbstractZookeeperListener(final String path, final Cache<String, E> cache, final GsonSerializer<E> serializer, final EsperConfiguration<ID, E> configuration)
    {
        this.rootPath      = path;
        this.cache         = cache;
        this.serializer    = serializer;
        this.configuration = configuration;
    }

    @Override
    public void childEvent(final CuratorFramework curator, final PathChildrenCacheEvent event) throws Exception
    {
        if ( ! allowedEvents.contains(event.getType())) {
            logger.debug("Ignoring event : " + event.getType());

            return;
        }

        final ChildData eData = event.getData();
        final String path     = eData.getPath();

        logger.debug(String.format("%s : %s",  event.getType(), eData.getPath()));

        if (PathChildrenCacheEvent.Type.CHILD_REMOVED == event.getType()) {
            
            final String data = new String(eData.getData());
            final E item      = serializer.fromJson(data);

            logger.debug("Removing entity : " + path);
            cache.invalidate(path);

            if (item == null) {
                logger.warn(String.format("Ignoring entity configuration for path '%s'", path));

                return;
            }

            configuration.remove(item);

            return;
        }

        final String data = new String(eData.getData());
        final E item      = serializer.fromJson(data);

        if (item == null) {
            logger.warn(String.format("Ignoring entity for path '%s', It cannot be NULL", path));

            return;
        }

        logger.debug("Storing entity : " + item);

        cache.put(path, item);
        configuration.save(item);
    }
}