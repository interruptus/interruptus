package org.cad.interruptus.core.zookeeper;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import org.apache.zookeeper.data.Stat;
import org.apache.commons.logging.Log;
import com.google.gson.Gson;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.cad.interruptus.core.esper.EsperConfiguration;

abstract class AbstractConfigurationListener<T> implements ZookeeperConfigurationListener
{
    protected final Log logger = LogFactory.getLog(getClass());
    protected final Map<String, Integer> childVersion = new HashMap<>();
    protected final EsperConfiguration<T> config;
    protected final Class<T> targetClass;
    protected final String path;
    protected final Gson gson;

    public AbstractConfigurationListener(final String path, final EsperConfiguration<T> configuration, final Gson gson)
    {
        this.path        = path;
        this.gson        = gson;
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

        if (data == null || data.length == 0){
            logger.info("Ignoring empty event data");

            return;
        }

        final String json = new String(data);
        final int version = stat.getVersion();
        final T flow      = gson.fromJson(json, this.targetClass);
        
        if ( ! childVersion.containsKey(path)){
            childVersion.put(path, -1);
        }

        if (EventType.REMOVED != eventType && childVersion.get(path).equals(version)) {
            logger.info("Ignoring duplicated event");

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
