package org.cad.interruptus.entity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TypeProperty
{
    protected String name;
    protected String type;

    public TypeProperty()
    {
    }

    public TypeProperty(String name, String type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
