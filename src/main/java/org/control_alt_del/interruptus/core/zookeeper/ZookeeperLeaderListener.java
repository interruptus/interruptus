package org.control_alt_del.interruptus.core.zookeeper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.control_alt_del.interruptus.core.ZookeeperConfiguration;

public class ZookeeperLeaderListener implements LeaderSelectorListener
{
    private static Log logger = LogFactory.getLog(TypeConfigurationListener.class);
    
    private ZookeeperConfiguration config;

    public ZookeeperLeaderListener(ZookeeperConfiguration config)
    {
        this.config = config;
    }

    @Override
    public synchronized void takeLeadership(CuratorFramework cf) throws Exception
    {
        logger.info("Take Leadership");

        // I'm the leader \o/
        config.setLeader(Boolean.TRUE);
        
        try{
            config.startLeadership();

            // hold while is the leader
            while (config.isLeader()) {
                // @TODO - lame !!!
                Thread.sleep(1000);
            }

        }catch(Exception e){
            logger.error(this, e);
        }finally{
            config.stopLeadership();
        }

        // this callback will get called when you are the leader
        // do whatever leader work you need to and only exit
        // this method when you want to relinquish leadership
    }

    @Override
    public synchronized void stateChanged(CuratorFramework cf, ConnectionState cs)
    {
        logger.info("State Changed : " + cs);
    }
}
