package io.sharq.platform.outbound;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

@Path("/apis/outbound/v1")
public class OutboundProxyApi {

    private OutboundProxy proxy;

    public OutboundProxyApi(OutboundProxy proxy) {
        this.proxy = proxy;
    }

    @Path("/{componentName}")
    @POST
    @Produces("*/*")
    public Response send(@PathParam("componentName") String componentName, InputStream data, @Context  HttpHeaders headers) {

         Map<String, String> metadata = headers.getRequestHeaders()
                 .entrySet()
                 .stream()
                 .filter(e -> e.getKey().startsWith("ce-"))
                 .map(e -> Map.entry(e.getKey(), String.join(",", e.getValue())))
                 .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

         try {
            ProxyResponse response =  this.proxy.send(componentName, IOUtils.toByteArray(data), metadata);


            var builder = Response.ok();

            if (response.getData() != null) {
                builder.entity(response.getData());
            }

            if (response.getMetadata() != null) {
                response.getMetadata()
                .forEach((k, v) -> {
                   builder.header(k, v);
                });
            }

            return builder.build();
         } catch (ComponentException e) {
             return Response.status(e.getStatus()).header("x-error-message", e.getErrorMessage()).build();
         } catch (IOException e) {
             throw new UncheckedIOException(e);
         }

    }

}
