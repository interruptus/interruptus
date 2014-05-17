package org.cad.interruptus.core.zookeeper;

import java.io.IOException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;

public class AttachLeaderSelectorListener implements ZookeeperLifecycleListener
{
    final LeaderLatchListener listener;
    LeaderLatch leaderLatch;
    final String path;

    public AttachLeaderSelectorListener(final LeaderLatchListener listener, final String path)
    {
        this.listener = listener;
        this.path     = path;
    }

    @Override
    public void onStart(CuratorFramework curator) throws Exception
    {
        leaderLatch = new LeaderLatch(curator, path);
        
        leaderLatch.start();
        leaderLatch.addListener(listener);
    }

    @Override
    public void onStop(CuratorFramework curator) throws IOException
    {
        if (leaderLatch == null) {
            return;
        }

        leaderLatch.close();
    }
}