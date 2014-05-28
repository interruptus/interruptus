package org.cad.interruptus.entity;

public interface RunnableEntity extends Entity
{
    public boolean isRunning();
    public boolean isMasterOnly();
}
