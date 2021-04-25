package io.sharq.platform.proxy.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.DefaultBean;
import io.sharq.platform.outbound.OutboundProxyApi;

@ApplicationScoped
public class KafkaProxyProducer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Produces
    public OutboundProxyApi restApi(KafkaOutboundProxy kafka) {
        return new OutboundProxyApi(kafka);
    }

    @Produces
    @DefaultBean
    @ApplicationScoped
//    @Named("default-kafka-broker")
    public Map<String, InboundComponentConfiguration> createKafkaRuntimeConfig() {

        final Config config = ConfigProvider.getConfig();

        String configPrefix = "sharq.inbound.";

        Map<String, Map<String, String>> componentsProperties = new HashMap<>();

        StreamSupport
                .stream(config.getPropertyNames().spliterator(), false)
                .map(String::toLowerCase)
                .map(prop -> prop.replaceAll("[^a-z0-9.]", "."))
                .filter(prop -> prop.startsWith(configPrefix))
                .distinct()
                .sorted()
                .forEach(prop -> {

                    String sub = prop.substring(configPrefix.length());
                    String componentName = sub.substring(0, sub.indexOf("."));

                    Map<String, String> componentProps = componentsProperties.computeIfAbsent(componentName, n -> new HashMap<>());

                    Optional<String> value = config.getOptionalValue(prop, String.class);
                    if (value.isPresent()) {

                        String componentPrefix = configPrefix + componentName + ".";

                        final String key = prop.substring(componentPrefix.length()).toLowerCase().replaceAll("[^a-z0-9.]", ".");

                        componentProps.put(key, value.get());

                    } else {
                        throw new IllegalStateException("This is not possible");
                    }

                });

        Map<String, InboundComponentConfiguration> componentsConfigs = new HashMap<>();
        componentsProperties.forEach((k, v) -> {
            componentsConfigs.put(k, new InboundComponentConfiguration(k, v));
        });

        return componentsConfigs;
    }

}
