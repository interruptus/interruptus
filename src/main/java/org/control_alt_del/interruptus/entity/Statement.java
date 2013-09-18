package org.control_alt_del.interruptus.entity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Fabio B. Silva <fabio.bat.silva@gmail.com>
 */
@XmlRootElement
public class Statement
{
    protected String query;
    protected String name;
    protected boolean debug;

    public Statement()
    {

    }

    public Statement(String name, String query, boolean debug)
    {
        this.name  = name;
        this.query = query;
        this.debug = debug;
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

}
