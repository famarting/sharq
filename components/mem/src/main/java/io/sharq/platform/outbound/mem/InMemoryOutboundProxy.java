package io.sharq.platform.outbound.mem;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sharq.platform.outbound.OutboundProxy;
import io.sharq.platform.outbound.ProxyResponse;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.mutiny.core.Vertx;

@ApplicationScoped
public class InMemoryOutboundProxy implements OutboundProxy {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    Vertx vertx;

    @Override
    public ProxyResponse send(String componentName, byte[] data, Map<String, String> metadata) {

        logger.info("Forwarding message to vertx event bus");

        DeliveryOptions opts = new DeliveryOptions();
        if (metadata != null) {
            metadata.forEach((k,v) -> opts.addHeader(k, v));
        }

        vertx.eventBus().publish(componentName, data, opts);

        //TODO optional? implement request/response

        return new ProxyResponse();
    }

}
