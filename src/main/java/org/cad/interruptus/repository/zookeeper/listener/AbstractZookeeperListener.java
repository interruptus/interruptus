package org.cad.interruptus.repository.zookeeper.listener;

import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cad.interruptus.entity.Entity;
import org.cad.interruptus.core.esper.EsperConfiguration;
import org.cad.interruptus.entity.RunnableEntity;

abstract public class AbstractZookeeperListener<ID extends Serializable, E extends Entity> implements EntityConfigurationListener<E>
{
    final EsperConfiguration<String, E> configuration;
    final Log logger = LogFactory.getLog(getClass());

    public AbstractZookeeperListener(final EsperConfiguration<String, E> configuration)
    {
        this.configuration = configuration;
    }

    protected void startIfRunning(final RunnableEntity e)
    {
        if (e.isRunning()) {
            return;
        }

        logger.debug("Starting entity : " + e);
        configuration.start(e.getId());
    }

    @Override
    public void onInsert(final E e)
    {
        configuration.save(e);

        if (e instanceof RunnableEntity) {
            startIfRunning((RunnableEntity) e);
        }
    }

    @Override
    public void onDelete(final E e)
    {
        configuration.remove(e.getId());
    }

    @Override
    public void onUpdate(final E e)
    {
        configuration.save(e);

        if (e instanceof RunnableEntity) {
            startIfRunning((RunnableEntity) e);
        }
    }
}