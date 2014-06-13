package org.cad.interruptus.core.esper;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import java.util.List;
import org.cad.interruptus.entity.Statement;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class StatementConfigurationTest
{
    EPAdministrator epAdministrator;

    @Before
    public void setUp()
    {
        epAdministrator = mock(EPAdministrator.class);
    }

    @Test
    public void testList()
    {
        final StatementConfiguration instance = new StatementConfiguration(epAdministrator);

        when(epAdministrator.getStatementNames()).thenReturn(new String[]{
            "sttm1", "sttm2"
        });

        final List<String> result = instance.list();

        assertEquals(2, result.size());
        assertEquals("sttm1", result.get(0));
        assertEquals("sttm2", result.get(1));
    }
    
    @Test
    public void testSaveNewStatement()
    {
        final EPStatement existing            = null;
        final String name                     = "sttm1";
        final String query                    = "sttm...";
        final EPStatement sttm                = mock(EPStatement.class);
        final Statement statement             = new Statement(name, query, false);
        final StatementConfiguration instance = new StatementConfiguration(epAdministrator);

        statement.setStarted(false);
        when(sttm.isStopped()).thenReturn(false);
        when(epAdministrator.getStatement(eq(name))).thenReturn(existing);
        when(epAdministrator.createEPL(eq(query), eq(name))).thenReturn(sttm);

        instance.save(statement);

        verify(sttm).stop();
        verify(epAdministrator).getStatement(eq(name));
        verify(epAdministrator).createEPL(eq(query), eq(name));
    }

    @Test
    public void testSaveExistingStatement()
    {
        final String name                     = "sttm1";
        final String query                    = "sttm...";
        final EPStatement sttm                = mock(EPStatement.class);
        final EPStatement existing            = mock(EPStatement.class);
        final Statement statement             = new Statement(name, query, false);
        final StatementConfiguration instance = new StatementConfiguration(epAdministrator);

        statement.setStarted(true);
        when(sttm.isStopped()).thenReturn(false);
        when(epAdministrator.getStatement(eq(name))).thenReturn(existing);
        when(epAdministrator.createEPL(eq(query), eq(name))).thenReturn(sttm);

        instance.save(statement);

        verify(existing).destroy();
        verify(sttm, never()).stop();
        verify(epAdministrator, times(2)).getStatement(eq(name));
        verify(epAdministrator).createEPL(eq(query), eq(name));
    }

    @Test
    public void testRemoveExistingStatement()
    {
        final String name                     = "sttm1";
        final EPStatement existing            = mock(EPStatement.class);
        final StatementConfiguration instance = new StatementConfiguration(epAdministrator);

        when(existing.isStarted()).thenReturn(true);
        when(epAdministrator.getStatement(eq(name))).thenReturn(existing);

        assertTrue(instance.remove(name));

        verify(existing).destroy();
        verify(epAdministrator).getStatement(eq(name));
    }

    @Test
    public void testRemoveNotExistingStatement()
    {
        final String name                     = "sttm1";
        final EPStatement existing            = null;
        final StatementConfiguration instance = new StatementConfiguration(epAdministrator);

        when(epAdministrator.getStatement(eq(name))).thenReturn(existing);

        assertTrue(instance.remove(name));
        verify(epAdministrator).getStatement(eq(name));
    }

    @Test
    public void testStartStatement()
    {
        final String name                     = "sttm1";
        final EPStatement sttm                = mock(EPStatement.class);
        final StatementConfiguration instance = new StatementConfiguration(epAdministrator);

        when(sttm.isStarted()).thenReturn(false);
        when(epAdministrator.getStatement(eq(name))).thenReturn(sttm);

        assertTrue(instance.start(name));

        verify(sttm).start();
        verify(epAdministrator).getStatement(eq(name));
    }

    @Test
    public void testStartRunningStatement()
    {
        final String name                     = "sttm1";
        final EPStatement sttm                = mock(EPStatement.class);
        final StatementConfiguration instance = new StatementConfiguration(epAdministrator);

        when(sttm.isStarted()).thenReturn(true);
        when(epAdministrator.getStatement(eq(name))).thenReturn(sttm);

        assertTrue(instance.start(name));

        verify(sttm, never()).start();
        verify(epAdministrator).getStatement(eq(name));
    }
    
    @Test
    public void testStartNotExistingStatement()
    {
        final String name                     = "sttm1";
        final EPStatement sttm                = null;
        final StatementConfiguration instance = new StatementConfiguration(epAdministrator);

        when(epAdministrator.getStatement(eq(name))).thenReturn(sttm);

        assertFalse(instance.start(name));
        verify(epAdministrator).getStatement(eq(name));
    }
    
    @Test
    public void testStopStatement()
    {
        final String name                     = "sttm1";
        final EPStatement sttm                = mock(EPStatement.class);
        final StatementConfiguration instance = new StatementConfiguration(epAdministrator);

        when(sttm.isStopped()).thenReturn(false);
        when(epAdministrator.getStatement(eq(name))).thenReturn(sttm);

        assertTrue(instance.stop(name));

        verify(sttm).stop();
        verify(epAdministrator).getStatement(eq(name));
    }

    @Test
    public void testStopRunningStatement()
    {
        final String name                     = "sttm1";
        final EPStatement sttm                = mock(EPStatement.class);
        final StatementConfiguration instance = new StatementConfiguration(epAdministrator);

        when(sttm.isStopped()).thenReturn(true);
        when(epAdministrator.getStatement(eq(name))).thenReturn(sttm);

        assertTrue(instance.stop(name));

        verify(sttm, never()).stop();
        verify(epAdministrator).getStatement(eq(name));
    }
    
    @Test
    public void testStopNotExistingStatement()
    {
        final String name                     = "sttm1";
        final EPStatement sttm                = null;
        final StatementConfiguration instance = new StatementConfiguration(epAdministrator);

        when(epAdministrator.getStatement(eq(name))).thenReturn(sttm);

        assertFalse(instance.stop(name));
        verify(epAdministrator).getStatement(eq(name));
    }
}
