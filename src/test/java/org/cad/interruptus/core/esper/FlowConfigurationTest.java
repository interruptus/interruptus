package org.cad.interruptus.core.esper;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.dataflow.EPDataFlowInstance;
import com.espertech.esper.client.dataflow.EPDataFlowRuntime;
import com.espertech.esper.client.dataflow.EPDataFlowState;
import java.util.List;
import org.cad.interruptus.entity.Flow;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class FlowConfigurationTest
{
    EPServiceProvider epService;
    EPAdministrator epAdministrator;

    @Before
    public void setUp()
    {
        epService       = mock(EPServiceProvider.class);
        epAdministrator = mock(EPAdministrator.class);

        when(epService.getEPAdministrator()).thenReturn(epAdministrator);
    }

    @Test
    public void testList()
    {
        final EPRuntime epRuntime           = mock(EPRuntime.class);
        final EPDataFlowRuntime flowRuntime = mock(EPDataFlowRuntime.class);
        final FlowConfiguration instance    = new FlowConfiguration(epService, epAdministrator);

        when(epService.getEPRuntime()).thenReturn(epRuntime);
        when(epRuntime.getDataFlowRuntime()).thenReturn(flowRuntime);
        when(flowRuntime.getDataFlows()).thenReturn(new String[]{
            "flow1", "flow2"
        });

        final List<String> result = instance.list();

        assertEquals(2, result.size());
        assertEquals("flow1", result.get(0));
        assertEquals("flow2", result.get(1));

        verify(flowRuntime).getDataFlows();
    }

    @Test
    public void testSaveNewFlow()
    {
        final String name                = "flowX";
        final String query               = "EPL ... EPL";
        final Flow flow                  = new Flow(name, query);
        final FlowConfiguration instance = new FlowConfiguration(epService, epAdministrator);

        when(epAdministrator.getStatement(eq(name))).thenReturn(null);

        instance.save(flow);

        verify(epAdministrator).createEPL(eq(query), eq(name));
    }

    @Test
    public void testSaveExistingFlow()
    {
        final String name                     = "flowX";
        final String query                    = "EPL ... EPL";
        final Flow flow                       = new Flow(name, query);
        final FlowConfiguration instance      = new FlowConfiguration(epService, epAdministrator);
        final EPDataFlowInstance flowInstance = mock(EPDataFlowInstance.class);
        final EPDataFlowRuntime flowRuntime   = mock(EPDataFlowRuntime.class);
        final EPStatement sttm                = mock(EPStatement.class);
        final EPRuntime epRuntime             = mock(EPRuntime.class);

        when(flowRuntime.getSavedInstance(eq(name))).thenReturn(flowInstance);
        when(epAdministrator.getStatement(eq(name))).thenReturn(sttm);
        when(epRuntime.getDataFlowRuntime()).thenReturn(flowRuntime);
        when(epService.getEPRuntime()).thenReturn(epRuntime);

        instance.save(flow);

        verify(sttm).destroy();
        verify(epAdministrator).createEPL(eq(query), eq(name));
    }
    
    @Test
    public void testRemoveExistingFlow()
    {
        final String name                     = "flowX";
        final FlowConfiguration instance      = new FlowConfiguration(epService, epAdministrator);
        final EPDataFlowInstance flowInstance = mock(EPDataFlowInstance.class);
        final EPDataFlowRuntime flowRuntime   = mock(EPDataFlowRuntime.class);
        final EPStatement sttm                = mock(EPStatement.class);
        final EPRuntime epRuntime             = mock(EPRuntime.class);

        when(flowRuntime.getSavedInstance(eq(name))).thenReturn(flowInstance);
        when(epAdministrator.getStatement(eq(name))).thenReturn(sttm);
        when(epRuntime.getDataFlowRuntime()).thenReturn(flowRuntime);
        when(epService.getEPRuntime()).thenReturn(epRuntime);

        assertTrue(instance.remove(name));

        verify(sttm).destroy();
        verify(flowInstance).cancel();
        verify(flowRuntime).removeSavedInstance(eq(name));
    }

    @Test
    public void testRemoveNotExistingFlow()
    {
        final String name                   = "flowX";
        final FlowConfiguration instance    = new FlowConfiguration(epService, epAdministrator);
        final EPDataFlowRuntime flowRuntime = mock(EPDataFlowRuntime.class);
        final EPRuntime epRuntime           = mock(EPRuntime.class);

        when(flowRuntime.getSavedInstance(eq(name))).thenReturn(null);
        when(epAdministrator.getStatement(eq(name))).thenReturn(null);
        when(epRuntime.getDataFlowRuntime()).thenReturn(flowRuntime);
        when(epService.getEPRuntime()).thenReturn(epRuntime);

        assertTrue(instance.remove(name));
        verify(flowRuntime, never()).removeSavedInstance(eq(name));
    }

    @Test
    public void testStartFlow()
    {
        final String name                     = "flowX";
        final FlowConfiguration instance      = new FlowConfiguration(epService, epAdministrator);
        final EPDataFlowRuntime flowRuntime   = mock(EPDataFlowRuntime.class);
        final EPDataFlowInstance flowInstance = mock(EPDataFlowInstance.class);
        final EPRuntime epRuntime             = mock(EPRuntime.class);

        when(flowRuntime.getSavedInstance(eq(name))).thenReturn(null);
        when(epRuntime.getDataFlowRuntime()).thenReturn(flowRuntime);
        when(flowRuntime.instantiate(eq(name))).thenReturn(flowInstance);
        when(epService.getEPRuntime()).thenReturn(epRuntime);

        assertTrue(instance.start(name));

        verify(flowInstance).start();
        verify(flowRuntime).instantiate(eq(name));
        verify(flowRuntime).saveInstance(eq(name), eq(flowInstance));
    }

    @Test
    public void testStartExistingFlow()
    {
        final String name                     = "flowX";
        final FlowConfiguration instance      = new FlowConfiguration(epService, epAdministrator);
        final EPDataFlowRuntime flowRuntime   = mock(EPDataFlowRuntime.class);
        final EPDataFlowInstance flowInstance = mock(EPDataFlowInstance.class);
        final EPRuntime epRuntime             = mock(EPRuntime.class);

        when(flowRuntime.getSavedInstance(eq(name))).thenReturn(flowInstance);
        when(epRuntime.getDataFlowRuntime()).thenReturn(flowRuntime);
        when(flowRuntime.instantiate(eq(name))).thenReturn(flowInstance);
        when(epService.getEPRuntime()).thenReturn(epRuntime);

        assertTrue(instance.start(name));

        verify(flowInstance).start();
        verify(flowRuntime, never()).instantiate(eq(name));
        verify(flowRuntime, never()).saveInstance(eq(name), eq(flowInstance));
    }
    
    @Test
    public void testStartRunningFlow()
    {
        final String name                     = "flowX";
        final FlowConfiguration instance      = new FlowConfiguration(epService, epAdministrator);
        final EPDataFlowRuntime flowRuntime   = mock(EPDataFlowRuntime.class);
        final EPDataFlowInstance flowInstance = mock(EPDataFlowInstance.class);
        final EPRuntime epRuntime             = mock(EPRuntime.class);

        when(flowRuntime.getSavedInstance(eq(name))).thenReturn(flowInstance);
        when(flowRuntime.instantiate(eq(name))).thenReturn(flowInstance);
        when(epRuntime.getDataFlowRuntime()).thenReturn(flowRuntime);
        when(epService.getEPRuntime()).thenReturn(epRuntime);
        when(flowInstance.getState()).thenReturn(EPDataFlowState.RUNNING);

        assertTrue(instance.start(name));

        verify(flowInstance, never()).start();
        verify(flowRuntime, never()).instantiate(eq(name));
        verify(flowRuntime, never()).saveInstance(eq(name), eq(flowInstance));
    }

    @Test
    public void testStopFlow()
    {
        final String name                     = "flowX";
        final FlowConfiguration instance      = new FlowConfiguration(epService, epAdministrator);
        final EPDataFlowRuntime flowRuntime   = mock(EPDataFlowRuntime.class);
        final EPDataFlowInstance flowInstance = mock(EPDataFlowInstance.class);
        final EPRuntime epRuntime             = mock(EPRuntime.class);

        when(flowRuntime.getSavedInstance(eq(name))).thenReturn(flowInstance);
        when(epRuntime.getDataFlowRuntime()).thenReturn(flowRuntime);
        when(epService.getEPRuntime()).thenReturn(epRuntime);

        assertTrue(instance.stop(name));
        verify(flowInstance).cancel();
    }

    @Test
    public void testStopExistingFlow()
    {
        final String name                     = "flowX";
        final FlowConfiguration instance      = new FlowConfiguration(epService, epAdministrator);
        final EPDataFlowRuntime flowRuntime   = mock(EPDataFlowRuntime.class);
        final EPDataFlowInstance flowInstance = mock(EPDataFlowInstance.class);
        final EPRuntime epRuntime             = mock(EPRuntime.class);

        when(flowRuntime.getSavedInstance(eq(name))).thenReturn(flowInstance);
        when(epRuntime.getDataFlowRuntime()).thenReturn(flowRuntime);
        when(epService.getEPRuntime()).thenReturn(epRuntime);

        assertTrue(instance.stop(name));
        verify(flowInstance).cancel();
    }

    @Test
    public void testStopCanceledFlow()
    {
        final String name                     = "flowX";
        final FlowConfiguration instance      = new FlowConfiguration(epService, epAdministrator);
        final EPDataFlowRuntime flowRuntime   = mock(EPDataFlowRuntime.class);
        final EPDataFlowInstance flowInstance = mock(EPDataFlowInstance.class);
        final EPRuntime epRuntime             = mock(EPRuntime.class);

        when(flowRuntime.getSavedInstance(eq(name))).thenReturn(flowInstance);
        when(flowInstance.getState()).thenReturn(EPDataFlowState.CANCELLED);
        when(epRuntime.getDataFlowRuntime()).thenReturn(flowRuntime);
        when(epService.getEPRuntime()).thenReturn(epRuntime);

        assertTrue(instance.stop(name));
        verify(flowInstance, never()).cancel();
    }
}
