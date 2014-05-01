package org.cad.interruptus.core.zookeeper;

import com.google.gson.Gson;
import org.cad.interruptus.entity.Type;
import org.cad.interruptus.core.esper.TypeConfiguration;

public class TypeConfigurationListener extends AbstractConfigurationListener<Type>
{
    public TypeConfigurationListener(final String path, final TypeConfiguration typeConfiguration, final Gson gson)
    {
        super(path, typeConfiguration, gson);
    }
}
