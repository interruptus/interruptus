package org.cad.interruptus.core.zookeeper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;

public class ZookeeperLeaderListener implements LeaderSelectorListener
{
    final Log logger = LogFactory.getLog(ZookeeperLeaderListener.class);

    @Override
    public synchronized void takeLeadership(CuratorFramework cf) throws Exception
    {
        logger.info("Take Leadership");

        // I'm the leader \o/

        // this callback will get called when you are the leader
        // do whatever leader work you need to and only exit
        // this method when you want to relinquish leadership
        try{

            // hold while is the leader
            while (true) {
                // @TODO - lame !!!
                Thread.sleep(1000);
            }

        } catch(InterruptedException e) {
            logger.error(this, e);
        }
    }

    @Override
    public synchronized void stateChanged(CuratorFramework cf, ConnectionState cs)
    {
        logger.info("State Changed : " + cs);
    }
}
