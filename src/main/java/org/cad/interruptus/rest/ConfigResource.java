package org.cad.interruptus.rest;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import javax.inject.Inject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import org.cad.interruptus.entity.Configuration;
import org.cad.interruptus.repository.zookeeper.ConfigurationManager;

@Component
@Path("/config")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Api(value = "/config", description = "Config information")
public class ConfigResource
{
    @Inject
    ConfigurationManager configuration;

    @GET
    @ApiOperation(value = "Get configuration", notes = "Show the corrent configuration", response = Configuration.class)
    public Configuration getConfig() throws Exception
    {
	return configuration.get();
    }
}
