package io.sharq.platform.inbound.mem;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import io.sharq.platform.outbound.OutboundProxyApi;
import io.vertx.mutiny.core.Vertx;

@ApplicationScoped
public class InMemoryProxyProducer {

    @Produces
    public OutboundProxyApi restApi(InMemoryOutboundProxy kafka) {
        return new OutboundProxyApi(kafka);
    }

    @Produces
    public Vertx vertx(io.vertx.core.Vertx v) {
        return Vertx.newInstance(v);
    }

}
