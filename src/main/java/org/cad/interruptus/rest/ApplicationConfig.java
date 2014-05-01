package org.cad.interruptus.rest;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class ApplicationConfig extends ResourceConfig
{
    public ApplicationConfig()
    {
        packages("org.cad.interruptus.entity");
        packages("org.cad.interruptus.rest");
    }
}