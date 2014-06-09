package org.cad.interruptus.repository.zookeeper.listener;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cad.interruptus.entity.Configuration;
import org.cad.interruptus.entity.Entity;
import org.cad.interruptus.entity.Flow;
import org.cad.interruptus.entity.Statement;
import org.cad.interruptus.entity.Type;
import org.cad.interruptus.repository.ConfigDiffTool;

public class ConfigurationEventDispatcher
{
    final Log logger = LogFactory.getLog(getClass());
    final Map<String, List<EntityConfigurationListener>> listeners;

    public ConfigurationEventDispatcher(final Map<String, List<EntityConfigurationListener>> listeners)
    {
        this.listeners = listeners;
    }

    protected String classType(final Class clazz)
    {
        return clazz.getSimpleName().toLowerCase();
    }

    public void dispatchSave(final Entity entity)
    {
        final String type = classType(entity.getClass());

        if ( ! listeners.containsKey(type)) {
            return;
        }

        logger.debug(String.format("onSave : %s@%s", entity.getClass().getSimpleName(), entity.getId()));

        for (final EntityConfigurationListener listener : listeners.get(type)) {
            listener.onSave(entity);
        }
    }

    public void dispatchDelete(final Entity entity)
    {
        final String type = classType(entity.getClass());

        if ( ! listeners.containsKey(type)) {
            return;
        }

        logger.debug(String.format("onDelete : %s@%s", entity.getClass().getSimpleName(), entity.getId()));

        for (final EntityConfigurationListener listener : listeners.get(type)) {
            listener.onDelete(entity);
        }
    }

    public void dispatchSave(final Collection<Entity> collection)
    {
        for (final Entity entity : collection) {
            dispatchSave(entity);
        }
    }

    public void dispatchDelete(final Collection<Entity> collection)
    {
        for (final Entity entity : collection) {
            dispatchDelete(entity);
        }
    }

    public void dispatchEvents(final Configuration newConfig, final Configuration oldConfig)
    {
        final ConfigDiffTool diffTool = new ConfigDiffTool(oldConfig, newConfig);

        dispatchSave(diffTool.computeInsertMap(Type.class).values());
        dispatchSave(diffTool.computeUpdateMap(Type.class).values());
        dispatchDelete(diffTool.computeDeleteMap(Type.class).values());

        dispatchSave(diffTool.computeInsertMap(Flow.class).values());
        dispatchSave(diffTool.computeUpdateMap(Flow.class).values());
        dispatchDelete(diffTool.computeDeleteMap(Flow.class).values());

        dispatchSave(diffTool.computeInsertMap(Statement.class).values());
        dispatchSave(diffTool.computeUpdateMap(Statement.class).values());
        dispatchDelete(diffTool.computeDeleteMap(Statement.class).values());
    }
}