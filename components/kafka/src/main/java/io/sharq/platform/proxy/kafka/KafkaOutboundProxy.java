package io.sharq.platform.proxy.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response.Status;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sharq.platform.outbound.ComponentException;
import io.sharq.platform.outbound.OutboundProxy;
import io.sharq.platform.outbound.ProxyResponse;

@ApplicationScoped
public class KafkaOutboundProxy implements OutboundProxy {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    @Named("default-kafka-broker")
    Map<String, Object> defaultKafkaConfig;

    @Inject
    @ConfigProperty(name = "produce.timeout.ms")
    long timeout;

    @Inject
    Config config;

    private final Map<String, KafkaComponent<KafkaProducer<String, byte[]>>> components = new ConcurrentHashMap<>(1);

    @Override
    public ProxyResponse send(String componentName, byte[] data, Map<String, String> metadata) {
        KafkaComponent<KafkaProducer<String, byte[]>> component = components.computeIfAbsent(componentName, this::computeComponent);

        String key = metadata.remove("key");

        List<Header> headers = metadata.entrySet()
                    .stream()
                    .map(e -> new RecordHeader(e.getKey(), e.getValue().getBytes()))
                    .collect(Collectors.toList());

        try {
            RecordMetadata recordMeta = component.client.send(new ProducerRecord<String, byte[]>(component.topic, null, key, data, headers))
                .get(timeout, TimeUnit.MILLISECONDS);

            Map<String, String> responseMetadata = new HashMap<>();
            //TODO handle cloud events attributes externally?
            responseMetadata.put("ce-offset", String.valueOf(recordMeta.offset()));

            return new ProxyResponse(responseMetadata, null);

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("", e);
            throw new ComponentException(Status.GATEWAY_TIMEOUT, "error sending kafka message " + e.getMessage(), e);
        }

    }

    private KafkaComponent<KafkaProducer<String, byte[]>> computeComponent(String componentName) {

        String componentPrefix = "sharq.outbound." + componentName + ".";

        Optional<String> topic = config.getOptionalValue(componentPrefix + "topic", String.class);
        if (topic.isEmpty()) {
            throw new ComponentException(Status.NOT_FOUND, "component " + componentName + " not found");
        }

        Map<String, Object> kafkaConfiguration = new HashMap<>(defaultKafkaConfig);

        StreamSupport.stream(config.getPropertyNames().spliterator(), false)
            .filter(name -> name.startsWith(componentPrefix))
            .forEach(name -> {
                String value = config.getValue(name, String.class);
                String kafkaProp = name.substring(componentPrefix.length());
                kafkaConfiguration.put(kafkaProp, value);
            });

        String group = (String) kafkaConfiguration.get(ConsumerConfig.GROUP_ID_CONFIG);
        if (group == null) {
            String s = UUID.randomUUID().toString();
            logger.warn("No `group.id` set in the configuration, generating a random id: {}", s);
            kafkaConfiguration.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        }

        //remove all the custom properties that the platform defines
        kafkaConfiguration.remove("topic");

        return new KafkaComponent<>(topic.get(), new KafkaProducer<>(kafkaConfiguration, new StringSerializer(), new ByteArraySerializer()));
    }

}
