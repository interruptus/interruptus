package org.cad.interruptus.core.esper;

import java.util.List;
import java.util.ArrayList;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.dataflow.EPDataFlowDescriptor;
import com.espertech.esper.client.dataflow.EPDataFlowInstance;
import com.espertech.esper.client.dataflow.EPDataFlowRuntime;
import com.espertech.esper.client.dataflow.EPDataFlowState;
import org.cad.interruptus.entity.Flow;

public class FlowConfiguration implements EsperConfiguration<String, Flow>
{
    private final EPServiceProvider epService;
    private final EPAdministrator epAdministrator;

    public FlowConfiguration(final EPServiceProvider epService, final EPAdministrator epAdministrator)
    {
        this.epService       = epService;
        this.epAdministrator = epAdministrator;
    }

    @Override
    public List<Flow> list()
    {
        final EPDataFlowRuntime flowRuntime = epService.getEPRuntime().getDataFlowRuntime();
        final String[] dataFlowsNames       = flowRuntime.getDataFlows();
        final List<Flow> list               = new ArrayList<>();

        for (String name : dataFlowsNames) {
            final EPDataFlowDescriptor descriptor = flowRuntime.getDataFlow(name);
            final Flow flow                       = new Flow(name, descriptor.getStatementName());

            list.add(flow);
        }

        return list;
    }

    @Override
    public void save(final Flow flow)
    {
        final String name      = flow.getName();
        final String query     = flow.getQuery();
        final EPStatement sttm = epAdministrator.getStatement(name);
        
        if (sttm != null) {
            remove(name);
        }

        epAdministrator.createEPL(query, name);
    }

    public Boolean start(final String name)
    {
        final EPRuntime epRuntime             = epService.getEPRuntime();
        final EPDataFlowRuntime flowRuntime   = epRuntime.getDataFlowRuntime();
        final EPDataFlowInstance instance     = flowRuntime.instantiate(name);

        if (instance == null) {
            return false;
        }
        
        if (instance.getState() == EPDataFlowState.RUNNING) {
            return true;
        }

        instance.start();

        return true;
    }
    
    public Boolean stop(final String name)
    {
        final EPRuntime epRuntime             = epService.getEPRuntime();
        final EPDataFlowRuntime flowRuntime   = epRuntime.getDataFlowRuntime();
        final EPDataFlowInstance instance     = flowRuntime.instantiate(name);

        if (instance == null) {
            return false;
        }
        
        instance.cancel();

        return true;
    }
    
    public EPDataFlowState getFlowState(final String name)
    {
        final EPRuntime epRuntime             = epService.getEPRuntime();
        final EPDataFlowRuntime flowRuntime   = epRuntime.getDataFlowRuntime();
        final EPDataFlowInstance instance     = flowRuntime.instantiate(name);

        if (instance == null) {
            return null;
        }

        return instance.getState();
    }

    @Override
    public Boolean remove(final String name)
    {
        final EPRuntime epRuntime             = epService.getEPRuntime();
        final EPDataFlowRuntime flowRuntime   = epRuntime.getDataFlowRuntime();
        final EPDataFlowInstance instance     = flowRuntime.instantiate(name);
        final EPStatement sttm                = epAdministrator.getStatement(name);

        instance.cancel();
        sttm.destroy();

        return true;
    }

    @Override
    public Boolean remove(final Flow e)
    {
        return remove(e.getName());
    }
    
    @Override
    public Boolean exists(final String name)
    {
        final EPRuntime epRuntime             = epService.getEPRuntime();
        final EPDataFlowRuntime flowRuntime   = epRuntime.getDataFlowRuntime();
        final EPDataFlowDescriptor dataFlow   = flowRuntime.getDataFlow(name);

        return dataFlow != null;
    }
}
