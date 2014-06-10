package org.cad.interruptus.core.zookeeper;

import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.cad.interruptus.core.esper.FlowConfiguration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.cad.interruptus.core.esper.EsperConfiguration;
import org.cad.interruptus.core.esper.StatementConfiguration;
import org.cad.interruptus.entity.Configuration;
import org.cad.interruptus.entity.Entity;
import org.cad.interruptus.entity.Flow;
import org.cad.interruptus.entity.RunnableEntity;
import org.cad.interruptus.entity.Statement;

public class ZookeeperLeaderListener implements LeaderLatchListener
{
    final Log logger = LogFactory.getLog(ZookeeperLeaderListener.class);
    final StatementConfiguration statementConfiguration;
    final FlowConfiguration flowConfiguration;
    final AtomicReference<Configuration> reference;
    final AtomicBoolean isLeader;

    public ZookeeperLeaderListener(final AtomicBoolean isLeader, final FlowConfiguration flowConfiguration, final StatementConfiguration statementConfiguration, final AtomicReference<Configuration> reference)
    {
        this.isLeader               = isLeader;
        this.reference              = reference;
        this.flowConfiguration      = flowConfiguration;
        this.statementConfiguration = statementConfiguration;
    }
    
    private RunnableEntity getRunnableEntity(final String name, final Class clazz)
    {
        try {
            final Map<String, Entity> map   = reference.get().mapOf(clazz);
            final Entity entity             = map.get(name);

            if (entity instanceof RunnableEntity) {
                return (RunnableEntity) entity;
            }

            return null;
        } catch (final Exception ex) {
            logger.error(this, ex);
            return null;
        }
    }

    private void startMasterOnly(final EsperConfiguration configuration, final Class clazz)
    {
        List<String> list = configuration.list();

        for (final String name : list) {

            final RunnableEntity entity = getRunnableEntity(name, clazz);

            if (entity == null) {
                continue;
            }

            if (entity.isMasterOnly() && ! isLeader.get()) {
                continue;
            }

            logger.info(String.format("Starting %s : %s", clazz.getSimpleName(), name));
            configuration.start(name);
        }
    }

    private void stopMasterOnly(final EsperConfiguration configuration, final Class clazz)
    {
        List<String> list = configuration.list();

        for (final String name : list) {

            final RunnableEntity entity = getRunnableEntity(name, clazz);

            if (entity == null || (entity.isMasterOnly() || ! isLeader.get())) {
                logger.info(String.format("Stoping %s : %s", clazz.getSimpleName(), name));
                configuration.stop(name);
            }
        }
    }

    @Override
    public void isLeader()
    {
        logger.info("Take Leadership");
        isLeader.set(true);

        startMasterOnly(flowConfiguration, Flow.class);
        startMasterOnly(statementConfiguration, Statement.class);
    }

    @Override
    public void notLeader()
    {
        logger.info("Not leader");
        isLeader.set(false);

        stopMasterOnly(flowConfiguration, Flow.class);
        stopMasterOnly(statementConfiguration, Statement.class);
    }
}
