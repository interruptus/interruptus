package org.cad.interruptus.repository.zookeeper.listener;

import com.google.gson.Gson;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.cad.interruptus.core.GsonSerializer;
import org.cad.interruptus.entity.Configuration;

public class ConfigurationZookeeperListener implements ZookeeperConfigurationListener
{
    final Log logger = LogFactory.getLog(getClass());
    final AtomicReference<Configuration> reference;
    final GsonSerializer<Configuration> serializer;
    final ConfigurationEventDispatcher dispatcher;

    public ConfigurationZookeeperListener(final AtomicReference<Configuration> configuration, final ConfigurationEventDispatcher dispatcher, final Gson gson)
    {
        this(configuration, dispatcher, new GsonSerializer(Configuration.class, gson));
    }

    public ConfigurationZookeeperListener(final AtomicReference<Configuration> configuration, final ConfigurationEventDispatcher dispatcher, final GsonSerializer<Configuration> serializer)
    {
        this.reference  = configuration;
        this.serializer = serializer;
        this.dispatcher = dispatcher;
    }

    @Override
    public void onChange(final CuratorFramework curator, final NodeCache cache, final String path)
    {
        logger.debug("Config change ..");

        if (cache.getCurrentData() == null) {
            logger.warn("Empty config data ..");
            reference.set(null);

            return;
        }

        final ChildData eData         = cache.getCurrentData();
        final String data             = new String(eData.getData());
        final Configuration newConfig = serializer.fromJson(data);
        final Configuration oldConfig = reference.get() != null ? reference.get() : new Configuration();

        if (newConfig == null) {
            logger.warn(String.format("Ignoring entity for path '%s', It cannot be NULL", path));

            return;
        }

        if (newConfig.equals(oldConfig)) {
            logger.debug(String.format("No changes detected..."));

            return;
        }

        logger.debug("Storing entity : " + newConfig);

        reference.set(newConfig);
        dispatcher.dispatchEvents(newConfig, oldConfig);
    }
}