package org.cad.interruptus.repository;

import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import org.cad.interruptus.entity.Configuration;
import org.cad.interruptus.entity.Entity;

public class ConfigDiffTool
{
    final Configuration oldConfig;
    final Configuration newConfig;
    final Map<Class, MapDifference<String, Entity>> diffMap = new HashMap<>();

    public ConfigDiffTool(final Configuration oldConfig, final Configuration newConfig)
    {
        this.oldConfig = oldConfig;
        this.newConfig = newConfig;
    }
    
    public void compute(final Class<? extends Entity> clazz)
    {
        diffMap.put(clazz, Maps.difference(oldConfig.mapOf(clazz), newConfig.mapOf(clazz)));
    }
    
    public MapDifference<String, Entity> getDiff(final Class<? extends Entity> clazz)
    {
        if ( ! diffMap.containsKey(clazz)) {
            compute(clazz);
        }

        return diffMap.get(clazz);
    }

    public Map<String, Entity> computeInsertMap(final Class<? extends Entity> clazz)
    {
        final MapDifference<String, Entity> diff = getDiff(clazz);
        final Map<String, Entity> result         = diff.entriesOnlyOnRight();

        return result;
    }
    
    public Map<String, Entity> computeDeleteMap(final Class<? extends Entity> clazz)
    {
        final MapDifference<String, Entity> diff = getDiff(clazz);
        final Map<String, Entity> result         = diff.entriesOnlyOnLeft();

        return result;
    }

    public Map<String, Entity> computeUpdateMap(final Class<? extends Entity> clazz)
    {
        final Map<String, Entity> result                     = new HashMap<>();
        final MapDifference<String, Entity> diff             = getDiff(clazz);
        final Map<String, ValueDifference<Entity>> differing = diff.entriesDiffering();

        for (final Map.Entry<String, ValueDifference<Entity>> entry : differing.entrySet()) {
            final String key                    = entry.getKey();
            final ValueDifference<Entity> value = entry.getValue();

            result.put(key, value.rightValue());
        }

        return result;
    }
}
