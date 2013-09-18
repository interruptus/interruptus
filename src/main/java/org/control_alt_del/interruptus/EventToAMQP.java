package org.control_alt_del.interruptus;

import com.espertech.esperio.amqp.ObjectToAMQPCollector;
import com.espertech.esperio.amqp.ObjectToAMQPCollectorContext;
import org.json.simple.JSONValue;

public class EventToAMQP implements ObjectToAMQPCollector
{
    @Override
    public void collect(ObjectToAMQPCollectorContext context)
    {
        context.getEmitter().send(JSONValue.toJSONString(context.getObject()).getBytes());
    }
}
