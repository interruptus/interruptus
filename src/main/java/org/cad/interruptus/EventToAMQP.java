package org.cad.interruptus;

import com.espertech.esperio.amqp.ObjectToAMQPCollector;
import com.espertech.esperio.amqp.ObjectToAMQPCollectorContext;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EventToAMQP implements ObjectToAMQPCollector
{
    final private static Gson gson = new GsonBuilder().create();

    @Override
    public void collect(ObjectToAMQPCollectorContext context)
    {
        context.getEmitter().send(gson.toJson(context.getObject()).getBytes());
    }
}
