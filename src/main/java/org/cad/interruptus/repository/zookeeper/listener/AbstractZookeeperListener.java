package org.cad.interruptus.repository.zookeeper.listener;

import java.io.Serializable;
import org.cad.interruptus.entity.Entity;
import org.cad.interruptus.core.esper.EsperConfiguration;

abstract public class AbstractZookeeperListener<ID extends Serializable, E extends Entity> implements EntityConfigurationListener<E>
{
    final EsperConfiguration<String, E> configuration;
    
    public AbstractZookeeperListener(final EsperConfiguration<String, E> configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public void onInsert(final E e)
    {
        configuration.save(e);
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
    }
}