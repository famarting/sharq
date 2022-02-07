package io.sharq.platform.application;

import java.util.List;

public class SharqApplicationSpec {
    
    private EventsConfig events;
    private List<String> stores;

    public EventsConfig getEvents() {
        return events;
    }
    public void setEvents(EventsConfig events) {
        this.events = events;
    }
    public List<String> getStores() {
        return stores;
    }
    public void setStores(List<String> stores) {
        this.stores = stores;
    }
    
}
