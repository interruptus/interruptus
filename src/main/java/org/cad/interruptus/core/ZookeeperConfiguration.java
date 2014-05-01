package org.cad.interruptus.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import com.google.gson.Gson;
import java.io.Serializable;
import org.cad.interruptus.entity.Flow;
import org.cad.interruptus.entity.Statement;
import org.cad.interruptus.entity.Type;

public class ZookeeperConfiguration
{
    final private static Log log = LogFactory.getLog(ZookeeperConfiguration.class);
    private final AtomicBoolean isLeader = new AtomicBoolean(false);

    private static final Map<String, String> paths = new HashMap<>();
    private final String configPath;
    private final Gson gson;

    private CuratorFramework client;


    static {
        paths.put(Flow.class.getName(), Flow.class.getSimpleName().toLowerCase());
        paths.put(Type.class.getName(), Type.class.getSimpleName().toLowerCase());
        paths.put(Statement.class.getName(), Statement.class.getSimpleName().toLowerCase());
    }

    public ZookeeperConfiguration(final String configPath, final Gson gson)
    {
        this.configPath = configPath;
        this.gson       = gson;
    }

    public void setCuratorFramework(CuratorFramework client)
    {
        this.client = client;
    }

    public synchronized Boolean isLeader()
    {
        return isLeader.get();
    }

    public synchronized void setLeader(Boolean isLeader)
    {
        this.isLeader.set(isLeader);
    }

    private String getNodePath(Class clazz)
    {
        if ( ! paths.containsKey(clazz.getName())) {
            throw new RuntimeException("Unable to find path for class : " + clazz.getName());
        }

        return configPath + "/" + paths.get(clazz.getName());
    }

    private synchronized Boolean mutexCall(String path, Callable<Boolean> callable) throws Exception
    {
        InterProcessMutex mutex = new InterProcessMutex(client, path);

        try {
            mutex.acquire();

            return callable.call();
        } finally {
            mutex.release();
        }
    }

    private synchronized Boolean saveNode(Object object, String name) throws IOException, Exception
    {
        final byte[] bytes    = gson.toJson(object).getBytes();
        final String fullPath = getNodePath(object.getClass()) + "/" + name;

        return mutexCall(fullPath, new Callable() {
            @Override
            public Object call() throws Exception
            {
                Stat stat = client.checkExists().forPath(fullPath);

                if (stat != null) {
                    client.setData()
                        .forPath(fullPath, bytes);
                } else {
                  client.create()
                      .creatingParentsIfNeeded()
                      .forPath(fullPath, bytes);
                }

                return true;
            }
        });
    }

    private synchronized Boolean removeNode(Class clazz, String name) throws IOException, Exception
    {
        final String fullPath = getNodePath(clazz) + "/" + name;

        return mutexCall(fullPath, new Callable() {
            @Override
            public Object call() throws Exception
            {
                if (client.checkExists().forPath(fullPath) != null) {
                    client.delete().forPath(fullPath);

                    return false;
                }

                return true;
            }
        });
    }

    public Boolean save(Flow flow) throws IOException, Exception
    {
        return saveNode(flow, flow.getName());
    }

    public Boolean remove(Flow flow) throws IOException, Exception
    {
        return removeNode(Flow.class, flow.getName());
    }

    public Boolean save(Type type) throws IOException, Exception
    {
        return saveNode(type, type.getName());
    }

    public Boolean remove(Type type) throws IOException, Exception
    {
        return removeNode(Type.class, type.getName());
    }

    public Boolean save(Statement statement) throws IOException, Exception
    {
        return saveNode(statement, statement.getName());
    }

    public Boolean remove(Statement statement) throws IOException, Exception
    {
        return removeNode(Statement.class, statement.getName());
    }

    public <T> List<T> list(Class<? extends T> clazz) throws Exception
    {
        final List<T> result  = new ArrayList<>();
        final String rootPath = getNodePath(clazz);
    
        if (client.checkExists().forPath(rootPath) == null) {
            return result;
        }

        for (String name : client.getChildren().forPath(rootPath)) {

            final String path = rootPath + "/" + name;
            final byte[] data = client.getData().forPath(path);
            final T item      = gson.fromJson(new String(data), clazz);

            result.add(item);
        }

        return result;
    }

    public <T> T get(Class<? extends T> clazz, Serializable id) throws Exception
    {
        final String rootPath = getNodePath(clazz);
        final String path     = rootPath + "/" + id;

        if (client.checkExists().forPath(path) == null) {
            return null;
        }

        final byte[] data = client.getData().forPath(path);
        final T item      = gson.fromJson(new String(data), clazz);

        return item;
    }

    /**
     * @TODO - Start flows
     *
     * @throws IOException
     * @throws Exception
     */
    public synchronized void startLeadership() throws Exception
    {
    }

    /**
     * @TODO - Stop flows
     *
     * @throws IOException
     * @throws Exception
     */
    public synchronized void stopLeadership() throws Exception
    {
    }
}
