package io.sharq.platform.kvs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class KeyValueStoreExceptionMapper implements ExceptionMapper<KeyValueStoreException>{

    @Override
    public Response toResponse(KeyValueStoreException e) {
        return Response.status(e.getStatus()).header("x-error-message", e.getErrorMessage()).build();
    }

}
