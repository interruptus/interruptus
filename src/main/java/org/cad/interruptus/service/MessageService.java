package org.cad.interruptus.service;

import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cad.interruptus.Message;

public class MessageService
{
    final Log logger = LogFactory.getLog(getClass());
    final Gson gson;

    public MessageService(final Gson gson)
    {
        this.gson = gson;
    }

    public List<Message> extractMessages(final byte[] input) throws Exception
    {
        // Max UDP packet size without fragmentation = 1500 bytes (MTU on ethernet without jumbo frames) - IP header (60 bytes max) - UDP header (8 bytes) = 1432 bytes
        // Ideally if we're trying to squeeze every little bit of performance, we should require clients to do path MTU discovery and encode 
        // pre-compression length in first 4 bytes of payload. We would also enfore that that UDP packet size after compression be < 1432
        // This would allow us to size buffers appropriately as well as have the lowest possible amount of TCP/IP header overhead.
        // See http://tools.ietf.org/html/rfc5405#section-3.2
        // http://stackoverflow.com/questions/973439/how-to-set-the-dont-fragment-df-flag-on-a-socket
        // Should do some testing on alternate compression algorithms to see what gives us best bang for the buck. Gut feeling is that zlib reduces by > 50%
        // as json encoded metrics have lots of repetition.

        final List<Message> list        = new ArrayList<>(); 
        final String json               = getJsonString(input);
        final StringTokenizer tokenizer = new StringTokenizer(json, "\n");

        while (tokenizer.hasMoreTokens()) {
            final String jsonLine = tokenizer.nextToken();
            final Message message = gson.fromJson(jsonLine, Message.class);

            list.add(message);
        }

        return list;
    }

    private String getJsonString(byte[] input) throws DataFormatException, UnsupportedEncodingException
    {
	if ((char) input[0] == '{') { // Very cheezy ...
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
}
