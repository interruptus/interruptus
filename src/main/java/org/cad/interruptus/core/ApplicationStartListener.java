package org.cad.interruptus.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class ApplicationStartListener implements ApplicationListener<ContextRefreshedEvent>
{
    private static Log logger = LogFactory.getLog(ApplicationStartListener.class);

    private Kernel zookeeperKernel;

    public ApplicationStartListener(Kernel zookeeperKernel)
    {
        this.zookeeperKernel = zookeeperKernel;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        try {
            zookeeperKernel.start();
        } catch (Exception ex) {
           logger.fatal(ex);
        }
    }

}
