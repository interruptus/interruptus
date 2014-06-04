package org.cad.interruptus;

import com.espertech.esperio.amqp.AMQPToObjectCollector;
import com.espertech.esperio.amqp.AMQPToObjectCollectorContext;
import org.cad.interruptus.service.MessageCollectorService;

public class AMQPJsonToMap implements AMQPToObjectCollector
{
    private static MessageCollectorService collectorService;

    /**
     * @TODO -- Make it non static !!
     *          http://esper.codehaus.org/esperio-4.9.0/doc/reference/en-US/html/adapter_amqp.html#amqp-source
     *          esper documentation says that "collector" is a class or instance, 
     *          but does not show how to provide a collector instance..
     *
     *          Investigating alternatives...
     *
     * @param collectorService
     */
    public static void setMessageCollectorService(final MessageCollectorService collectorService)
    {
        AMQPJsonToMap.collectorService = collectorService;
    }

    @Override
    public void collect(final AMQPToObjectCollectorContext context)
    {
        collectorService.collect(context);
    }
}
