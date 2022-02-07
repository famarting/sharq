package io.sharq.platform.infra;

public class SharqInfraSpec {
    
    KafkaSharqInfra kafka;

    public KafkaSharqInfra getKafka() {
        return kafka;
    }

    public void setKafka(KafkaSharqInfra kafka) {
        this.kafka = kafka;
    }

}
