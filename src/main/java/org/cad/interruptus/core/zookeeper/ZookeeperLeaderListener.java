package org.cad.interruptus.core.zookeeper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;

public class ZookeeperLeaderListener implements LeaderLatchListener
{
    final Log logger = LogFactory.getLog(ZookeeperLeaderListener.class);

    @Override
    public void isLeader()
    {
        logger.info("Take Leadership");
    }

    @Override
    public void notLeader()
    {
        logger.info("not leader");
    }
}
