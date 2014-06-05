package org.cad.interruptus.rest;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cad.interruptus.service.InventoryService;

@Singleton
@Path("/inventory")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Api(value = "/inventory", description = "Auto-inventory a endpoint could then serve queries to known events/values")
public class InventoryResource
{
    final Log logger = LogFactory.getLog(getClass());
    
    @Inject
    InventoryService service;

    @GET
    @Path("/{typeName}/{path:.*}")
    @ApiOperation(
        value = "List known inventory values for a given event type",
        notes = "List known inventory values for a given event type",
        response = String.class,
        responseContainer = "List"
    )
    public Set<String> list(
        @ApiParam(value = "Type name to lookup for", required = true) @PathParam("typeName") final String typeName,
        @ApiParam(value = "Path hierarchy", required = false) @PathParam("path") final String path
    ) throws Exception
    {
        return service.getTypeHierarchyInventory(typeName, path);
    }
}