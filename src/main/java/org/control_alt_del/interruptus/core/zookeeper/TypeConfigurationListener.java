package org.control_alt_del.interruptus.core.zookeeper;

import org.control_alt_del.interruptus.entity.Type;
import org.control_alt_del.interruptus.core.esper.TypeConfiguration;

public class TypeConfigurationListener extends AbstractConfigurationListener<Type>
{
    public TypeConfigurationListener(String path, TypeConfiguration typeConfiguration)
    {
        super(path, typeConfiguration);
    }
}
