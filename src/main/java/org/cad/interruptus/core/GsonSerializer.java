package org.cad.interruptus.core;

import com.google.gson.Gson;

public class GsonSerializer<E>
{
    private final Class<E> clazz;
    private final Gson gson;

    public GsonSerializer(Class<E> clazz, Gson gson)
    {
        this.clazz = clazz;
        this.gson  = gson;
    }

    public String toJson(E entity)
    {
       return gson.toJson(entity);
    }

    public E fromJson(String json)
    {
       return gson.fromJson(json, this.clazz);
    }
}