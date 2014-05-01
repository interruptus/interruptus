package org.cad.interruptus.entity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Fabio B. Silva <fabio.bat.silva@gmail.com>
 */
@XmlRootElement
public class Flow
{
    protected String query;
    protected String name;

    public Flow()
    {
    }

    public Flow(String name, String query)
    {
        this.name = name;
        this.query = query;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
