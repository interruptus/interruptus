package org.cad.interruptus.service;

import com.google.common.base.Strings;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.cad.interruptus.Message;
import org.cad.interruptus.entity.Type;
import org.cad.interruptus.repository.TypeRepository;

public class InventoryService
{
    final Map<String, Set<String>> typeInventory = new HashMap<>();
    final ScheduledExecutorService scheduler;
    final CuratorFramework curator;
    final AtomicBoolean isLeader;
    final String rootPath;

    final Log logger = LogFactory.getLog(getClass());
    final TypeRepository repository;
    
    ScheduledFuture scheduledFuture;
    Integer interval = 5;

    public InventoryService(final TypeRepository repository, final CuratorFramework curator, final ScheduledExecutorService scheduler, final AtomicBoolean isLeader, final String rootPath) 
    {
        this.repository = repository;
        this.scheduler  = scheduler;
        this.curator    = curator;
        this.isLeader   = isLeader;
        this.rootPath   = rootPath;
    }

    public void setInterval(final Integer interval)
    {
        this.interval = interval;
    }

    public void start()
    {
        scheduledFuture = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {
                try {
                    saveInventoryData();
                } catch (Exception ex) {
                    logger.error(this, ex);
                }
            }
        }, interval, interval, TimeUnit.SECONDS);
    }

    public void stop() throws Exception
    {
        saveInventoryData();

        if (scheduledFuture == null) {
            return;
        }

        scheduledFuture.cancel(false);
    }

    public void collect(final Message message)
    {
        if ( ! isLeader.get()) {
            return;
        }

        final String typeName          = message.getType();
        final Map<String, Object> body = message.getBody();

        try {
            collectData(typeName, body);
        } catch (Exception ex) {
            logger.error(this, ex);
        }
    }
    
    protected void saveInventoryData() throws Exception
    {
        if (typeInventory.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Set<String>> entry : typeInventory.entrySet()) {
            final String type      = entry.getKey();
            final Set<String> data = entry.getValue();
            
            saveTypeData(type, data);
        }

        typeInventory.clear();
    }

    protected void saveTypeData(final String typeName, final Set<String> data) throws Exception
    {
        if (data.isEmpty()) {
            return;
        }

        for (final String hierarchy : data) {
            final String path = getInventoryPath(typeName, hierarchy);
            final Stat status = curator.checkExists().forPath(path);

            if (status != null) {
                continue;
            }

            logger.debug("Create inventory : " + path);
            curator.create()
                .creatingParentsIfNeeded()
                .forPath(path);
        }
    }

    protected void collectData(final String typeName, final Map<String, Object> body) throws Exception
    {
        final Type type         = repository.findById(typeName);
        final String hierarchy  = type.getHierarchy();

        if (hierarchy == null) {
            return;
        }

        final Set<String> inventory   = getInventory(typeName);
        final String[] treeSet        = hierarchy.split("\\.");
        final StringBuffer pathBuffer = new StringBuffer();
        
        for (final String property : treeSet) {
            final Object value = body.get(property);
            final String path  = "/" + value;

            pathBuffer.append(path);
            inventory.add(pathBuffer.toString());
        }
    }

    protected Set<String> getInventory(final String typeName) throws Exception
    {
        if ( ! typeInventory.containsKey(typeName)) {
            typeInventory.put(typeName, new TreeSet<String>());
        }

        return typeInventory.get(typeName);
    }

    protected String getInventoryPath(final String typeName, final String path)
    {
        if (Strings.isNullOrEmpty(path)) {
            return rootPath + "/" + typeName;
        }

        if (path.startsWith("/")) {
            return rootPath + "/" + typeName + path;
        }

        return rootPath + "/" + typeName + "/" + path;
    }

    public Set<String> getTypeHierarchyInventory(final String typeName, final String hierarchy) throws Exception
    {
        final String path = getInventoryPath(typeName, hierarchy);
        final Stat status = curator.checkExists().forPath(path);

        if (status == null) {
            return Collections.EMPTY_SET;
        }

        return new TreeSet<>(curator.getChildren().forPath(path));
    }
}
