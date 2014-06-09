package org.cad.interruptus.core.zookeeper;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.cad.interruptus.core.esper.FlowConfiguration;
import java.util.concurrent.atomic.AtomicBoolean;
import org.cad.interruptus.core.esper.EsperConfiguration;
import org.cad.interruptus.core.esper.StatementConfiguration;
import org.cad.interruptus.entity.RunnableEntity;

public class ZookeeperLeaderListener implements LeaderLatchListener
{
    final Log logger = LogFactory.getLog(ZookeeperLeaderListener.class);
    final StatementConfiguration statementConfiguration;
    final FlowConfiguration flowConfiguration;
    final AtomicBoolean isLeader;

    public ZookeeperLeaderListener(final AtomicBoolean isLeader, final FlowConfiguration flowConfiguration, StatementConfiguration statementConfiguration)
    {
        this.isLeader               = isLeader;
        this.flowConfiguration      = flowConfiguration;
        this.statementConfiguration = statementConfiguration;
    }

    private void startMasterOnly(EsperConfiguration configuration , final List<RunnableEntity> list)
    {
        for (final RunnableEntity item : list) {
            if (item.isMasterOnly() && ! isLeader.get()) {
                continue;
            }

            logger.info(String.format("Starting %s : %s" , item.getClass().getSimpleName(), item.getId()));
            configuration.start(item.getId());
        }
    }

    private void stopMasterOnly(EsperConfiguration configuration , final List<RunnableEntity> list)
    {
        for (final RunnableEntity item : list) {
            if ( ! item.isMasterOnly() || isLeader.get()) {
                continue;
            }

            logger.info(String.format("Stoping %s : %s" , item.getClass().getSimpleName(), item.getId()));
            configuration.stop(item.getId());
        }
    }

    @Override
    public void isLeader()
    {
        logger.info("Take Leadership");
        isLeader.set(true);

        startMasterOnly(flowConfiguration, new ArrayList<RunnableEntity>(flowConfiguration.list()));
        startMasterOnly(statementConfiguration, new ArrayList<RunnableEntity>(statementConfiguration.list()));
    }

    @Override
    public void notLeader()
    {
        logger.info("Not leader");
        isLeader.set(false);

        stopMasterOnly(flowConfiguration, new ArrayList<RunnableEntity>(flowConfiguration.list()));
        stopMasterOnly(statementConfiguration, new ArrayList<RunnableEntity>(statementConfiguration.list()));
    }
}
