package org.cad.interruptus.repository.zookeeper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.cad.interruptus.core.GsonSerializer;
import org.cad.interruptus.entity.Configuration;
import org.cad.interruptus.entity.Entity;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationManagerTest
{
    GsonSerializer<Configuration> serializer;
    AtomicReference<Configuration> reference;
    CuratorFramework client;
    String path;

    @Before
    public void setUp()
    {
        path       = "/path/to/config";
        serializer = mock(GsonSerializer.class);
        reference  = mock(AtomicReference.class);
        client     = mock(CuratorFramework.class, RETURNS_MOCKS);
    }

    @Test
    public void testMutexCall() throws Exception
    {
        final ConfigurationManager manager  = new ConfigurationManager(client, reference, serializer, path);
        final InterProcessLock mutex        = mock(InterProcessLock.class);
        final Throwable throwable           = new RuntimeException();
        final Callable callable             = mock(Callable.class);

        manager.mutex(callable, mutex);

        verify(mutex).acquire();
        verify(callable).call();
        verify(mutex).release();

        reset(mutex, callable);

        when(callable.call()).thenThrow(throwable);

        try {
            manager.mutex(callable, mutex);
        } catch (Exception e) {
            assertSame(e, throwable);
        }

        verify(mutex).acquire();
        verify(callable).call();
        verify(mutex).release();
    }
    
    @Test
    public void testGetLoadedConfiguration() throws Exception
    {
        final ConfigurationManager manager  = new ConfigurationManager(client, reference, serializer, path);
        final Configuration configuration   = mock(Configuration.class);

        reference.set(configuration);

        assertSame(configuration, manager.get());
    }
    
    @Test
    public void testMapConfiguration() throws Exception
    {
        final ConfigurationManager manager  = new ConfigurationManager(client, reference, serializer, path);
        final Configuration configuration   = mock(Configuration.class);
        final Entity entity1                = mock(Entity.class);
        final Entity entity2                = mock(Entity.class);
        final Map<String, Entity> map       = new HashMap<>();

        map.put("entity1", entity1);
        map.put("entity2", entity2);
        reference.set(configuration);

        when(configuration.mapOf(eq(Entity.class))).thenReturn(map);

        assertEquals(map, manager.map(Entity.class));
        verify(configuration).mapOf(eq(Entity.class));
    }

    @Test
    public void testListConfiguration() throws Exception
    {
        final ConfigurationManager manager  = new ConfigurationManager(client, reference, serializer, path);
        final Configuration configuration   = mock(Configuration.class);
        final Entity entity1                = mock(Entity.class);
        final Entity entity2                = mock(Entity.class);
        final Map<String, Entity> map       = new HashMap<>();

        map.put("entity1", entity1);
        map.put("entity2", entity2);
        reference.set(configuration);

        when(configuration.mapOf(eq(Entity.class))).thenReturn(map);
        
        final List<Entity> result = manager.list(Entity.class);

        assertEquals(2, result.size());
        assertTrue(result.contains(entity1));
        assertTrue(result.contains(entity2));
        verify(configuration).mapOf(eq(Entity.class));
    }

    @Test
    public void testSave() throws Exception
    {
        final ConfigurationManager manager  = new ConfigurationManager(client, reference, serializer, path);
        final Configuration configuration   = mock(Configuration.class);
        final Entity entity                 = mock(Entity.class);

        reference.set(configuration);
        manager.save(entity);

        verify(configuration).put(entity);
    }

    @Test
    public void testRemove() throws Exception
    {
        final ConfigurationManager manager  = new ConfigurationManager(client, reference, serializer, path);
        final Configuration configuration   = mock(Configuration.class);
        final String entityId               = "foo";

        reference.set(configuration);
        manager.remove(Entity.class, entityId);

        verify(configuration).remove(Entity.class, entityId);
    }
}
