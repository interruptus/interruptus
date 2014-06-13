
package org.cad.interruptus.repository.zookeeper;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cad.interruptus.core.EntityNotFoundException;
import org.cad.interruptus.entity.Entity;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class AbstractConfigurationRepositoryTest
{
    ConfigurationManager manager;

    @Before
    public void setUp()
    {
        manager = mock(ConfigurationManager.class);
    }

    @Test
    public void testFindAll() throws Exception
    {
        final AbstractConfigurationRepository instance = new AbstractConfigurationRepository<Entity>(manager){};

        final Entity entity1    = mock(Entity.class);
        final Entity entity2    = mock(Entity.class);

        when(manager.list(Entity.class)).thenReturn(Lists.newArrayList(
            entity1, entity2
        ));

        final List result = instance.findAll();

        assertEquals(2, result.size());
        assertEquals(entity1, result.get(0));
        assertEquals(entity2, result.get(1));
        verify(manager).list(Entity.class);
    }

    @Test
    public void testFindById() throws Exception
    {
        final AbstractConfigurationRepository instance = new AbstractConfigurationRepository<Entity>(manager){};

        final Entity entity1           = mock(Entity.class);
        final Entity entity2           = mock(Entity.class);
        final Map<String, Entity>  map = new HashMap<>();

        map.put("e1", entity1);
        map.put("e2", entity2);

        when(manager.map(Entity.class)).thenReturn(map);

        final Entity result1 = instance.findById("e1");
        final Entity result2 = instance.findById("e2");

        verify(manager, times(2)).map(Entity.class);
        assertSame(result1, entity1);
        assertSame(result2, entity2);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testFindByIdEntityNotFoundException() throws Exception
    {
        final AbstractConfigurationRepository instance = new AbstractConfigurationRepository<Entity>(manager){};

        final Entity entity1           = mock(Entity.class);
        final Entity entity2           = mock(Entity.class);
        final Map<String, Entity>  map = new HashMap<>();

        map.put("e1", entity1);
        map.put("e2", entity2);

        when(manager.map(Entity.class)).thenReturn(map);

        final Entity result1 = instance.findById("e1");
        final Entity result2 = instance.findById("e2");

        assertSame(result1, entity1);
        assertSame(result2, entity2);

        instance.findById("unknown");
    }

    @Test
    public void testSave() throws Exception
    {
        final AbstractConfigurationRepository instance = new AbstractConfigurationRepository<Entity>(manager){};
        final Entity entity = mock(Entity.class);

        instance.save(entity);

        verify(manager).save(eq(entity));
        verify(manager).flush();
    }

    @Test
    public void testRemove() throws Exception
    {
        final AbstractConfigurationRepository instance = new AbstractConfigurationRepository<Entity>(manager){};
        final String identifier = "eid";

        instance.remove(identifier);

        verify(manager).remove(eq(Entity.class), eq(identifier));
        verify(manager).flush();
    }
}
