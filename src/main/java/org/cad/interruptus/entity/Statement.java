package org.cad.interruptus.entity;

import com.google.common.hash.Hashing;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Statement implements Entity
{
    protected String query;
    protected String name;
    protected boolean debug;

    public Statement()
    {

    }

    public Statement(String name, String query, boolean debug)
    {
        this.name = name;
        this.query = query;
        this.debug = debug;
    }

    @Override
    public String getId()
    {
        return name;
    }
    
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public boolean getDebug()
    {
        return debug;
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    @Override
    public String toString()
    {
        return String.format("{name:'%s', query:'%s'}", name, query);
    }
}
