package org.cad.interruptus.entity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Flow implements Entity
{
    protected String query;
    protected String name;
    protected boolean masterOnly = true;

    public Flow()
    {
    }

    public Flow(final String name, final String query)
    {
        this(name, query, true);
    }

    public Flow(final String name, final String query, final boolean masterOnly)
    {
        this.name       = name;
        this.query      = query;
        this.masterOnly = masterOnly;
    }

    @Override
    public String getId()
    {
        return name;
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

    public void setMasterOnly(boolean masterOnly)
    {
        this.masterOnly = masterOnly;
    }

    public boolean isMasterOnly()
    {
        return masterOnly;
    }

    @Override
    public String toString()
    {
        return String.format("{name:'%s', query:'%s'}", name, query);
    }
}
