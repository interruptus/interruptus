package org.cad.interruptus.repository.zookeeper;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import org.cad.interruptus.core.EntityNotFoundException;
import org.cad.interruptus.entity.Entity;
import org.cad.interruptus.repository.EntityRepository;

abstract class AbstractConfigurationRepository<E extends Entity> implements EntityRepository<String, E>
{
    final ConfigurationManager manager;
    final Class<E> targetClass;

    public AbstractConfigurationRepository(final ConfigurationManager manager)
    {
        this.manager     = manager;
        this.targetClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public List<E> findAll() throws Exception
    {
        return manager.list(targetClass);
    }

    @Override
    public E findById(final String name) throws Exception
    {
        final Map<String, E> map = manager.map(targetClass);

        if ( ! map.containsKey(name)) {
            throw new EntityNotFoundException(targetClass, name);
        }

        return map.get(name);
    }

    @Override
    public void save(final E entity) throws Exception
    {
        manager.save(entity);
        manager.flush();
    }

    @Override
    public void remove(final String name) throws Exception
    {
        manager.remove(targetClass, name);
        manager.flush();
    }
}