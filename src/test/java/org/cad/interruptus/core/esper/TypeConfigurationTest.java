package org.cad.interruptus.core.esper;

import com.espertech.esper.client.ConfigurationOperations;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EventType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cad.interruptus.entity.Type;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class TypeConfigurationTest
{
    EPServiceProvider epService;

    @Before
    public void setUp()
    {
        epService = mock(EPServiceProvider.class);
    }

    @Test
    public void testListTypes()
    {
        final TypeConfiguration instance      = new TypeConfiguration(epService);
        final ConfigurationOperations config  = mock(ConfigurationOperations.class);
        final EPAdministrator admin           = mock(EPAdministrator.class);
        final EventType eventType1            = mock(EventType.class);
        final EventType eventType2            = mock(EventType.class);
        final EventType[] eventTypes          = new EventType[]{
            eventType1, eventType2
        };
        
        when(epService.getEPAdministrator()).thenReturn(admin);
        when(admin.getConfiguration()).thenReturn(config);
        when(config.getEventTypes()).thenReturn(eventTypes);
        when(eventType1.getName()).thenReturn("EventType1");
        when(eventType2.getName()).thenReturn("EventType2");

        final List<String> result = instance.list();

        assertEquals(2, result.size());
        assertEquals("EventType1", result.get(0));
        assertEquals("EventType2", result.get(1));

        verify(config).getEventTypes();
    }
    
    @Test
    public void testSaveNewType()
    {
        final TypeConfiguration instance      = new TypeConfiguration(epService);
        final ConfigurationOperations config  = mock(ConfigurationOperations.class);
        final EPAdministrator admin           = mock(EPAdministrator.class);
        final Map<String, String> properties  = new HashMap<>();
        final String name                     = "EventTypeX";
        final Type type                       = new Type(name, properties);
        final Map<String, Object> map         = new HashMap<>();

        properties.put("value", "string");
        properties.put("key", "string");
        map.putAll(properties);

        when(admin.getConfiguration()).thenReturn(config);
        when(epService.getEPAdministrator()).thenReturn(admin);
        when(config.isEventTypeExists(eq(name))).thenReturn(Boolean.FALSE);

        instance.save(type);

        verify(config).isEventTypeExists(eq(name));
        verify(config).addEventType(eq(name), eq(map));
    }
    
    @Test
    public void testSaveExistingType()
    {
        final TypeConfiguration instance      = new TypeConfiguration(epService);
        final ConfigurationOperations config  = mock(ConfigurationOperations.class);
        final EPAdministrator admin           = mock(EPAdministrator.class);
        final Map<String, String> properties  = new HashMap<>();
        final String name                     = "EventTypeY";
        final Type type                       = new Type(name, properties);
        final Map<String, Object> map         = new HashMap<>();

        properties.put("value", "string");
        properties.put("key", "string");
        map.putAll(properties);

        when(admin.getConfiguration()).thenReturn(config);
        when(epService.getEPAdministrator()).thenReturn(admin);
        when(config.isEventTypeExists(eq(name))).thenReturn(Boolean.TRUE);

        instance.save(type);

        verify(config).isEventTypeExists(eq(name));
        verify(config).updateMapEventType(eq(name), eq(map));
    }
    
    @Test
    public void testRemoveExistingType()
    {
        final TypeConfiguration instance      = new TypeConfiguration(epService);
        final ConfigurationOperations config  = mock(ConfigurationOperations.class);
        final EPAdministrator admin           = mock(EPAdministrator.class);
        final String name                     = "EventTypeY";

        when(admin.getConfiguration()).thenReturn(config);
        when(epService.getEPAdministrator()).thenReturn(admin);
        when(config.isEventTypeExists(eq(name))).thenReturn(Boolean.TRUE);
        when(config.removeEventType(eq(name), eq(true))).thenReturn(Boolean.TRUE);

        assertTrue(instance.remove(name));
        verify(config).isEventTypeExists(eq(name));
        verify(config).removeEventType(eq(name), eq(true));
    }

    @Test
    public void testRemoveNotExistingType()
    {
        final TypeConfiguration instance      = new TypeConfiguration(epService);
        final ConfigurationOperations config  = mock(ConfigurationOperations.class);
        final EPAdministrator admin           = mock(EPAdministrator.class);
        final String name                     = "EventTypeY";

        when(admin.getConfiguration()).thenReturn(config);
        when(epService.getEPAdministrator()).thenReturn(admin);
        when(config.isEventTypeExists(eq(name))).thenReturn(Boolean.FALSE);
        when(config.removeEventType(eq(name), eq(true))).thenReturn(Boolean.TRUE);

        assertTrue(instance.remove(name));
        verify(config).isEventTypeExists(eq(name));
        verify(config, never()).removeEventType(eq(name), eq(true));
    }
    
    @Test
    public void testStartType()
    {
        final TypeConfiguration instance = new TypeConfiguration(epService);

        assertFalse(instance.start("foo"));
        assertFalse(instance.start("bar"));
    }

    @Test
    public void testStopType()
    {
        final TypeConfiguration instance = new TypeConfiguration(epService);

        assertFalse(instance.stop("foo"));
        assertFalse(instance.stop("bar"));
    }
}