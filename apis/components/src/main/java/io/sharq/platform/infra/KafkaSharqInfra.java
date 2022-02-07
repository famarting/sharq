package io.sharq.platform.infra;

public class KafkaSharqInfra {
    
    private String bootstrapServers;
    private KafkaSharqInfraTopicMappings topicMappings;

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public KafkaSharqInfraTopicMappings getTopicMappings() {
        return topicMappings;
    }

    public void setTopicMappings(KafkaSharqInfraTopicMappings topicMappings) {
        this.topicMappings = topicMappings;
    }
    
}
