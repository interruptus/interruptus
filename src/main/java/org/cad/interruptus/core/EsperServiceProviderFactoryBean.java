package org.cad.interruptus.core;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esperio.amqp.AMQPSource;
import org.springframework.beans.factory.InitializingBean;

public class EsperServiceProviderFactoryBean implements FactoryBean<EPServiceProvider>, InitializingBean, DisposableBean 
{
    EPServiceProvider serviceProvider;

    @Override
    public EPServiceProvider getObject() 
    {
        if (serviceProvider != null) {
            return serviceProvider;
        }

        final Configuration config       = createConfiguration();
        final EPServiceProvider service = createServiceProvider(config);

        service.initialize();

        serviceProvider = service;

        return service;
    }

    @Override
    public Class<?> getObjectType() 
    {
        return EPServiceProvider.class;
    }

    @Override
    public boolean isSingleton() 
    {
        return true;
    }

    @Override
    public void destroy()
    {
        if (serviceProvider == null) {
            return;
        }

        serviceProvider.destroy();
    }

    @Override
    public void afterPropertiesSet()
    {

    }

    protected Configuration createConfiguration()
    {
        final Configuration config = new Configuration();

        config.addImport(AMQPSource.class.getPackage().getName() + ".*");

        return config;
    }

    protected EPServiceProvider createServiceProvider(final Configuration config)
    {
        return EPServiceProviderManager.getDefaultProvider(config);
    }
}
