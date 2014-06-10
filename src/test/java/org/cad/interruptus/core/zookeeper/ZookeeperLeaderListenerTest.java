package org.cad.interruptus.core.zookeeper;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.cad.interruptus.core.esper.FlowConfiguration;
import org.cad.interruptus.core.esper.StatementConfiguration;
import org.cad.interruptus.entity.Configuration;
import org.cad.interruptus.entity.Flow;
import org.cad.interruptus.entity.Statement;
import org.cad.interruptus.entity.Type;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ZookeeperLeaderListenerTest
{
    StatementConfiguration statementConfiguration;
    AtomicReference<Configuration> reference;
    FlowConfiguration flowConfiguration;
    AtomicBoolean isLeader;

    @Before
    public void setUp()
    {
        statementConfiguration = mock(StatementConfiguration.class);
        flowConfiguration      = mock(FlowConfiguration.class);

        reference = new AtomicReference<>();
        isLeader  = new AtomicBoolean(false);
    }

    @Test
    public void testIsLeaderStartFlow()
    {
        final ZookeeperLeaderListener instance = new ZookeeperLeaderListener(isLeader, flowConfiguration, statementConfiguration, reference);
        
        final Flow f1               = new Flow("f1", "...", true);
        final Flow f2               = new Flow("f2", "...", false);
        final Configuration config  = new Configuration(new HashMap<String, Type>(), new HashMap<String, Flow>(), new HashMap<String, Statement>());

        config.put(f1);
        config.put(f2);

        when(flowConfiguration.list()).thenReturn(Lists.newArrayList("f1", "f2"));

        reference.set(config);
        isLeader.set(false);

        instance.isLeader();

        assertTrue(isLeader.get());

        verify(flowConfiguration).list();
        verify(flowConfiguration).start(eq("f1"));
        verify(flowConfiguration).start(eq("f2"));
    }

    @Test
    public void testNotLeaderStopFlow()
    {
        final ZookeeperLeaderListener instance = new ZookeeperLeaderListener(isLeader, flowConfiguration, statementConfiguration, reference);

        final Flow f1               = new Flow("f1", "...", true);
        final Flow f2               = new Flow("f2", "...", false);
        final Configuration config  = new Configuration(new HashMap<String, Type>(), new HashMap<String, Flow>(), new HashMap<String, Statement>());

        config.put(f1);
        config.put(f2);

        when(flowConfiguration.list()).thenReturn(Lists.newArrayList("f1", "f2"));

        reference.set(config);
        isLeader.set(true);

        instance.notLeader();

        assertFalse(isLeader.get());

        verify(flowConfiguration).list();
        verify(flowConfiguration).stop(eq("f1"));
        verify(flowConfiguration, never()).stop(eq("f2"));
    }
    
    @Test
    public void testIsLeaderStartStatement()
    {
        final ZookeeperLeaderListener instance = new ZookeeperLeaderListener(isLeader, flowConfiguration, statementConfiguration, reference);
        
        final Statement s1               = new Statement("s1", "...", true);
        final Statement s2               = new Statement("s2", "...", false);
        final Configuration config  = new Configuration(new HashMap<String, Type>(), new HashMap<String, Flow>(), new HashMap<String, Statement>());

        config.put(s1);
        config.put(s2);

        when(statementConfiguration.list()).thenReturn(Lists.newArrayList("s1", "s2"));

        reference.set(config);
        isLeader.set(false);

        instance.isLeader();

        assertTrue(isLeader.get());

        verify(statementConfiguration).list();
        verify(statementConfiguration).start(eq("s1"));
        verify(statementConfiguration).start(eq("s2"));
    }

    @Test
    public void testNotLeaderStopStatement()
    {
        final ZookeeperLeaderListener instance = new ZookeeperLeaderListener(isLeader, flowConfiguration, statementConfiguration, reference);

        final Statement s1          = new Statement("s1", "...", true);
        final Statement s2          = new Statement("s2", "...", false);
        final Configuration config  = new Configuration(new HashMap<String, Type>(), new HashMap<String, Flow>(), new HashMap<String, Statement>());

        config.put(s1);
        config.put(s2);

        when(statementConfiguration.list()).thenReturn(Lists.newArrayList("s1", "s2"));

        reference.set(config);
        isLeader.set(true);

        instance.notLeader();

        assertFalse(isLeader.get());

        verify(statementConfiguration).list();
        verify(statementConfiguration).stop(eq("s1"));
        verify(statementConfiguration, never()).stop(eq("s2"));
    }
}
