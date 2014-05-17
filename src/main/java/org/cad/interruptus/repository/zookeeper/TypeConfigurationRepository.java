package org.cad.interruptus.repository.zookeeper;

import org.cad.interruptus.entity.Type;
import org.cad.interruptus.repository.TypeRepository;

public class TypeConfigurationRepository extends AbstractConfigurationRepository<Type> implements TypeRepository
{
    public TypeConfigurationRepository(final ConfigurationManager manager)
    {
        super(manager);
    }
}