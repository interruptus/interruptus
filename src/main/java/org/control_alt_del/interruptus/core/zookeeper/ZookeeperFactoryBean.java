package org.control_alt_del.interruptus.core.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.utils.EnsurePath;
import org.control_alt_del.interruptus.core.ZookeeperConfiguration;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class ZookeeperFactoryBean implements FactoryBean<CuratorFramework>, InitializingBean, DisposableBean {

    private static Log logger = LogFactory.getLog(ZookeeperFactoryBean.class);

    private List<PathChildrenCache> pathConfigList = new ArrayList<PathChildrenCache>();
    private ZookeeperConfiguration zookeeperConfiguration;
    private List<ZookeeperPathListener> listeners;
    private CuratorFramework curator;
    private String connectionString;
    private String leaderSelectorPath;

    private static final Set<PathChildrenCacheEvent.Type> allowedEvents = new HashSet<PathChildrenCacheEvent.Type>(Arrays.asList(new PathChildrenCacheEvent.Type[]{
        PathChildrenCacheEvent.Type.CHILD_ADDED,
        PathChildrenCacheEvent.Type.CHILD_UPDATED,
        PathChildrenCacheEvent.Type.CHILD_REMOVED
    }));

    private LeaderSelectorListener leaderSelectorListener;

    public ZookeeperConfiguration getZookeeperConfiguration()
    {
        return zookeeperConfiguration;
    }

    public void setZookeeperConfiguration(ZookeeperConfiguration configuration)
    {
        this.zookeeperConfiguration = configuration;
    }


    public LeaderSelectorListener getLeaderSelectorListener()
    {
        return leaderSelectorListener;
    }

    public void setLeaderSelectorListener(LeaderSelectorListener leaderSelectorListener)
    {
        this.leaderSelectorListener = leaderSelectorListener;
    }
    
    public void setListeners(List<ZookeeperPathListener> listeners) {
        this.listeners = listeners;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getLeaderSelectorPath()
    {
        return leaderSelectorPath;
    }

    public void setLeaderSelectorPath(String leaderSelectorPath)
    {
        this.leaderSelectorPath = leaderSelectorPath;
    }

    @Override
    public CuratorFramework getObject() {
        return curator;
    }

    @Override
    public Class<?> getObjectType() {
        return CuratorFramework.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        curator.close();

        for (PathChildrenCache child : pathConfigList) {
            child.close();
        }
    }

    @Override
    public void afterPropertiesSet() throws IOException, Exception{
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curator = createWithOptions(connectionString, retryPolicy, 2000, 10000);

        curator.start();

        registerListeners(curator);
        registerLeaderSelector(curator);
        zookeeperConfiguration.setCuratorFramework(curator);
    }

    public CuratorFramework  createWithOptions(String connectionString, RetryPolicy retryPolicy, int connectionTimeoutMs, int sessionTimeoutMs) throws IOException
    {
        return CuratorFrameworkFactory.builder()
            .connectString(connectionString)
            .retryPolicy(retryPolicy)
            .connectionTimeoutMs(connectionTimeoutMs)
            .sessionTimeoutMs(sessionTimeoutMs)
            .build();
    }

    private void registerLeaderSelector(CuratorFramework client)
    {
        LeaderSelector selector = new LeaderSelector(client, "/ELECTION", leaderSelectorListener);
        selector.autoRequeue();
        selector.start();
    }

    private void registerListeners(CuratorFramework client) throws Exception
    {
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                logger.info(String.format("CuratorFramework state changed: %s", newState));
            }
        });

        client.getUnhandledErrorListenable().addListener(new UnhandledErrorListener() {
            @Override
            public void unhandledError(String message, Throwable e) {
                logger.error("CuratorFramework  : " + message, e);
            }
        });

        for (ZookeeperPathListener listener : listeners) {
            registerListener(client, listener);
        }
    }

    private void registerListener(final CuratorFramework client, final ZookeeperPathListener listener) throws Exception
    {

        EnsurePath ensurePath       = new EnsurePath(listener.getPath());
        PathChildrenCache pathCache = new PathChildrenCache(client, listener.getPath(), true);

        ensurePath.ensure(client.getZookeeperClient());

        pathCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework cf, PathChildrenCacheEvent pcce) throws Exception
            {
                if ( ! allowedEvents.contains(pcce.getType())) {
                    return;
                }

                listener.executor(client, pcce);
            }
        });

        pathCache.start();
        pathConfigList.add(pathCache);

        logger.info(String.format("Add listener for : %s", listener.getPath()));
    }
}