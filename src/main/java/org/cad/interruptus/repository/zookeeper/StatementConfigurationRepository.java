package org.cad.interruptus.repository.zookeeper;

import org.cad.interruptus.entity.Statement;
import org.cad.interruptus.repository.StatementRepository;

public class StatementConfigurationRepository extends AbstractConfigurationRepository<Statement> implements StatementRepository
{
    public StatementConfigurationRepository(final ConfigurationManager manager)
    {
        super(manager);
    }
}