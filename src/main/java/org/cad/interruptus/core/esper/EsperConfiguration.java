package org.cad.interruptus.core.esper;

import java.util.List;

public interface EsperConfiguration<T>
{
    public List<String> list();

    public void save(T e);

    public Boolean remove(String name);

    public Boolean exists(String name);
    
    public Boolean start(String name);

    public Boolean stop(String name);
}
