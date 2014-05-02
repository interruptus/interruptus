package org.cad.interruptus.core.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.EnsurePath;

public class AttachPathListener implements ZookeeperLifecycleListener
{
    private static final Log logger = LogFactory.getLog(AttachPathListener.class);
    private Map<String, Set<PathChildrenCacheListener>> listeners = new HashMap<>();
    private final List<PathChildrenCache> childrens = new ArrayList<>();

    public AttachPathListener()
    {

    }

    public AttachPathListener(Map<String, Set<PathChildrenCacheListener>> listeners)
    {
        this.listeners = listeners;
    }

    public void addListener(String path, PathChildrenCacheListener listener)
    {
        if ( ! listeners.containsKey(path)) {
            listeners.put(path, new HashSet<PathChildrenCacheListener>());
        }

        listeners.get(path).add(listener);
    }

    public void setListeners(Map<String, Set<PathChildrenCacheListener>> listeners)
    {
        this.listeners = listeners;
    }

    public List<PathChildrenCache> getChildrens()
    {
        return childrens;
    }

    @Override
    public void onStart(CuratorFramework curator)
    {
        try {
            for (Map.Entry<String, Set<PathChildrenCacheListener>> entry : listeners.entrySet()) {
                for (PathChildrenCacheListener pathChildrenCacheListener : entry.getValue()) {
                    registerListener(curator, entry.getKey(), pathChildrenCacheListener);
                }
            }
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

    private void registerListener(CuratorFramework curator, String path, PathChildrenCacheListener listener) throws Exception
    {
        EnsurePath ensure       = new EnsurePath(path);
        PathChildrenCache cache = new PathChildrenCache(curator, path, true);

        ensure.ensure(curator.getZookeeperClient());
        cache.getListenable().addListener(listener);

        cache.start();
        childrens.add(cache);

        logger.info(String.format("Add listener for : %s", path));
    }
}