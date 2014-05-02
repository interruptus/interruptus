package org.cad.interruptus.repository;

import java.io.Serializable;
import java.util.List;

public interface EntityRepository<ID extends Serializable, E>
{
    public List<E> findAll() throws Exception;

    public E findById(ID entity) throws Exception;

    public void save(E entity) throws Exception;

    public Boolean remove(ID entity) throws Exception;
}