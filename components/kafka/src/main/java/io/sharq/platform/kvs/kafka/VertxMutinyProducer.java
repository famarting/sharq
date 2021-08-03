package io.sharq.platform.kvs.kafka;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import io.vertx.mutiny.core.Vertx;

@ApplicationScoped
public class VertxMutinyProducer {

    @Produces
    public Vertx vertx(io.vertx.core.Vertx v) {
        return Vertx.newInstance(v);
    }

}
