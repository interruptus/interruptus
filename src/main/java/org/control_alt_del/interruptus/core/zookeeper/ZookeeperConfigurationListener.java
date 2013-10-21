package org.control_alt_del.interruptus.core.zookeeper;

import java.io.IOException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;

public interface ZookeeperConfigurationListener
{
    String getPath();

    public void invoke(CuratorFramework client, EventType eventType, Stat stat, String path, byte[] data) throws IOException;

    enum EventType
    {
        ADDED,
        UPDATED,
        REMOVED,
        INITIALIZED,
    }
}
