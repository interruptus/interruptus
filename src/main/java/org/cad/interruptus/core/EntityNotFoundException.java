package org.cad.interruptus.core;

public class EntityNotFoundException extends RuntimeException
{
    public EntityNotFoundException(String identifier)
    {
        super(String.format("Entity identified by '%s' not Found", identifier));
    }
}