package io.sharq.platform.infra;

import java.util.List;

public class KafkaSharqInfraTopicMappings {
    
    private List<KafkaTopicMapping> stores;
    private KafkaSharqInfraEventsMappings evets;

    public List<KafkaTopicMapping> getStores() {
        return stores;
    }
    public void setStores(List<KafkaTopicMapping> stores) {
        this.stores = stores;
    }
    public KafkaSharqInfraEventsMappings getEvets() {
        return evets;
    }
    public void setEvets(KafkaSharqInfraEventsMappings evets) {
        this.evets = evets;
    }

}
