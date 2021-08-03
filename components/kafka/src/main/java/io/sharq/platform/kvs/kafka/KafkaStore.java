package io.sharq.platform.kvs.kafka;

import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

public class KafkaStore {

    final ReadOnlyKeyValueStore<byte[], byte[]> store;
    final KafkaStoreConfiguration config;

    public KafkaStore(ReadOnlyKeyValueStore<byte[], byte[]> store, KafkaStoreConfiguration config) {
        super();
        this.store = store;
        this.config = config;
    }

}
