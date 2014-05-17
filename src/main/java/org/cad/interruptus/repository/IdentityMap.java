package org.cad.interruptus.repository;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;
import org.cad.interruptus.entity.Configuration;
import org.cad.interruptus.entity.Entity;

public class IdentityMap
{
    Set<Entity> entitySet;

    public Set<Entity> getIdentitySet()
    {
        return entitySet;
    }
    
    public Sets.SetView<Entity> applyConfiguration(final Configuration config)
    {
        final Set<Entity> oldSet  = getIdentitySet();
        final Set<Entity> newSet  = createIdentitySet(config);

        this.entitySet = newSet;
        
        return Sets.difference(oldSet, newSet);
    }

    public Set<Entity> createIdentitySet(final Configuration config)
    {
        final Set<Entity> set = new HashSet<>();

        set.addAll(config.getTypes().values());
        set.addAll(config.getFlows().values());
        set.addAll(config.getStatements().values());

        return set;
    }
}
