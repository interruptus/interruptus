package org.cad.interruptus.core;

public class EntityNotFoundException extends RuntimeException
{
    public EntityNotFoundException(final String identifier)
    {
        super(String.format("Entity identified by '%s' not found.", identifier));
    }

    public EntityNotFoundException(final Class clazz, final String identifier)
    {
        super(String.format("%s identified by '%s' not found.", clazz.getSimpleName(), identifier));
    }
}
