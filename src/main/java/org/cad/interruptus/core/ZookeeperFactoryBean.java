package org.cad.interruptus.core;

import java.util.Collections;
import java.util.List;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.cad.interruptus.core.zookeeper.ZookeeperLifecycleListener;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class ZookeeperFactoryBean implements FactoryBean<CuratorFramework>, InitializingBean, DisposableBean {

    private RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    private CuratorFramework curator;
    private String connection;

    private int connectionTimeout = 2000;
    private int sessionTimeout = 10000;

    private List<ZookeeperLifecycleListener> lifecycleListeners = Collections.EMPTY_LIST;

    public void setRetryPolicy(RetryPolicy retryPolicy)
    {
        this.retryPolicy = retryPolicy;
    }

    public void setConnection(String connectionString)
    {
        this.connection = connectionString;
    }

    public void setConnectionTimeout(int connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout)
    {
        this.sessionTimeout = sessionTimeout;
    }

    public void setLifecycleListeners(List<ZookeeperLifecycleListener> lifecycleListeners)
    {
        this.lifecycleListeners = lifecycleListeners;
    }

    @Override
    public CuratorFramework getObject()
    {
        if (curator == null) {
            curator = createClient();

            curator.start();

            for (ZookeeperLifecycleListener listener : lifecycleListeners) {
                listener.onStart(curator);
            }
        }

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
    public void destroy() {

        if (curator == null) {
            return;
        }

        for (ZookeeperLifecycleListener listener : lifecycleListeners) {
            listener.onStop(curator);
        }

        curator.close();
    }

    @Override
    public void afterPropertiesSet()
    {

    }

    protected CuratorFramework createClient()
    {
        return CuratorFrameworkFactory.builder()
            .connectionTimeoutMs(connectionTimeout)
            .sessionTimeoutMs(sessionTimeout)
            .connectString(connection)
            .retryPolicy(retryPolicy)
            .build();
    }
}