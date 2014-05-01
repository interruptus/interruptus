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

public class FlowConfiguration implements EsperConfiguration<Flow>
{
    private final EPServiceProvider epService;
    private final EPAdministrator epAdministrator;

    public FlowConfiguration(EPServiceProvider epService, EPAdministrator epAdministrator)
    {
        this.epService       = epService;
        this.epAdministrator = epAdministrator;
    }

    @Override
    public List<Flow> list()
    {
        EPDataFlowRuntime flowRuntime = epService.getEPRuntime().getDataFlowRuntime();
        String[] dataFlowsNames       = flowRuntime.getDataFlows();
        List<Flow> list               = new ArrayList<>();

        for (String name : dataFlowsNames) {
            EPDataFlowDescriptor descriptor = flowRuntime.getDataFlow(name);

            list.add(new Flow(name, descriptor.getStatementName()));
        }

        return list;

    }

    @Override
    public Flow create(Flow flow)
    {
        epAdministrator.createEPL(flow.getQuery(), flow.getName());

        EPRuntime epRuntime             = epService.getEPRuntime();
        EPDataFlowRuntime flowRuntime   = epRuntime.getDataFlowRuntime();
        EPDataFlowInstance instance     = flowRuntime.instantiate(flow.getName());

        instance.start();

        return flow;
    }

    public Boolean start(Flow flow)
    {
        EPRuntime epRuntime             = epService.getEPRuntime();
        EPDataFlowRuntime flowRuntime   = epRuntime.getDataFlowRuntime();
        EPDataFlowInstance instance     = flowRuntime.instantiate(flow.getName());

        if (instance == null) {
            return false;
        }

        instance.start();

        return true;
    }

    // @TODO - FIX IT !! Does not remove the flow definition
    @Override
    public Boolean destroy(Flow flow)
    {
        EPRuntime epRuntime             = epService.getEPRuntime();
        EPDataFlowRuntime flowRuntime   = epRuntime.getDataFlowRuntime();
        EPDataFlowInstance instance     = flowRuntime.instantiate(flow.getName());

        instance.cancel();

        return true;
    }

    @Override
    public Boolean exists(Flow flow)
    {
        EPRuntime epRuntime             = epService.getEPRuntime();
        EPDataFlowRuntime flowRuntime   = epRuntime.getDataFlowRuntime();
        EPDataFlowDescriptor dataFlow   = flowRuntime.getDataFlow(flow.getName());

        return dataFlow != null;
    }
}
