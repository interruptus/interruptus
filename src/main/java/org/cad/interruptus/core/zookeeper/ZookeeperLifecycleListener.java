package org.cad.interruptus.core.zookeeper;

import org.apache.curator.framework.CuratorFramework;

public interface ZookeeperLifecycleListener
{
    public void onStart(CuratorFramework curator) throws Exception;
    public void onStop(CuratorFramework curator) throws Exception;
}