package io.sharq.platform.components.kcompat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TriggerSpec {

    private String broker;
    private TriggerFilter filter;
    private TriggerSubscriber subscriber;

    public String getBroker() {
        return broker;
    }
    public void setBroker(String broker) {
        this.broker = broker;
    }
    public TriggerFilter getFilter() {
        return filter;
    }
    public void setFilter(TriggerFilter filter) {
        this.filter = filter;
    }
    public TriggerSubscriber getSubscriber() {
        return subscriber;
    }
    public void setSubscriber(TriggerSubscriber subscriber) {
        this.subscriber = subscriber;
    }

}
