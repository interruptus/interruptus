package org.cad.interruptus.features;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javax.inject.Inject;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = {"classpath*:/cucumber.xml"})
public abstract class BaseIntegration
{
    @Inject
    protected CuratorFramework curator;

    protected String getBaseUrl()
    {
        return "http://localhost:8080/";
    }

    protected String getUrl(String path)
    {
        return getBaseUrl() + path;
    }

    protected String getResourceUrl(String path)
    {
        return getUrl("api/" + path);
    }

    public ClientResponse getHtml(final String path) throws Throwable
    {
        final String url                = getUrl(path);
        final Client client             = Client.create();
        final WebResource webResource   = client.resource(url);
        final ClientResponse response   = webResource.accept("text/html")
            .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException(String.format("[%s] - Failed to get'%s'", response.getStatus(), url));
        }

        return response;
    }

    public ClientResponse postResource(final String path, final String body) throws Throwable
    {
        final Client client             = Client.create();
        final String url                = getResourceUrl(path);
        final WebResource webResource   = client.resource(url);
        final ClientResponse response   = webResource
            .type("application/json")
            .post(ClientResponse.class, body);

        if (response.getStatus() != 200) {
            throw new RuntimeException(String.format("[%s] - Failed to post to '%s'", response.getStatus(), url));
        }

        return response;
    }

    public ClientResponse getResource(final String path) throws Throwable
    {
        final Client client           = Client.create();
        final String url              = getResourceUrl(path);
        final WebResource webResource = client.resource(url);
        final ClientResponse response = webResource
            .accept("application/json")
            .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException(String.format("[%s] - Failed to get from '%s'", response.getStatus(), url));
        }

        return response;
    }

    public ClientResponse deleteResource(final String path) throws Throwable
    {
        final Client client           = Client.create();
        final String url              = getResourceUrl(path);
        final WebResource webResource = client.resource(url);
        final ClientResponse response = webResource
            .accept("application/json")
            .delete(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException(String.format("[%s] - Failed to get from '%s'", response.getStatus(), url));
        }

        return response;
    }
}
