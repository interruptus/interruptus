package org.cad.interruptus.rest;

import com.wordnik.swagger.jersey.listing.ApiListingResourceJSON;
import com.wordnik.swagger.jersey.listing.JerseyApiDeclarationProvider;
import com.wordnik.swagger.jersey.listing.JerseyResourceListingProvider;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class ApplicationConfig extends ResourceConfig
{
    public ApplicationConfig()
    {
        packages("org.cad.interruptus.entity");
        packages("org.cad.interruptus.rest");

        register(ApiListingResourceJSON.class);
        register(JerseyApiDeclarationProvider.class);
        register(JerseyResourceListingProvider.class);
    }
}