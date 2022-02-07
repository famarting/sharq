package io.sharq.platform.application;

public class InboundEventsConfig {
    
    private String name;
    private String sendTo;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSendTo() {
        return sendTo;
    }
    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

}
