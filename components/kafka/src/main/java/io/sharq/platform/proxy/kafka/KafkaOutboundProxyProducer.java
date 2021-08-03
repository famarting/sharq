package io.sharq.platform.proxy.kafka;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import io.sharq.platform.outbound.OutboundProxyApi;

@ApplicationScoped
public class KafkaOutboundProxyProducer {

    @Produces
    public OutboundProxyApi restApi(KafkaOutboundProxy kafka) {
        return new OutboundProxyApi(kafka);
    }

}
