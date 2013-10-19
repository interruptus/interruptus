package org.control_alt_del.interruptus.core.zookeeper;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import org.apache.zookeeper.data.Stat;
import org.apache.commons.logging.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.control_alt_del.interruptus.core.esper.EsperConfiguration;

abstract class AbstractConfigurationListener<T> implements ZookeeperConfigurationListener
{
    protected Log logger = LogFactory.getLog(getClass());
    protected Map<String, Integer> childVersion = new HashMap<String, Integer>();
    protected ObjectMapper mapper = new ObjectMapper();
    protected EsperConfiguration<T> config;
    protected Class<T> targetClass;
    protected String path;

    public AbstractConfigurationListener(String path, EsperConfiguration<T> configuration) {
        this.path        = path;
        this.config      = configuration;
        this.targetClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public String getPath()
    {
        return path;
    }

    @Override
    public void invoke(CuratorFramework client, EventType eventType, Stat stat, String path, byte[] data) throws IOException
    {
        logger.info(String.format("Event \"%s\" for node \"%s\"", eventType, path));
        
        int version = stat.getVersion();
        String json = new String(data);
        T flow      = mapper.readValue(json, this.targetClass);

        if ( ! childVersion.containsKey(path)){
            childVersion.put(path, -1);
        }

        if (EventType.REMOVED != eventType && childVersion.get(path).equals(version)) {
            logger.info("Ignore change");

            return;
        }

        childVersion.put(path, version);

        if (config.exists(flow)) {
            config.destroy(flow);
        }

        if(EventType.REMOVED != eventType) {
            config.create(flow);
        }
    }
}
