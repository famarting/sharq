package io.sharq.platform.kvs;

import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * A JAX-RS interface.  An implementation of this interface must be provided.
 */
@Path("/apis")
public interface KeyValueStore {
  @Path("/keyvaluestore/v1/{storeName}")
  @GET
  @Produces({"application/json", "application/x-value-type", "text/plain"})
  Response getValue(@QueryParam("key") String key, @PathParam("storeName") String storeName);

  @Path("/keyvaluestore/v1/{storeName}")
  @POST
  @Consumes({"application/json", "application/x-value-type", "text/plain"})
  Response postValue(@QueryParam("key") String key, @PathParam("storeName") String storeName,
      InputStream data);

  @Path("/keyvaluestore/v1/{storeName}")
  @DELETE
  @Produces({"application/json", "application/x-value-type", "text/plain"})
  Response delete(@QueryParam("key") String key, @PathParam("storeName") String storeName);
}
