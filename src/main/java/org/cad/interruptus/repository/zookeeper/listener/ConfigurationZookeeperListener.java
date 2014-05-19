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
import org.cad.interruptus.entity.Statement;
import org.cad.interruptus.entity.Type;
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
        logger.debug("dispatchInsert : " + type + " - " + collection);

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
        logger.debug("dispatchUpdate : " + type + " - " + collection);

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
        logger.debug("dispatchDelete : " + type + " - " + collection);

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

    public void dispatchEvents(final Configuration newConfig, final Configuration oldConfig)
    {
        final ConfigDiffTool diffTool   = new ConfigDiffTool(oldConfig, newConfig);
        final String typeKey            = Type.class.getSimpleName().toLowerCase();
        final String flowKey            = Flow.class.getSimpleName().toLowerCase();
        final String statementKey       = Statement.class.getSimpleName().toLowerCase();

        dispatchInsert(typeKey, diffTool.computeInsertMap(Type.class).values());
        dispatchUpdate(typeKey, diffTool.computeUpdateMap(Type.class).values());
        dispatchDelete(typeKey, diffTool.computeDeleteMap(Type.class).values());

        dispatchInsert(flowKey, diffTool.computeInsertMap(Flow.class).values());
        dispatchUpdate(flowKey, diffTool.computeUpdateMap(Flow.class).values());
        dispatchDelete(flowKey, diffTool.computeDeleteMap(Flow.class).values());

        dispatchInsert(statementKey, diffTool.computeInsertMap(Statement.class).values());
        dispatchUpdate(statementKey, diffTool.computeUpdateMap(Statement.class).values());
        dispatchDelete(statementKey, diffTool.computeDeleteMap(Statement.class).values());
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
        final Configuration oldConfig = reference.get() != null ? reference.get() : new Configuration();

        if (newConfig == null) {
            logger.warn(String.format("Ignoring entity for path '%s', It cannot be NULL", path));

            return;
        }
        
        logger.info("newConfig : " + newConfig);
        logger.info("oldConfig : " + oldConfig);
        
        if (newConfig.equals(oldConfig)) {
            logger.warn(String.format("Configuration remains the same."));

            return;
        }

        logger.debug("Storing entity : " + newConfig);

        reference.set(newConfig);
        dispatchEvents(newConfig, oldConfig);
    }
}