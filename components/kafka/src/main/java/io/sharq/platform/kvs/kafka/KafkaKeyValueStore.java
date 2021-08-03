package io.sharq.platform.kvs.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sharq.platform.kvs.KeyValueStore;
import io.sharq.platform.kvs.KeyValueStoreException;
import io.sharq.platform.outbound.ComponentException;

@ApplicationScoped
public class KafkaKeyValueStore implements KeyValueStore {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    KafkaStreams kstreams;

    @Inject
    RemoteKeyValueStore remoteKeyValueStore;

    @Inject
    @Named("default-kafka-broker")
    Map<String, Object> defaultKafkaConfig;

    @Inject
    Map<String, KafkaStoreConfiguration> storesConfig;

    @Inject
    @ConfigProperty(name = "produce.timeout.ms")
    long timeout;

    @Inject
    @ConfigProperty(name = "quarkus.kafka-streams.application-server")
    String currentHost;

    private final Map<String, KafkaStore> stores = new ConcurrentHashMap<String, KafkaStore>();

    private KafkaProducer<byte[], byte[]> producer;
    private HostInfo currentHostInfo;

//    private void autoCreateTopics() {
//        Set<String> topicNames = new LinkedHashSet<>();
//        topicNames.add(configuration.topic());
//        Map<String, String> topicProperties = new HashMap<>();
//        configuration.topicProperties().entrySet().forEach(entry -> topicProperties.put(entry.getKey().toString(), entry.getValue().toString()));
//        // Use log compaction by default.
//        topicProperties.putIfAbsent(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT);
//        Properties adminProperties = configuration.adminProperties();
//        adminProperties.putIfAbsent(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, configuration.bootstrapServers());
//        try {
//            KafkaUtil.createTopics(adminProperties, topicNames, topicProperties);
//        } catch (TopicExistsException e) {
//            log.info("Topic already exists, skipping");
//        }
//    }

    @PostConstruct
    void init() {
        producer = new KafkaProducer<>(defaultKafkaConfig, new ByteArraySerializer(), new ByteArraySerializer());
        currentHostInfo = HostInfo.buildFromEndpoint(currentHost);
    }

    @Override
    public Response getValue(String key, String storeName) {
        KafkaStore store = getStore(storeName);

        KeyQueryMetadata meta = kstreams.queryMetadataForKey(store.config.name, key.getBytes(), new ByteArraySerializer());

        if (meta.activeHost().equals(currentHostInfo)) {
            byte[] value = store.store.get(key.getBytes());

            if (value == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            return Response.ok(value).build();
        }

        InputStream data = remoteKeyValueStore.getValue(meta.activeHost(), storeName, key);

        return Response.ok(data).build();
    }

    @Override
    public Response postValue(String key, String storeName, InputStream data) {

        KafkaStore store = getStore(storeName);

        try {
            ProducerRecord<byte[], byte[]> r = new ProducerRecord<>(store.config.topic, key.getBytes(), IOUtils.toByteArray(data));

            producer.send(r);
            return Response.noContent().build();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    @Override
    public Response delete(String key, String storeName) {
        KafkaStore store = getStore(storeName);

        ProducerRecord<byte[], byte[]> r = new ProducerRecord<>(store.config.topic, key.getBytes(), null);

        producer.send(r);
        return Response.noContent().build();

    }

    private KafkaStore getStore(String storeName) {
        return stores.computeIfAbsent(storeName, n -> {

            KafkaStoreConfiguration config = storesConfig.get(storeName);
            if (config == null) {
                throw new KeyValueStoreException(Status.NOT_FOUND, "Store " + storeName + " not found");
            }

            ReadOnlyKeyValueStore<byte[], byte[]> store = kstreams.store(StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore()));

            return new KafkaStore(store, config);
        });
    }

}
