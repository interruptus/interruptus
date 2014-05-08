package org.cad.interruptus.core.zookeeper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.cad.interruptus.core.esper.FlowConfiguration;
import org.cad.interruptus.entity.Flow;

public class ZookeeperLeaderListener implements LeaderLatchListener
{
    final Log logger = LogFactory.getLog(ZookeeperLeaderListener.class);
    
    final FlowConfiguration flowConfiguration;

    public ZookeeperLeaderListener(final FlowConfiguration flowConfiguration)
    {
        this.flowConfiguration = flowConfiguration;
    }
    
    @Override
    public void isLeader()
    {
        logger.info("Take Leadership");

        for (final Flow flow : flowConfiguration.list()) {
            if (flow.isMasterOnly()) {
                flowConfiguration.start(flow);
            }
        }
    }

    @Override
    public void notLeader()
    {
        logger.info("Not leader");

        for (final Flow flow : flowConfiguration.list()) {
            if (flow.isMasterOnly()) {
                flowConfiguration.stop(flow.getName());
            }
        }
    }
}
