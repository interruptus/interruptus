package org.control_alt_del.interruptus.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.utils.EnsurePath;
import org.apache.zookeeper.data.Stat;
import org.control_alt_del.interruptus.core.zookeeper.ZookeeperFactoryBean;
import org.control_alt_del.interruptus.core.zookeeper.ZookeeperConfigurationListener;


public class ZookeeperKernel implements Kernel
{
    private static Log logger = LogFactory.getLog(ZookeeperFactoryBean.class);

    private List<PathChildrenCache> pathConfigList = new ArrayList<PathChildrenCache>();
    private LeaderSelectorListener leaderSelectorListener;
    private ZookeeperConfiguration zookeeperConfiguration;
    private List<ZookeeperConfigurationListener> listeners;
    private String leaderSelectorPath = "/ELECTION";
    private Boolean isInitialized = false;
    private CuratorFramework client;

    private static final EnumMap<PathChildrenCacheEvent.Type, ZookeeperConfigurationListener.EventType> allowedEvents = new EnumMap<PathChildrenCacheEvent.Type, ZookeeperConfigurationListener.EventType>(PathChildrenCacheEvent.Type.class);

    {
        allowedEvents.put(PathChildrenCacheEvent.Type.CHILD_ADDED, ZookeeperConfigurationListener.EventType.ADDED);
        allowedEvents.put(PathChildrenCacheEvent.Type.CHILD_UPDATED, ZookeeperConfigurationListener.EventType.UPDATED);
        allowedEvents.put(PathChildrenCacheEvent.Type.CHILD_REMOVED, ZookeeperConfigurationListener.EventType.REMOVED);
    }

    public void setLeaderSelectorListener(LeaderSelectorListener leaderSelectorListener)
    {
        this.leaderSelectorListener = leaderSelectorListener;
    }

    public void setListeners(List<ZookeeperConfigurationListener> listeners)
    {
        this.listeners = listeners;
    }

    public void setLeaderSelectorPath(String leaderSelectorPath)
    {
        this.leaderSelectorPath = leaderSelectorPath;
    }

    public void setCuratorFramework(CuratorFramework curatorFramework)
    {
        this.client = curatorFramework;
    }

    public void setZookeeperConfiguration(ZookeeperConfiguration zookeeperConfiguration)
    {
        this.zookeeperConfiguration = zookeeperConfiguration;
    }

    @Override
    public void start() throws Exception
    {
        logger.info("Start interruptus zookeeper");

        zookeeperConfiguration.setCuratorFramework(client);
        client.start();

        registerListeners();
        registerLeaderSelector();
    }

    @Override
    public void stop() throws IOException
    {
        client.close();

        for (PathChildrenCache child : pathConfigList) {
            child.close();
        }
    }

    private void registerLeaderSelector()
    {
        LeaderSelector selector = new LeaderSelector(client, leaderSelectorPath, leaderSelectorListener);
        selector.autoRequeue();
        selector.start();
    }

    private void dispatchInitEvent() throws Exception
    {
        logger.info("Init event listeners");

        for (ZookeeperConfigurationListener listener : listeners) {

            String rootPath = listener.getPath();
            Stat rootStat   = client.checkExists().forPath(rootPath);

            if (rootStat == null) {
                return;
            }

            for (String name : client.getChildren().forPath(rootPath)) {

                String path = rootPath + "/" + name;
                byte[] data = client.getData().forPath(path);
                Stat stat   = client.checkExists().forPath(path);

                listener.invoke(client, ZookeeperConfigurationListener.EventType.INITIALIZED, stat, path, data);
            }
        }
    }

    private void registerListeners() throws Exception
    {
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                logger.info(String.format("CuratorFramework state changed: %s", newState));

                if (isInitialized) {
                    return;
                }

                if (newState == ConnectionState.CONNECTED) {
                    try {
                        dispatchInitEvent();
                        isInitialized = true;
                    } catch (Exception ex) {
                        logger.fatal(ex);
                    }
                }
            }
        });

        client.getUnhandledErrorListenable().addListener(new UnhandledErrorListener() {
            @Override
            public void unhandledError(String message, Throwable e) {
                logger.error("CuratorFramework  : " + message, e);
            }
        });

        for (ZookeeperConfigurationListener listener : listeners) {
            registerListener(listener);
        }
    }

    private void registerListener(final ZookeeperConfigurationListener listener) throws Exception
    {

        EnsurePath ensurePath       = new EnsurePath(listener.getPath());
        PathChildrenCache pathCache = new PathChildrenCache(client, listener.getPath(), true);

        ensurePath.ensure(client.getZookeeperClient());

        pathCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent pcce) throws Exception
            {
                if ( ! allowedEvents.containsKey(pcce.getType())) {
                    logger.info(String.format("Ignore event %s for : %s", pcce.getType(), pcce.getData().getPath()));

                    return;
                }

                ChildData eventData = pcce.getData();
                byte[] data = eventData.getData();
                String path = eventData.getPath();
                Stat stat   = eventData.getStat();
                ZookeeperConfigurationListener.EventType eventType = allowedEvents.get(pcce.getType());

                listener.invoke(client, eventType, stat, path, data);
            }
        });

        pathCache.start();
        pathConfigList.add(pathCache);

        logger.info(String.format("Add listener for : %s", listener.getPath()));
    }

}