package org.cad.interruptus.core.zookeeper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.cad.interruptus.core.esper.FlowConfiguration;
import java.util.concurrent.atomic.AtomicBoolean;
import org.cad.interruptus.entity.Flow;

public class ZookeeperLeaderListener implements LeaderLatchListener
{
    final Log logger = LogFactory.getLog(ZookeeperLeaderListener.class);
    final FlowConfiguration flowConfiguration;
    final AtomicBoolean isLeader;

    public ZookeeperLeaderListener(final AtomicBoolean isLeader, final FlowConfiguration flowConfiguration)
    {
        this.isLeader          = isLeader;
        this.flowConfiguration = flowConfiguration;
    }
    
    @Override
    public void isLeader()
    {
        logger.info("Take Leadership");
        isLeader.set(true);

        for (final Flow flow : flowConfiguration.list()) {
            if (flow.isMasterOnly()) {
                flowConfiguration.start(flow.getName());
            }
        }
    }

    @Override
    public void notLeader()
    {
        logger.info("Not leader");
        isLeader.set(false);

        for (final Flow flow : flowConfiguration.list()) {
            if (flow.isMasterOnly()) {
                flowConfiguration.stop(flow.getName());
            }
        }
    }
}
