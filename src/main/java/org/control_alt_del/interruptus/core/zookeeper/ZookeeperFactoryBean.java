package org.control_alt_del.interruptus.core.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class ZookeeperFactoryBean implements FactoryBean<CuratorFramework>, InitializingBean, DisposableBean {

    private RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    private CuratorFramework curator;
    private String connectionString;

    public void setRetryPolicy(RetryPolicy retryPolicy)
    {
        this.retryPolicy = retryPolicy;
    }

    public void setConnectionString(String connectionString)
    {
        this.connectionString = connectionString;
    }

    @Override
    public CuratorFramework getObject()
    {
        if (curator == null) {
            curator = createWithOptions(connectionString, retryPolicy, 2000, 10000);
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
        if (curator != null && curator.getState() != CuratorFrameworkState.STOPPED) {
            curator.close();
        }
    }

    @Override
    public void afterPropertiesSet()
    {

    }

    public CuratorFramework  createWithOptions(String connectionString, RetryPolicy retryPolicy, int connectionTimeoutMs, int sessionTimeoutMs)
    {
        return CuratorFrameworkFactory.builder()
            .connectString(connectionString)
            .retryPolicy(retryPolicy)
            .connectionTimeoutMs(connectionTimeoutMs)
            .sessionTimeoutMs(sessionTimeoutMs)
            .build();
    }
}