package org.cad.interruptus.core.esper;

import java.util.List;
import java.util.ArrayList;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.dataflow.EPDataFlowDescriptor;
import com.espertech.esper.client.dataflow.EPDataFlowInstance;
import com.espertech.esper.client.dataflow.EPDataFlowRuntime;
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
        epAdministrator.createEPL(flow.getQuery(), flow.getName());

        start(flow);
    }

    public Boolean start(final Flow flow)
    {
        final EPRuntime epRuntime             = epService.getEPRuntime();
        final EPDataFlowRuntime flowRuntime   = epRuntime.getDataFlowRuntime();
        final EPDataFlowInstance instance     = flowRuntime.instantiate(flow.getName());

        if (instance == null) {
            return false;
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

    // @TODO - FIX IT !! Does not remove the flow definition
    @Override
    public Boolean remove(final String name)
    {
        final EPRuntime epRuntime             = epService.getEPRuntime();
        final EPDataFlowRuntime flowRuntime   = epRuntime.getDataFlowRuntime();
        final EPDataFlowInstance instance     = flowRuntime.instantiate(name);

        instance.cancel();

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
