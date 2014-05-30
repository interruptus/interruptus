package org.cad.interruptus;

import java.util.Map;

public class Message
{
    String type;
    Map<String, Object> body;

    public String getType()
    {
        return type;
    }

    public void setBody(String type)
    {
        this.type = type;
    }

    public Map<String, Object> getBody()
    {
        return body;
    }

    public void setBody(Map<String, Object> body)
    {
        this.body = body;
    }
}
