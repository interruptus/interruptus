package org.cad.interruptus.core.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.cad.interruptus.repository.zookeeper.listener.ZookeeperConfigurationListener;

public class AttachConfigurationListener implements ZookeeperLifecycleListener
{
    private static final Log logger = LogFactory.getLog(AttachConfigurationListener.class);
    private final List<NodeCache> childrens = new ArrayList<>();
    private final Map<String, Set<ZookeeperConfigurationListener>> listeners;

    public AttachConfigurationListener(final Map<String, Set<ZookeeperConfigurationListener>> listeners)
    {
        this.listeners = listeners;
    }

    @Override
    public void onStart(final CuratorFramework curator) throws Exception
    {
        for (final Map.Entry<String, Set<ZookeeperConfigurationListener>> entry : listeners.entrySet()) {
            for (final ZookeeperConfigurationListener listener : entry.getValue()) {
                registerListener(curator, entry.getKey(), listener);
            }
        }
    }

    @Override
    public void onStop(final CuratorFramework curator) throws IOException
    {
        for (NodeCache childrenCache : childrens) {
            childrenCache.close();
        }
    }

    private void registerListener(final CuratorFramework curator, final String path, final ZookeeperConfigurationListener listener) throws Exception
    {
        final NodeCache cache   = new NodeCache(curator, path, true);
        
        cache.start();
        cache.getListenable().addListener(new NodeCacheListener()
        {
            @Override
            public void nodeChanged() throws Exception
            {
                listener.onChange(curator, cache, path);
            }
        });

        childrens.add(cache);
        logger.info(String.format("Add listener %s for %s", listener, path));
    }
}