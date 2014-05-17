package org.cad.interruptus.repository.zookeeper.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;

public interface ZookeeperConfigurationListener
{
    public void onChange(final CuratorFramework curator, final NodeCache cache, final String path);
}
