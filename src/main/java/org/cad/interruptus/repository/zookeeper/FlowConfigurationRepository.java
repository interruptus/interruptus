package org.cad.interruptus.repository.zookeeper;

import org.cad.interruptus.entity.Flow;
import org.cad.interruptus.repository.FlowRepository;

public class FlowConfigurationRepository extends AbstractConfigurationRepository<Flow> implements FlowRepository
{
    public FlowConfigurationRepository(final ConfigurationManager manager)
    {
        super(manager);
    }
}