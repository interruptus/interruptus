package org.cad.interruptus.repository.zookeeper.listener;

import org.cad.interruptus.entity.Entity;

public interface EntityConfigurationListener<E extends Entity>
{
    public void onSave(E e);
    public void onDelete(E e);
}
