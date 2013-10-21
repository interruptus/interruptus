package org.control_alt_del.interruptus.core.esper;

import java.util.List;

public interface EsperConfiguration<T>
{
    public List<T> list();

    public T create(T flow);

    public Boolean destroy(T flow);

    public Boolean exists(T flow);
}
