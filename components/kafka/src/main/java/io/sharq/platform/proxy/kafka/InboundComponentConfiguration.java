package io.sharq.platform.proxy.kafka;

import java.util.Map;

public class InboundComponentConfiguration {

    final String componentName;
    final Map<String, String> kafkaProperties;

    public InboundComponentConfiguration(String componentName, Map<String, String> kafkaProperties) {
        super();
        this.componentName = componentName;
        this.kafkaProperties = kafkaProperties;
    }

}
