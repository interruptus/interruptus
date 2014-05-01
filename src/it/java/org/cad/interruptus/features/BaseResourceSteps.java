package org.cad.interruptus.features;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public abstract class BaseResourceSteps
{
    public ClientResponse post(final String name, final String body) throws Throwable
    {
        final Client client             = Client.create();
        final String url                = "http://localhost:8080/api/" + name;
        final WebResource webResource   = client.resource(url);
        ClientResponse response         = webResource
            .type("application/json")
            .post(ClientResponse.class, body);

        if (response.getStatus() != 200) {
            throw new RuntimeException(String.format("[%s] - Failed to post to '%s'", response.getStatus(), url));
        }

        return response;
    }

    public ClientResponse get(final String path) throws Throwable
    {
        final Client client           = Client.create();
        final String url                = "http://localhost:8080/api/" + path;
        final WebResource webResource = client.resource(url);
        final ClientResponse response = webResource
            .accept("application/json")
            .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException(String.format("[%s] - Failed to get from '%s'", response.getStatus(), url));
        }

        return response;
    }
}
