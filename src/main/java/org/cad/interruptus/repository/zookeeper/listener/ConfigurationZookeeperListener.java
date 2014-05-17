package org.cad.interruptus.repository.zookeeper.listener;

import com.google.gson.Gson;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.cad.interruptus.core.GsonSerializer;
import org.cad.interruptus.entity.Configuration;
import org.cad.interruptus.entity.Entity;
import org.cad.interruptus.entity.Flow;
import org.cad.interruptus.repository.ConfigDiffTool;

public class ConfigurationZookeeperListener implements ZookeeperConfigurationListener
{
    final Log logger = LogFactory.getLog(getClass());
    final AtomicReference<Configuration> reference;
    final GsonSerializer<Configuration> serializer;
    final Map<String, List<EntityConfigurationListener>> listeners;

    public ConfigurationZookeeperListener(final AtomicReference<Configuration> configuration, final Map<String, List<EntityConfigurationListener>> listeners, final Gson gson)
    {
        this(configuration, listeners, new GsonSerializer(Configuration.class, gson));
    }
    
    public ConfigurationZookeeperListener(final AtomicReference<Configuration> configuration, final Map<String, List<EntityConfigurationListener>> listeners, final GsonSerializer<Configuration> serializer)
    {
        this.reference  = configuration;
        this.serializer = serializer;
        this.listeners  = listeners;
    }

    public void dispatchInsert(final String type, final Collection<Entity> collection)
    {
        if ( ! listeners.containsKey(type)) {
            return;
        }

        for (EntityConfigurationListener listener : listeners.get(type)) {
            for (Entity entity : collection) {
                listener.onInsert(entity);
                logger.debug("onInsert : " + entity);
            }
        }
    }
    
    public void dispatchUpdate(final String type, final Collection<Entity> collection)
    {
        if ( ! listeners.containsKey(type)) {
            return;
        }

        for (EntityConfigurationListener listener : listeners.get(type)) {
            for (Entity entity : collection) {
                listener.onUpdate(entity);
                logger.debug("onUpdate : " + entity);
            }
        }
    }
    
    public void dispatchDelete(final String type, final Collection<Entity> collection)
    {
        if ( ! listeners.containsKey(type)) {
            return;
        }

        for (EntityConfigurationListener listener : listeners.get(type)) {
            for (Entity entity : collection) {
                listener.onDelete(entity);
                logger.debug("onDelete : " + entity);
            }
        }
    }

    @Override
    public void onChange(final CuratorFramework curator, final NodeCache cache, final String path)
    {
        logger.debug("Config change ..");
        
        if (cache.getCurrentData() == null) {
            logger.warn("Empty config data ..");
            return;
        }

        final ChildData eData         = cache.getCurrentData();
        final String data             = new String(eData.getData());
        final Configuration newConfig = serializer.fromJson(data);
        final Configuration oldConfig = reference.get();

        if (newConfig == null) {
            logger.warn(String.format("Ignoring entity for path '%s', It cannot be NULL", path));

            return;
        }
        
        if (oldConfig != null) {
            ConfigDiffTool diffTool = new ConfigDiffTool(oldConfig, newConfig);
            
            // dispatchInsert("flow", diffTool.getFlowsScheduledToInsert().values());

            for (Flow flow : diffTool.getFlowsScheduledToInsert().values()) {
                logger.debug("insert flow : " + flow);
            }
            
            for (Flow flow : diffTool.getFlowsScheduledToUpdate().values()) {
                logger.debug("update flow : " + flow);
            }
            
            for (Flow flow : diffTool.getFlowsScheduledToDelete().values()) {
                logger.debug("delete flow : " + flow);
            }
        }

        logger.debug("Storing entity : " + newConfig);
        reference.set(newConfig);
    }
}