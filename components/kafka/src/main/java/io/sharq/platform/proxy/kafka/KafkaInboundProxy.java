package io.sharq.platform.proxy.kafka;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response.Status;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.sharq.platform.cloudevents.Target;
import io.sharq.platform.inbound.InboundProxy;
import io.sharq.platform.outbound.ComponentException;

@ApplicationScoped
public class KafkaInboundProxy {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    @Named("default-kafka-broker")
    Map<String, Object> defaultKafkaConfig;

    @Inject
    @ConfigProperty(name = "kafka.consumer.poll.timeout", defaultValue = "1000")
    Integer pollTimeout;

    @Inject
    @ConfigProperty(name = "proxy.timeout.ms", defaultValue = "5000")
    long proxyTimeout;

    @Inject
    Map<String, InboundComponentConfiguration> inboundComponentsConfiguration;

    private ExecutorService pool = Executors.newCachedThreadPool();

    private final Map<String, KafkaComponent<KafkaConsumer<String, byte[]>>> components = new ConcurrentHashMap<>(1);

    boolean stopped = false;

    void onDestroy(@Observes ShutdownEvent ev) throws InterruptedException {
        logger.info("Shutting down");
        stopped = true;
        pool.shutdown();
//        pool.awaitTermination(2, TimeUnit.SECONDS);
    }

    public void onStart(@Observes StartupEvent ev) {
        logger.info("Starting inbound proxy");

//        Map<String, Map<String, String>> targetsConfig = new HashMap<>();

//        StreamSupport.stream(config.getPropertyNames().spliterator(), false)
//            .filter(prop -> {
//                logger.info("{}", prop);
//                return prop.startsWith("sharq.inbound.");
//            })
//            .forEach(prop -> {
//                logger.info("{}", prop);
//
//                Optional<String> value = config.getOptionalValue(prop, String.class);
//                if (value.isPresent()) {
//
//                    String componentName = prop.split(".")[2];
//
//                    String componentPrefix = "sharq.inbound." + componentName + ".";
//
//                    targetsConfig.computeIfAbsent(componentName, n -> new HashMap<>())
//                        .put(prop.substring(componentPrefix.length()), value.get());
//                }
//            });;



        inboundComponentsConfiguration
            .forEach((name, config) -> {

                logger.info("Configuring component {} properties {}", config.componentName, config.kafkaProperties);

                String host = config.kafkaProperties.remove("target.host");
                String port = config.kafkaProperties.remove("target.port");
                String path = config.kafkaProperties.remove("target.path");
                Target target = new Target(host, port == null ? null :Integer.valueOf(port), path);

                KafkaComponent<KafkaConsumer<String, byte[]>> component = components.computeIfAbsent(name, n -> computeComponent(config));

                InboundProxy proxy = new InboundProxy(target, null);

                pool.execute(() -> {
                    runConsumer(component, proxy);
                });


            });

    }

    private void runConsumer(KafkaComponent<KafkaConsumer<String, byte[]>> component, InboundProxy proxy) {
        KafkaConsumer<String, byte[]> consumer = component.client;
        try {

            logger.info("Subscribing to {}", component.topic);

            // Subscribe to the journal topic
            Collection<String> topics = Collections.singleton(component.topic);
            consumer.subscribe(topics);

            // Main consumer loop
            while (!stopped) {
                final ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(pollTimeout));
                if (records != null && !records.isEmpty()) {
                    logger.debug("Consuming {} journal records.", records.count());
                    records.forEach(record -> {

                        Map<String, String> metadata = new HashMap<>();
                        record.headers()
                            .forEach(h -> metadata.put(h.key(), new String(h.value())));

                        pool.execute(() -> {
                            try {
                                proxy.proxy(record.value(), metadata).await().atMost(Duration.ofMillis(proxyTimeout));
                            } catch (Exception e) {
                                logger.warn("Failed to deliver message", e);
                            }
                        });

                    });
                }
            }
        } finally {
            consumer.close();
        }
    }

    private KafkaComponent<KafkaConsumer<String, byte[]>> computeComponent(InboundComponentConfiguration config) {

        String topic = config.kafkaProperties.remove("topic");
        if (topic == null || topic.isEmpty()) {
            throw new ComponentException(Status.NOT_FOUND, "component " + config.componentName + " not found");
        }

        Map<String, Object> kafkaConfiguration = new HashMap<>(defaultKafkaConfig);

//        StreamSupport.stream(config.getPropertyNames().spliterator(), false)
//            .filter(name -> name.startsWith(componentPrefix))
//            .forEach(name -> {
//                String value = config.getValue(name, String.class);
//                String kafkaProp = name.substring(componentPrefix.length());
//                kafkaConfiguration.put(kafkaProp, value);
//            });

        config.kafkaProperties
            .forEach((k,v) -> {
                kafkaConfiguration.put(k, v);
            });

        String group = (String) kafkaConfiguration.get(ConsumerConfig.GROUP_ID_CONFIG);
        if (group == null) {
            String s = UUID.randomUUID().toString();
            logger.warn("No `group.id` set in the configuration, generating a random id: {}", s);
            kafkaConfiguration.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        }

        //remove all the custom properties that the platform defines, if needed

        return new KafkaComponent<>(topic, new KafkaConsumer<>(kafkaConfiguration, new StringDeserializer(), new ByteArrayDeserializer()));
    }

}
