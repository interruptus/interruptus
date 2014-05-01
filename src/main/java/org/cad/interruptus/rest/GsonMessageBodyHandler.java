package org.cad.interruptus.rest;


import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;

@Provider
@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonMessageBodyHandler implements MessageBodyWriter<Object>, MessageBodyReader<Object>
{
    private Gson gson = new Gson();

    @Inject
    public GsonMessageBodyHandler(Gson gson)
    {
        this.gson = gson;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] antns, MediaType mt)
    {
        return true;
    }

    @Override
    public long getSize(Object t, Class<?> type, Type genericType, Annotation[] antns, MediaType mt)
    {
        return 0;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] antns, MediaType mt)
    {
        return true;
    }

    @Override
    public void writeTo(Object object, Class<?> type, Type genericType, Annotation[] antns, MediaType mt, MultivaluedMap<String, Object> mm, OutputStream out) throws IOException, WebApplicationException
    {
        OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8.displayName());
        Type jsonType = type.equals(genericType) ? type : genericType;

        try {
            gson.toJson(object, jsonType, writer);
        } finally {
            writer.close();
        }
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] antns, MediaType mt, MultivaluedMap<String, String> mm, InputStream in) throws IOException, WebApplicationException
    {
        InputStreamReader streamReader = new InputStreamReader(in, StandardCharsets.UTF_8.displayName());
        Type jsonType = type.equals(genericType) ? type : genericType;

        try {
            return gson.fromJson(streamReader, jsonType);
        } finally {
            streamReader.close();
        }
    }
}
