package org.cad.interruptus.repository.zookeeper.listener;

import com.google.gson.Gson;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.cad.interruptus.core.GsonSerializer;
import org.cad.interruptus.entity.Configuration;

public class ConfigurationZookeeperListener implements PathChildrenCacheListener
{
    final Log logger = LogFactory.getLog(getClass());
    final AtomicReference<Configuration> reference;
    final GsonSerializer<Configuration> serializer;
    
    private static final EnumSet<PathChildrenCacheEvent.Type> allowedEvents = EnumSet.of(
        PathChildrenCacheEvent.Type.CHILD_ADDED,
        PathChildrenCacheEvent.Type.CHILD_UPDATED,
        PathChildrenCacheEvent.Type.CHILD_REMOVED
    );

    public ConfigurationZookeeperListener(final AtomicReference<Configuration> configuration, final Gson gson)
    {
        this(configuration, new GsonSerializer(Configuration.class, gson));
    }
    
    public ConfigurationZookeeperListener(final AtomicReference<Configuration> configuration, final GsonSerializer<Configuration> serializer)
    {
        this.reference  = configuration;
        this.serializer = serializer;
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

            logger.debug("Removing entity : " + path);
            reference.set(new Configuration());

            return;
        }

        final String data        = new String(eData.getData());
        final Configuration item = serializer.fromJson(data);

        if (item == null) {
            logger.warn(String.format("Ignoring entity for path '%s', It cannot be NULL", path));

            return;
        }

        logger.debug("Storing entity : " + item);
        reference.set(item);
    }
}