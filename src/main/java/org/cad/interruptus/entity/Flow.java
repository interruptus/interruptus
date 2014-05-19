package org.cad.interruptus.entity;

import java.util.Objects;
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

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash     = 97 * hash + Objects.hashCode(this.query);
        hash     = 97 * hash + Objects.hashCode(this.name);
        hash     = 97 * hash + (this.masterOnly ? 1 : 0);

        return hash;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null) {
            return false;
        }

        if ( ! getClass().equals(obj.getClass())) {
            return false;
        }

        final Flow other = (Flow) obj;

        if ( ! Objects.equals(this.name, other.name)) {
            return false;
        }

        if ( ! Objects.equals(this.query, other.query)) {
            return false;
        }

        return (this.masterOnly == other.masterOnly);
    }
}
