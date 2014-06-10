package org.cad.interruptus.core.esper;

import java.util.List;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.dataflow.EPDataFlowDescriptor;
import com.espertech.esper.client.dataflow.EPDataFlowInstance;
import com.espertech.esper.client.dataflow.EPDataFlowRuntime;
import com.espertech.esper.client.dataflow.EPDataFlowState;
import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cad.interruptus.entity.Flow;

public class FlowConfiguration implements EsperConfiguration<Flow>
{
    final EPServiceProvider epService;
    final EPAdministrator epAdministrator;
    final Log logger = LogFactory.getLog(getClass());

    public FlowConfiguration(final EPServiceProvider epService, final EPAdministrator epAdministrator)
    {
        this.epService       = epService;
        this.epAdministrator = epAdministrator;
    }

    @Override
    public List<String> list()
    {
        final EPDataFlowRuntime flowRuntime = epService.getEPRuntime().getDataFlowRuntime();
        final String[] dataFlowsNames       = flowRuntime.getDataFlows();

        return Lists.newArrayList(dataFlowsNames);
    }

    @Override
    public void save(final Flow flow)
    {
        final String name      = flow.getName();
        final String query     = flow.getQuery();
        final EPStatement sttm = epAdministrator.getStatement(name);

        if (sttm != null) {
            logger.info("Existing flow detected for : " + name);
            remove(name);
        }
        
        logger.info("Saving flow : " + name);

        epAdministrator.createEPL(query, name);
    }

    @Override
    public Boolean start(final String name)
    {
        final EPRuntime epRuntime           = epService.getEPRuntime();
        final EPDataFlowRuntime flowRuntime = epRuntime.getDataFlowRuntime();
        EPDataFlowInstance instance         = flowRuntime.getSavedInstance(name);

        if (instance == null) {
            instance = flowRuntime.instantiate(name);

            flowRuntime.saveInstance(name, instance);
        }

        if (instance.getState() == EPDataFlowState.RUNNING) {
            return true;
        }

        logger.info("Starting flow : " + name);
        instance.start();

        return true;
    }

    @Override
    public Boolean stop(final String name)
    {
        final EPRuntime epRuntime           = epService.getEPRuntime();
        final EPDataFlowRuntime flowRuntime = epRuntime.getDataFlowRuntime();
        final EPDataFlowInstance instance   = flowRuntime.getSavedInstance(name);

        if (instance != null) {
            logger.info("Stoping flow : " + name);
            instance.cancel();
        }

        return true;
    }

    public EPDataFlowState getFlowState(final String name)
    {
        final EPRuntime epRuntime           = epService.getEPRuntime();
        final EPDataFlowRuntime flowRuntime = epRuntime.getDataFlowRuntime();
        final EPDataFlowInstance instance   = flowRuntime.getSavedInstance(name);

        if (instance == null) {
            return null;
        }

        return instance.getState();
    }

    @Override
    public Boolean remove(final String name)
    {
        final EPRuntime epRuntime           = epService.getEPRuntime();
        final EPDataFlowRuntime flowRuntime = epRuntime.getDataFlowRuntime();
        final EPDataFlowInstance instance   = flowRuntime.getSavedInstance(name);
        final EPStatement sttm              = epAdministrator.getStatement(name);

        if (instance != null) {
            instance.cancel();
            flowRuntime.removeSavedInstance(name);
        }

        if (sttm == null) {
            return false;
        }

        logger.info("Removing flow : " + name);
        sttm.destroy();

        return true;
    }
}
