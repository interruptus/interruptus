package org.control_alt_del.interruptus.core.esper;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.dataflow.EPDataFlowDescriptor;
import com.espertech.esper.client.dataflow.EPDataFlowInstance;
import com.espertech.esper.client.dataflow.EPDataFlowRuntime;
import java.util.ArrayList;
import java.util.List;
import org.control_alt_del.interruptus.entity.Flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("flowConfiguration")
public class FlowConfiguration
{
    @Autowired
    private EPServiceProvider epService;

    @Autowired
    private EPAdministrator epAdministrator;

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

    public Flow create(Flow flow)
    {
        epAdministrator.createEPL(flow.getQuery(), flow.getName());

        EPRuntime epRuntime             = epService.getEPRuntime();
        EPDataFlowRuntime flowRuntime   = epRuntime.getDataFlowRuntime();
        EPDataFlowInstance instance     = flowRuntime.instantiate(flow.getName());

        instance.start();

        return flow;
    }

    public Boolean destroy(Flow flow)
    {
        epService.getEPRuntime().getDataFlowRuntime().instantiate(flow.getName()).cancel();

        return true;
    }

    public Boolean exists(Flow flow)
    {
        return epService.getEPRuntime()
            .getDataFlowRuntime()
            .getDataFlow(flow.getName()) != null;
    }
}
