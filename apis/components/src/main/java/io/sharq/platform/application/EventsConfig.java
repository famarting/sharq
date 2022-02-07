package io.sharq.platform.application;

import java.util.List;

public class EventsConfig {
    
    private List<String> outbound;
    private List<InboundEventsConfig> inbound;

    public List<String> getOutbound() {
        return outbound;
    }
    public void setOutbound(List<String> outbound) {
        this.outbound = outbound;
    }
    public List<InboundEventsConfig> getInbound() {
        return inbound;
    }
    public void setInbound(List<InboundEventsConfig> inbound) {
        this.inbound = inbound;
    }

}
