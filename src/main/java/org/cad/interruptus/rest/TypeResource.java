package org.cad.interruptus.rest;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import org.cad.interruptus.entity.Type;
import org.cad.interruptus.repository.EntityRepository;
import org.cad.interruptus.repository.TypeRepository;

@Singleton
@Path("/type")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class TypeResource extends AbstractResource<String, Type>
{
    @Inject
    private TypeRepository repository;

    @Override
    protected EntityRepository<String, Type> getRepository()
    {
        return repository;
    }
}