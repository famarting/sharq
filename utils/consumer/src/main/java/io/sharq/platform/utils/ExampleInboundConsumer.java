package io.sharq.platform.utils;

import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class ExampleInboundConsumer {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @POST
    public void receive(byte[] body, @Context HttpHeaders headers) {
        logger.info("--- new request ---");

        logger.info(
            headers.getRequestHeaders()
                .entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), String.join(", ", e.getValue())))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()))
                .toString()
        );
        logger.info(new String(body));
    }

}
