package org.cad.interruptus.entity;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@ApiModel(value = "Statement", description = "Statement resource representation")
public class Statement implements Entity
{
    @ApiModelProperty(value = "Statement epl query", required = true)
    protected String query;
    
    @ApiModelProperty(value = "Statement unique name", required = true)
    protected String name;
    
    @ApiModelProperty(value = "Statement debug flag", required = false)
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

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash     = 79 * hash + Objects.hashCode(this.query);
        hash     = 79 * hash + Objects.hashCode(this.name);
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

        return (this.debug == other.debug);
    }
}
