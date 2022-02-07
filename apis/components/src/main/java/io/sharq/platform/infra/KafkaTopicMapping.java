package io.sharq.platform.infra;

public class KafkaTopicMapping {
    
    private String name;
    private String topic;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }

}
