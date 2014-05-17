package org.cad.interruptus.repository;

import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cad.interruptus.entity.Configuration;
import org.cad.interruptus.entity.Entity;
import org.cad.interruptus.entity.Flow;

public class ConfigDiffTool
{
    final Log logger = LogFactory.getLog(getClass());
    final Configuration oldConfig;
    final Configuration newConfig;

    public ConfigDiffTool(Configuration oldConfig, Configuration newConfig)
    {
        this.oldConfig = oldConfig;
        this.newConfig = newConfig;
    }

    public Map<String, Flow> getFlowsScheduledToInsert()
    {
        final Map<String, Entity> oldMap = new HashMap<>();
        final Map<String, Entity> newMap = new HashMap<>();

        newMap.putAll(newConfig.getFlows());
        oldMap.putAll(oldConfig.getFlows());

        return computeFlowMap(new Callable<Map<String, Entity>>()
        {
            @Override
            public Map<String, Entity> call()
            {
                return computeInsertMap(oldMap, newMap);
            }
        });
    }
    
    public Map<String, Flow> getFlowsScheduledToUpdate()
    {
        final Map<String, Entity> oldMap = new HashMap<>();
        final Map<String, Entity> newMap = new HashMap<>();

        newMap.putAll(newConfig.getFlows());
        oldMap.putAll(oldConfig.getFlows());

        return computeFlowMap(new Callable<Map<String, Entity>>()
        {
            @Override
            public Map<String, Entity> call()
            {
                return computeUpdateMap(oldMap, newMap);
            }
        });
    }
    
    public Map<String, Flow> getFlowsScheduledToDelete()
    {
        final Map<String, Entity> oldMap = new HashMap<>();
        final Map<String, Entity> newMap = new HashMap<>();

        newMap.putAll(newConfig.getFlows());
        oldMap.putAll(oldConfig.getFlows());

        return computeFlowMap(new Callable<Map<String, Entity>>()
        {
            @Override
            public Map<String, Entity> call()
            {
                return computeDeleteMap(oldMap, newMap);
            }
        });
    }
    
    private Map<String, Flow> computeFlowMap(Callable<Map<String, Entity>> c)
    {
        final Map<String, Flow> map = new HashMap<>();
        
        try {
            
            final Map<String, Entity> result = c.call();
            
            if (result == null) {
                return map;
            }
            
            for (Map.Entry<String, Entity> entry : result.entrySet()) {
                map.put(entry.getKey(), (Flow) entry.getValue());
            }
            
            return map;
        } catch (Exception ex) {
            logger.error(this, ex);
        }
        
        return map;
    }

    public Map<String, Entity> computeInsertMap(final Map<String, Entity> oldMap, final Map<String, Entity> newMap)
    {
        final MapDifference<String, Entity> diff = Maps.difference(oldMap, newMap);
        final Map<String, Entity> result         = diff.entriesOnlyOnRight();

        return result;
    }
    
    public Map<String, Entity> computeDeleteMap(final Map<String, Entity> oldMap, final Map<String, Entity> newMap)
    {
        final MapDifference<String, Entity> diff = Maps.difference(oldMap, newMap);
        final Map<String, Entity> result         = diff.entriesOnlyOnLeft();

        return result;
    }

    public Map<String, Entity> computeUpdateMap(final Map<String, Entity> oldMap, final Map<String, Entity> newMap)
    {
        final MapDifference<String, Entity> diff             = Maps.difference(oldMap, newMap);
        final Map<String, ValueDifference<Entity>> differing = diff.entriesDiffering();
        final Map<String, Entity> result                     = new HashMap<>();
        
        for (Map.Entry<String, ValueDifference<Entity>> entry : differing.entrySet()) {
            final String key                    = entry.getKey();
            final ValueDifference<Entity> value = entry.getValue();

            result.put(key, value.rightValue());
        }

        return result;
    }
    
    public class Comparison
    {   
        final Map<String, Entity> onLeft;
        final Map<String, Entity> onRight;

        public Comparison(Map<String, Entity> onLeft, Map<String, Entity> onRight)
        {
            this.onLeft = onLeft;
            this.onRight = onRight;
        }
    }

}
