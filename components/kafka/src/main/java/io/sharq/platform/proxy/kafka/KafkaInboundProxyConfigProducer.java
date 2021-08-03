package io.sharq.platform.proxy.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.arc.DefaultBean;
import io.sharq.platform.ComponentConfig;
import io.sharq.platform.ComponentConfigReader;

@ApplicationScoped
public class KafkaInboundProxyConfigProducer {

    @Produces
    @DefaultBean
    @ApplicationScoped
//    @Named("default-kafka-broker")
    public Map<String, InboundComponentConfiguration> createKafkaRuntimeConfig(ComponentConfigReader configReader) {

        String configPrefix = "sharq.inbound.";

        List<ComponentConfig> componentsConfig = configReader.readConfig(configPrefix);

        Map<String, InboundComponentConfiguration> componentsConfigs = new HashMap<>();
        componentsConfig.forEach((component) -> {
            componentsConfigs.put(component.name, new InboundComponentConfiguration(component.name, component.properties));
        });

        return componentsConfigs;
    }

}
