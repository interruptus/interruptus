package org.cad.interruptus.core.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.utils.EnsurePath;

public class AttachLeaderSelectorListener implements ZookeeperLifecycleListener
{
    private static final Log logger = LogFactory.getLog(AttachLeaderSelectorListener.class);
    private final List<PathChildrenCache> childrens = new ArrayList<>();
    private final LeaderSelectorListener listener;
    private final String path;

    public AttachLeaderSelectorListener(LeaderSelectorListener leaderSelectorListener, String leaderSelectorPath)
    {
        this.listener = leaderSelectorListener;
        this.path     = leaderSelectorPath;
    }

    @Override
    public void onStart(CuratorFramework curator)
    {
        try {
            registerListener(curator);
        } catch (Exception ex) {
            logger.error(this, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onStop(CuratorFramework curator)
    {
        try {
            for (PathChildrenCache childrenCache : childrens) {
                childrenCache.close();
            }
        } catch (IOException ex) {
            logger.error(this, ex);
            throw new RuntimeException(ex);
        }
    }

    private void registerListener(final CuratorFramework curator) throws Exception
    {
        EnsurePath ensure       = new EnsurePath(path);
        LeaderSelector selector = new LeaderSelector(curator, path, listener);

        ensure.ensure(curator.getZookeeperClient());
        selector.autoRequeue();
        selector.start();
    }
}