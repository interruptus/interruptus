package org.cad.interruptus.core.esper;

import java.io.Serializable;
import java.util.List;

public interface EsperConfiguration<ID extends Serializable, T>
{
    public List<T> list();

    public void save(T e);

    public Boolean remove(ID id);

    public Boolean remove(T e);

    public Boolean exists(ID id);
    
    public Boolean start(ID id);

    public Boolean stop(ID id);
}
