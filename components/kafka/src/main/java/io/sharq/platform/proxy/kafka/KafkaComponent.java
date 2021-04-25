package io.sharq.platform.proxy.kafka;

public class KafkaComponent<C> {

    final String topic;
    final C client;

    public KafkaComponent(String topic, C client) {
        this.topic = topic;
        this.client = client;
    }

}
