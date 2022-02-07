package io.sharq.platform.infra;

import java.util.List;

public class KafkaSharqInfraEventsMappings {
    
    private List<KafkaTopicMapping> inbound;
    private List<KafkaTopicMapping> outbound;

    public List<KafkaTopicMapping> getInbound() {
        return inbound;
    }
    public void setInbound(List<KafkaTopicMapping> inbound) {
        this.inbound = inbound;
    }
    public List<KafkaTopicMapping> getOutbound() {
        return outbound;
    }
    public void setOutbound(List<KafkaTopicMapping> outbound) {
        this.outbound = outbound;
    }

}
