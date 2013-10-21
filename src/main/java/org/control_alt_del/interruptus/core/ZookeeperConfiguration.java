package org.control_alt_del.interruptus.core;

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
import org.codehaus.jackson.map.ObjectMapper;
import org.control_alt_del.interruptus.core.esper.FlowConfiguration;
import org.control_alt_del.interruptus.core.esper.TypeConfiguration;
import org.control_alt_del.interruptus.entity.Flow;
import org.control_alt_del.interruptus.entity.Statement;
import org.control_alt_del.interruptus.entity.Type;

public class ZookeeperConfiguration
{
    final private static Log log = LogFactory.getLog(ZookeeperConfiguration.class);
    private AtomicBoolean isLeader = new AtomicBoolean(false);
    private ObjectMapper mapper = new ObjectMapper();
    private TypeConfiguration typeConfiguration;
    private FlowConfiguration flowConfiguration;
    private CuratorFramework client;
    private String configPath;

    private static Map<String, String> paths = new HashMap<String, String>();
    {
        paths.put(Flow.class.getName(), Flow.class.getSimpleName().toLowerCase());
        paths.put(Type.class.getName(), Type.class.getSimpleName().toLowerCase());
        paths.put(Statement.class.getName(), Statement.class.getSimpleName().toLowerCase());
    }

    public ZookeeperConfiguration(String configPath)
    {
        this.configPath = configPath;
    }

    public void setCuratorFramework(CuratorFramework client)
    {
        this.client = client;
    }

    public void setTypeConfiguration(TypeConfiguration typeConfig)
    {
        this.typeConfiguration = typeConfig;
    }

    public void setFlowConfiguration(FlowConfiguration flowConfig)
    {
        this.flowConfiguration = flowConfig;
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
        final byte[] bytes    = mapper.writeValueAsBytes(object);
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
        List<T> result  = new ArrayList<T>();
        String rootPath = getNodePath(clazz);
    
        if (client.checkExists().forPath(rootPath) == null) {
            return result;
        }

        for (String name : client.getChildren().forPath(rootPath)) {

            String path = rootPath + "/" + name;
            byte[] data = client.getData().forPath(path);
            T item      = mapper.readValue(new String(data), clazz);

            result.add(item);
        }

        return result;
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
