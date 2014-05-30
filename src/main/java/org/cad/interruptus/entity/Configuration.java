package org.cad.interruptus.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Configuration
{
    Map<String, Type> types = new HashMap<>();
    Map<String, Flow> flows = new HashMap<>();
    Map<String, Statement> statements = new HashMap<>();

    public Configuration()
    {
    }

    public Configuration(final Map<String, Type> types, final Map<String, Flow> flows, final Map<String, Statement> statements)
    {
        this.types      = types;
        this.flows      = flows;
        this.statements = statements;
    }

    public Map<String, Type> getTypes()
    {
        return types;
    }

    public void setTypes(Map<String, Type> types)
    {
        this.types = types;
    }

    public Map<String, Flow> getFlows()
    {
        return flows;
    }

    public void setFlows(Map<String, Flow> flows)
    {
        this.flows = flows;
    }

    public Map<String, Statement> getStatements()
    {
        return statements;
    }

    public void setStatements(Map<String, Statement> statements)
    {
        this.statements = statements;
    }

    public Map<String, Entity> mapOf(Class<? extends Entity> clazz)
    {
        if (Statement.class.equals(clazz)) {
            return new HashMap<String, Entity>(this.statements);
        }

        if (Flow.class.equals(clazz)) {
            return new HashMap<String, Entity>(this.flows);
        }

        if (Type.class.equals(clazz)) {
            return new HashMap<String, Entity>(this.types);
        }

        throw new RuntimeException("Unknow class : " + clazz);
    }

    public void put(final Entity entity)
    {
        if (entity instanceof Statement) {
            this.statements.put(entity.getId(), (Statement) entity);

            return;
        }

        if (entity instanceof Flow) {
            this.flows.put(entity.getId(), (Flow) entity);

            return;
        }

        if (entity instanceof Type) {
            this.types.put(entity.getId(), (Type) entity);

            return;
        }

        throw new RuntimeException("Unknow configuration entity : " + entity.getClass());
    }
    
    public void remove(final Class<? extends Entity> clazz, final String id)
    {
        if (Statement.class.equals(clazz)) {
            this.statements.remove(id);

            return;
        }

        if (Flow.class.equals(clazz)) {
            this.flows.remove(id);

            return;
        }

        if (Type.class.equals(clazz)) {
            this.types.remove(id);

            return;
        }

        throw new RuntimeException("Unknow configuration entity : " + clazz);
    }

    @Override
    public String toString()
    {
        return String.format("{types:%s, flows:%s, statements:%s}", types, flows, statements);
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash    = 67 * hash + Objects.hashCode(this.types);
        hash    = 67 * hash + Objects.hashCode(this.flows);
        hash    = 67 * hash + Objects.hashCode(this.statements);

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

        final Configuration other = (Configuration) obj;

        if ( ! Objects.equals(this.types, other.types)) {
            return false;
        }

        if (!Objects.equals(this.flows, other.flows)) {
            return false;
        }

        return Objects.equals(this.statements, other.statements);
    }
}
