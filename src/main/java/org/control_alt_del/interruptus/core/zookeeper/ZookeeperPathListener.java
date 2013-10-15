package org.control_alt_del.interruptus.core.zookeeper;

import java.io.IOException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

public interface ZookeeperPathListener
{
    String getPath();

    public void executor(CuratorFramework client, PathChildrenCacheEvent pcce) throws IOException;
}
