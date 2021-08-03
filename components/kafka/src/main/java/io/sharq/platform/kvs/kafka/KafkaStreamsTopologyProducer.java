package io.sharq.platform.kvs.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.Stores;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.DefaultBean;
import io.sharq.platform.ComponentConfig;
import io.sharq.platform.ComponentConfigReader;

@ApplicationScoped
public class KafkaStreamsTopologyProducer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Produces
    @DefaultBean
    @ApplicationScoped
    public Map<String, KafkaStoreConfiguration> createKafkaRuntimeConfig(ComponentConfigReader configReader) {

        String configPrefix = "sharq.store.";

        List<ComponentConfig> componentsConfig = configReader.readConfig(configPrefix);

        Map<String, KafkaStoreConfiguration> storesConfigs = new ConcurrentHashMap<>();
        componentsConfig.forEach((component) -> {
            String topic = component.properties.get("topic");
            if (topic == null) {
                throw new IllegalArgumentException("component " + component.name + " missing topic");
            }
            storesConfigs.put(component.name, new  KafkaStoreConfiguration(component.name, topic));
        });

        //mandatory to set this from config
//        if (!storesConfigs.isEmpty()) {
//            String storesTopics = storesConfigs.values()
//                    .stream()
//                    .map(c -> c.topic)
//                    .collect(Collectors.joining(","));
//
//                System.setProperty("sharq.internal.store.topics", storesTopics);
//        }

        return storesConfigs;
    }

    @Produces
    public Topology buildTopology(Map<String, KafkaStoreConfiguration> storesConfiguration) {

        Optional<String> topics = ConfigProvider.getConfig().getOptionalValue("sharq.internal.store.topics", String.class);
        if (topics.isPresent()) {
            logger.info("Stores topics: {}", topics.get());
        }

        StreamsBuilder builder = new StreamsBuilder();

        for(KafkaStoreConfiguration storeConfig : storesConfiguration.values()) {
            KStream<byte[], byte[]> stream = builder.stream(storeConfig.topic, Consumed.with(Serdes.ByteArray(), Serdes.ByteArray()));
            stream.toTable(Materialized.as(Stores.persistentKeyValueStore(storeConfig.name)));
        }

//        String storeName = "state-store";
//
//
//        KStream<byte[], byte[]> stream = builder.stream("input-topic", Consumed.with(Serdes.ByteArray(), Serdes.ByteArray()));
//        stream.toTable(Materialized.as(Stores.persistentKeyValueStore(storeName)));



//        builder.addGlobalStore(
//                    Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(storeName), Serdes.ByteArray(), Serdes.ByteArray()),
//                    storeName + "-topic",
//                    Consumed.with(Serdes.ByteArray(), Serdes.ByteArray()),
//                    null);

        return builder.build();
    }

    // Processor that keeps the global store updated.
//    private static class GlobalStoreUpdater<K, V, KIn, KOut> implements Processor<K, V, KIn, KOut> {
//
//        private final String storeName;
//
//        public GlobalStoreUpdater(final String storeName) {
//            this.storeName = storeName;
//        }
//
//        private KeyValueStore<K, V> store;
//
//        @Override
//        public void init(
//            final org.apache.kafka.streams.processor.api.ProcessorContext<KIn, KOut> processorContext) {
//            store = processorContext.getStateStore(storeName);
//        }
//
//        @Override
//        public void process(final Record<K, V> record) {
//            // We are only supposed to put operation the keep the store updated.
//            // We should not filter record or modify the key or value
//            // Doing so would break fault-tolerance.
//            // see https://issues.apache.org/jira/browse/KAFKA-7663
//            store.put(record.key(), record.value());
//        }
//
//        @Override
//        public void close() {
//            // No-op
//        }
//
//    }

}
