package org.cad.interruptus;

import com.espertech.esper.client.ConfigurationOperations;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.dataflow.interfaces.EPDataFlowEmitter;
import com.espertech.esper.event.map.MapEventBean;
import java.util.StringTokenizer;

import java.util.zip.Inflater;

import com.google.gson.Gson;

import com.espertech.esperio.amqp.AMQPToObjectCollector;
import com.espertech.esperio.amqp.AMQPToObjectCollectorContext;
import com.google.gson.GsonBuilder;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class AMQPJsonToMap implements AMQPToObjectCollector
{
    //@TODO - make it configurable
    final private static Log log            = LogFactory.getLog(AMQPJsonToMap.class);
    final private static Gson parser        = new GsonBuilder().create();

    private static EPServiceProvider epService;

    final private ConfigurationOperations config;

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
        // Max UDP packet size without fragmentation = 1500 bytes (MTU on ethernet without jumbo frames) - IP header (60 bytes max) - UDP header (8 bytes) = 1432 bytes
        // Ideally if we're trying to squeeze every little bit of performance, we should require clients to do path MTU discovery and encode 
        // pre-compression length in first 4 bytes of payload. We would also enfore that that UDP packet size after compression be < 1432
        // This would allow us to size buffers appropriately as well as have the lowest possible amount of TCP/IP header overhead.
        // See http://tools.ietf.org/html/rfc5405#section-3.2
        // http://stackoverflow.com/questions/973439/how-to-set-the-dont-fragment-df-flag-on-a-socket
        // Should do some testing on alternate compression algorithms to see what gives us best bang for the buck. Gut feeling is that zlib reduces by > 50%
        // as json encoded metrics have lots of repetition.
        byte[] input = context.getBytes();
        String json  = null;

        try {
            json = getJsonString(input);
        } catch (DataFormatException | UnsupportedEncodingException ex) {
            log.error(this, ex);
        }

        if (json == null) {
            return;
        }

        String delimiter            = "\n";
        EPDataFlowEmitter emmiter   = context.getEmitter();
        StringTokenizer tokenizer   = new StringTokenizer(json, delimiter);

        log.debug("json: " +json);

        while (tokenizer.hasMoreTokens()) {
            String newline  = tokenizer.nextToken();
            EventBean event = parseLine(newline);
            emmiter.submit(event);
        }
    }

    private String getJsonString(byte[] input) throws DataFormatException, UnsupportedEncodingException
    {
	if ((char) input[0] == '{') { // Very cheezy....
            return new String(input);
        }

        // Max UDP packet size, worst case scenario until we get client to support encoding payload uncmopressed length.
        final byte[] restored       = new byte[65507];  
        final Inflater decompresser = new Inflater();

        decompresser.setInput(input,0,input.length);
        int resultLength = decompresser.inflate(restored);

        decompresser.end();

        return new String(restored, 0, resultLength, "UTF-8");
    }

    private EventBean parseLine(String json)
    {
        Map<String,Object> values = parser.fromJson(json, HashMap.class);
        String eventTypeName      = (String) values.get("event_type");
        EventType eventType       = config.getEventType(eventTypeName);
        EventBean eventBean       = new MapEventBean(values, eventType);

        return eventBean;
    }
}
