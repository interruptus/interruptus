package org.control_alt_del.interruptus;

import com.espertech.esper.client.ConfigurationOperations;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.dataflow.interfaces.EPDataFlowEmitter;
import com.espertech.esper.event.map.MapEventBean;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;
import org.xerial.snappy.Snappy;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.espertech.esperio.amqp.AMQPToObjectCollector;
import com.espertech.esperio.amqp.AMQPToObjectCollectorContext;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class AMQPJsonToMap implements AMQPToObjectCollector
{
    //@TODO - make it configurable
    final private static Log log            = LogFactory.getLog(AMQPJsonToMap.class);
    final private static JSONParser parser  = new JSONParser();
    private static EPServiceProvider epService;

    private ConfigurationOperations config;

    /**
     * @TODO -- Make it non static !!
     *          http://esper.codehaus.org/esperio-4.9.0/doc/reference/en-US/html/adapter_amqp.html#amqp-source
     *          esper documentation says that "collector" is a class or instance, 
     *          but does not show how to provide a collector instance..
     *
     *          Investigating alternatives...
     *
     * @param epService
     */
    public static void setEPServiceProvider(EPServiceProvider epService)
    {
        AMQPJsonToMap.epService = epService;
    }

    public AMQPJsonToMap()
    {
        config  = epService.getEPAdministrator().getConfiguration();
    }

    @Override
    public void collect(AMQPToObjectCollectorContext context)
    {
        byte[] input        = context.getBytes();

        String json  = getJsonString(input);
       
        if (json == null) {
            return;
        }

        String delimiter            = "\n";
        EPDataFlowEmitter emmiter   = context.getEmitter();
        StringTokenizer tokenizer   = new StringTokenizer(json, delimiter);

        while (tokenizer.hasMoreTokens()) {
            try {
                String newline  = tokenizer.nextToken();
                EventBean event = parseLine(newline);
                emmiter.submit(event);
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private String getJsonString(byte[] input)
    {
        // Super sophisticated compression algorithm detection: If string starts with 's', input is compressed. 
	if ((char) input[0] == 's') {
	        try {
        	    return Snappy.uncompress(Arrays.copyOfRange(input, 1,input.length)).toString();
	        } catch (Exception e) {
        	    log.error(e.getMessage(), e);
	            return null;
        	}
	} else {
            return new String(input);
	}

    }

    private EventBean parseLine(String json) throws ParseException
    {
        JSONObject values               = (JSONObject) parser.parse(json);
        String eventTypeName            = (String) values.get("eventType");
        EventType eventType             = config.getEventType(eventTypeName);
        EventBean eventBean             = new MapEventBean(values, eventType);
        return eventBean;
    }
}
