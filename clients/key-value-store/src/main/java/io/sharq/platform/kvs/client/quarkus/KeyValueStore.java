package io.sharq.platform.kvs.client.quarkus;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * A JAX-RS interface.  An implementation of this interface must be provided.
 */
@Path("/apis/keyvaluestore/v1")
@RegisterRestClient(configKey = "key-value-store-client")
public interface KeyValueStore {


    /**
     * @param storeName
     * @param key
     * @return value
     */
    @Path("/{storeName}")
    @GET
    @Produces({"application/json", "application/x-value-type", "text/plain"})
    InputStream getValue(@PathParam("storeName") String storeName, @QueryParam("key") String key);

    /**
     * @param storeName
     * @param key
     * @param data
     */
    @Path("/{storeName}")
    @POST
    @Consumes({"application/json", "application/x-value-type", "text/plain"})
    void setValue(@PathParam("storeName") String storeName, @QueryParam("key") String key, InputStream data);

    /**
     * @param storeName
     * @param key
     */
    @Path("/{storeName}")
    @DELETE
    @Produces({"application/json", "application/x-value-type", "text/plain"})
    void delete(@PathParam("storeName") String storeName, @QueryParam("key") String key);

}
