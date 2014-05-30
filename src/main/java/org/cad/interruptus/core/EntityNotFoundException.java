package org.cad.interruptus.core;

public class EntityNotFoundException extends RuntimeException
{
    public EntityNotFoundException(final String identifier)
    {
        super(String.format("Entity identified by '%s' not Found", identifier));
    }
    
    public EntityNotFoundException(final Class clazz, final String identifier)
    {
        super(String.format("%s identified by '%s' not Found", clazz.getSimpleName(), identifier));
    }
}