package org.control_alt_del.interruptus.core.zookeeper;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.control_alt_del.interruptus.core.esper.StatementConfiguration;
import org.control_alt_del.interruptus.entity.Statement;

public class StatementConfigurationListener implements ZookeeperPathListener
{
    private static Log logger = LogFactory.getLog(StatementConfigurationListener.class);

    private StatementConfiguration config;

    private ObjectMapper mapper = new ObjectMapper();

    private String path;

    public StatementConfigurationListener(String path, StatementConfiguration configuration) {
        this.path   = path;
        this.config = configuration;
    }

    @Override
    public String getPath()
    {
        return path;
    }

    @Override
    public void executor(CuratorFramework client, PathChildrenCacheEvent childEvent) throws IOException
    {
        logger.info(String.format("Event \"%s\" at node \"%s\"", childEvent.getType(), childEvent.getData().getPath()));

        byte[] data     = childEvent.getData().getData();
        String json     = new String(data);
        Statement sttm  = mapper.readValue(json, Statement.class);

        if(config.exists(sttm)) {
            config.destroy(sttm);
        }

        if(PathChildrenCacheEvent.Type.CHILD_REMOVED != childEvent.getType()) {
            config.create(sttm);
        }
    }
}
