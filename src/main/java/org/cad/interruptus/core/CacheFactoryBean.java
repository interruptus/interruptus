package org.cad.interruptus.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class CacheFactoryBean implements FactoryBean<Cache>, InitializingBean
{
    int lifetime = 60;

    public void setLifetime(int lifetime)
    {
        this.lifetime = lifetime;
    }

    @Override
    public Cache getObject()
    {
        if (lifetime > 0) {
            return CacheBuilder.newBuilder()
                .expireAfterWrite(lifetime, TimeUnit.SECONDS)
                .build();
        }

        return CacheBuilder.newBuilder().build();
    }

    @Override
    public Class<?> getObjectType()
    {
        return Cache.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

    @Override
    public void afterPropertiesSet()
    {

    }
}