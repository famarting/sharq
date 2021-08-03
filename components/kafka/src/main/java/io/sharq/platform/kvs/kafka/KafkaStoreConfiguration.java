package io.sharq.platform.kvs.kafka;

public class KafkaStoreConfiguration {

    final String name;
    final String topic;

    public KafkaStoreConfiguration(String name, String topic) {
        super();
        this.name = name;
        this.topic = topic;
    }

}
