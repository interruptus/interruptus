package org.cad.interruptus.entity;

import java.util.HashMap;
import java.util.Map;
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

    @Override
    public String toString()
    {
        return String.format("{types:%s, flows:%s, statements:%s}", types, flows, statements);
    }
}
