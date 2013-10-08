package org.control_alt_del.interruptus.core;

import java.io.IOException;
import java.util.List;
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

    private synchronized Boolean saveNode(Object object , String path) throws IOException, Exception
    {
        byte[] bytes    = mapper.writeValueAsBytes(object);
        String fullPath = configPath + "/" + path;
        String json     = new String(bytes);
        InterProcessMutex mutex = new InterProcessMutex(client, fullPath);
        Boolean ret = false;
        try {
          log.info(String.format("Save configuration %s : %s", fullPath, json));
          mutex.acquire();
          if (client.checkExists().forPath(fullPath) != null) {
              client.setData().forPath(fullPath, bytes);
          } else {
            client.create()
                .creatingParentsIfNeeded()
                .forPath(fullPath, bytes);
          }
          ret = true;
         } finally
         {
           mutex.release();
         }

          return ret;
    }

    private synchronized Boolean removeNode(String path) throws IOException, Exception
    {
        Boolean ret = false;
        String fullPath = configPath + "/" + path;
        InterProcessMutex mutex = new InterProcessMutex(client, fullPath);
        try {
          mutex.acquire();
          if (client.checkExists().forPath(fullPath) != null) {
              client.delete().forPath(fullPath);
              ret = true;
          }
        } finally
        {
          mutex.release();
        }
        return ret;
    }

    public Boolean save(Flow flow) throws IOException, Exception
    {
        return saveNode(flow, "flow/" + flow.getName());
    }

    public Boolean remove(Flow flow) throws IOException, Exception
    {
        return removeNode("flow/" + flow.getName());
    }

    public Boolean save(Type type) throws IOException, Exception
    {
        return saveNode(type, "type/" + type.getName());
    }

    public Boolean remove(Type type) throws IOException, Exception
    {
        return removeNode("type/" + type.getName());
    }

    public Boolean save(Statement statement) throws IOException, Exception
    {
        return saveNode(statement, "statement/" + statement.getName());
    }

    public Boolean remove(Statement statement) throws IOException, Exception
    {
        return removeNode("statement/" + statement.getName());
    }

    public synchronized void start() throws IOException, Exception
    {
        startType();
        startFlow();
    }

    private synchronized void startFlow() throws IOException, Exception
    {
        String path = configPath + "/flow";
        Stat stat   = client.checkExists().forPath(path);

        if ( stat == null) {
            return;
        }

        List<String> pathList = client.getChildren().forPath(path);

        for (String childPath : pathList) {
            byte[] data = client.getData().forPath(path + "/" + childPath);
            String json = new String(data);
            Flow flow   = mapper.readValue(json, Flow.class);

            if (flowConfiguration.exists(flow)) {
                continue;
            }

            flowConfiguration.create(flow);
            log.info(String.format("Flow %s created", flow.getName()));
        }
    }

    private synchronized void startType() throws IOException, Exception
    {
        String path = configPath + "/type";
        Stat stat   = client.checkExists().forPath(path);

        if ( stat == null) {
            return;
        }

        List<String> pathList = client.getChildren().forPath(path);

        for (String childPath : pathList) {
            byte[] data = client.getData().forPath(path + "/" + childPath);
            String json = new String(data);
            Type type   = mapper.readValue(json, Type.class);

            typeConfiguration.create(type);
            log.info(String.format("Type %s created", type.getName()));
        }
    }

    public synchronized void destroy() throws IOException, Exception
    {
        //@TODO - Stop AMQP Sinks
    }
}
