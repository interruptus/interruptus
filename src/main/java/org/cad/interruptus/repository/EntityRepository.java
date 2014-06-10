package org.cad.interruptus.repository;

import java.io.Serializable;
import java.util.List;
import org.cad.interruptus.entity.Entity;

public interface EntityRepository<ID extends Serializable, E extends Entity>
{
    public List<E> findAll() throws Exception;

    public E findById(ID entity) throws Exception;

    public void save(E entity) throws Exception;

    public void remove(ID entity) throws Exception;
}