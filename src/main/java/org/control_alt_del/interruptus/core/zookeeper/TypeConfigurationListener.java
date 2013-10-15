package org.control_alt_del.interruptus.core.zookeeper;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.apache.commons.logging.LogFactory;
import org.control_alt_del.interruptus.entity.Type;
import org.apache.curator.framework.CuratorFramework;
import org.control_alt_del.interruptus.core.esper.TypeConfiguration;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

public class TypeConfigurationListener implements ZookeeperPathListener
{
    private static Log logger = LogFactory.getLog(TypeConfigurationListener.class);

    private TypeConfiguration config;

    private ObjectMapper mapper = new ObjectMapper();

    private String path;

    public TypeConfigurationListener(String path, TypeConfiguration typeConfiguration) {
        this.path   = path;
        this.config = typeConfiguration;
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

        byte[] data = childEvent.getData().getData();
        String json = new String(data);
        Type type   = mapper.readValue(json, Type.class);

        if(PathChildrenCacheEvent.Type.CHILD_ADDED != childEvent.getType()) {
            config.destroy(type);
        }

        if(PathChildrenCacheEvent.Type.CHILD_REMOVED != childEvent.getType()) {
            config.create(type);
        }
    }
}
