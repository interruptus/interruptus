package org.cad.interruptus.core.esper;

import java.util.List;

public interface EsperConfiguration<T>
{
    public List<T> list();

    public void save(T e);

    public Boolean remove(T e);

    public Boolean exists(T e);
}
