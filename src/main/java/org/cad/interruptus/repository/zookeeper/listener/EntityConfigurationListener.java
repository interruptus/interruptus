package org.cad.interruptus.repository.zookeeper.listener;

import org.cad.interruptus.entity.Entity;

public interface EntityConfigurationListener<E extends Entity>
{
    public void onInsert(E newEntity);
    public void onDelete(E oldEntity);
    public void onUpdate(E newEntity);
}
