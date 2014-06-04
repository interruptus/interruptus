package org.cad.interruptus.service;

import com.espertech.esper.client.ConfigurationOperations;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.dataflow.interfaces.EPDataFlowEmitter;
import com.espertech.esper.event.map.MapEventBean;
import com.espertech.esperio.amqp.AMQPToObjectCollectorContext;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cad.interruptus.Message;

public class MessageCollectorService
{
    final Log logger = LogFactory.getLog(getClass());
    final ConfigurationOperations esperConfig;
    final InventoryService inventoryService;
    final MessageService messageService;

    public MessageCollectorService(final ConfigurationOperations esperConfig, final MessageService messageService, final InventoryService inventoryService)
    {
        this.esperConfig      = esperConfig;
        this.messageService   = messageService;
        this.inventoryService = inventoryService;
    }

    public void collect(final AMQPToObjectCollectorContext context)
    {
        final byte[] input              = context.getBytes();
        final EPDataFlowEmitter emmiter = context.getEmitter();
        final List<Message> list        = this.extractMessages(input);

        for (final Message message : list) {
            final EventBean event = getEventBean(message);

            emmiter.submit(event);
            inventoryService.collect(message);
        }
    }

    private List<Message> extractMessages(final byte[] input)
    {
        try {
            return messageService.extractMessages(input);
        } catch (final Exception ex) {
            logger.error(this, ex);
            return Collections.EMPTY_LIST;
        }
    }

    private EventBean getEventBean(final Message message)
    {
        final EventType eventType = esperConfig.getEventType(message.getType());
        final EventBean eventBean = new MapEventBean(message.getBody(), eventType);

        return eventBean;
    }
}
