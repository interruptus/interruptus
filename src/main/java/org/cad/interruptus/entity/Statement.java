package org.cad.interruptus.entity;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@ApiModel(value = "Statement", description = "Statement resource representation")
public class Statement implements RunnableEntity
{
    @ApiModelProperty(value = "Statement epl query", required = true)
    protected String query;
    
    @ApiModelProperty(value = "Statement unique name", required = true)
    protected String name;
    
    @ApiModelProperty(value = "Statement debug flag", required = false)
    protected boolean debug;

    @ApiModelProperty(value = "Statement runs only on master node or not", required = false)
    protected boolean masterOnly = false;

    @ApiModelProperty(value = "Whether or not the statement is running in esper", required = false)
    protected boolean started = false;
    
    public Statement()
    {

    }

    public Statement(final String name, final String query, final boolean masterOnly)
    {
        this.name       = name;
        this.query      = query;
        this.masterOnly = masterOnly;
    }

    @Override
    @XmlTransient
    public String getId()
    {
        return name;
    }

    @Override
    public boolean isRunning()
    {
        return this.started;
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

    public boolean isStarted()
    {
        return started;
    }

    public void setStarted(boolean started)
    {
        this.started = started;
    }
    
    public void setMasterOnly(boolean masterOnly)
    {
        this.masterOnly = masterOnly;
    }

    @Override
    public boolean isMasterOnly()
    {
        return masterOnly;
    }

    @Override
    public String toString()
    {
        return String.format("{name:'%s', query:'%s', masterOnly:%s, started:%s}", name, query, masterOnly, started);
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash     = 79 * hash + Objects.hashCode(this.query);
        hash     = 79 * hash + Objects.hashCode(this.name);
        hash     = 79 * hash + (this.masterOnly ? 1 : 0);
        hash     = 79 * hash + (this.started ? 1 : 0);
        hash     = 79 * hash + (this.debug ? 1 : 0);

        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }

        if ( ! getClass().equals(obj.getClass())) {
            return false;
        }

        final Statement other = (Statement) obj;

        if ( ! Objects.equals(this.name, other.name)) {
            return false;
        }

        if ( ! Objects.equals(this.query, other.query)) {
            return false;
        }

        return (this.debug == other.debug) && (this.started == other.started);
    }
}
