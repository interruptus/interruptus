package org.control_alt_del.interruptus.core.esper;

import java.util.List;
import java.util.ArrayList;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.dataflow.EPDataFlowDescriptor;
import com.espertech.esper.client.dataflow.EPDataFlowInstance;
import com.espertech.esper.client.dataflow.EPDataFlowRuntime;
import org.control_alt_del.interruptus.entity.Flow;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service("flowConfiguration")
public class FlowConfiguration implements EsperConfiguration<Flow>
{
    @Autowired
    private EPServiceProvider epService;

    @Autowired
    private EPAdministrator epAdministrator;

    @Override
    public List<Flow> list()
    {
        EPDataFlowRuntime flowRuntime = epService.getEPRuntime().getDataFlowRuntime();
        String[] dataFlowsNames       = flowRuntime.getDataFlows();
        List<Flow> list               = new ArrayList<Flow>();

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
