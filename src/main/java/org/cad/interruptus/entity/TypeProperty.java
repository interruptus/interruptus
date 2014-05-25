package org.cad.interruptus.entity;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@ApiModel(value = "TypeProperty", description = "TypeProperty resource representation")
public class TypeProperty
{
    @ApiModelProperty(value = "Property name", required = true)
    protected String name;

    @ApiModelProperty(value = "Property type (string/integer/long/etc..)", required = true)
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

    @Override
    public String toString()
    {
        return String.format("{name:'%s', type:'%s'}", name, type);
    }
    
    @Override
    public int hashCode()
    {
        int hash = 3;
        hash     = 97 * hash + Objects.hashCode(this.type);
        hash     = 97 * hash + Objects.hashCode(this.name);

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

        final TypeProperty other = (TypeProperty) obj;

        if ( ! Objects.equals(this.name, other.name)) {
            return false;
        }

        return Objects.equals(this.type, other.type);
    }
}
