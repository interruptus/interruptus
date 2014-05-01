package org.cad.interruptus.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class ApplicationStopListener implements ApplicationListener<ContextClosedEvent>
{
    private static Log logger = LogFactory.getLog(ApplicationStopListener.class);
    private Kernel zookeeperKernel;

    public ApplicationStopListener(Kernel zookeeperKernel)
    {
        this.zookeeperKernel = zookeeperKernel;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event)
    {
        logger.fatal(event);

        try {
            zookeeperKernel.stop();
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }
}
