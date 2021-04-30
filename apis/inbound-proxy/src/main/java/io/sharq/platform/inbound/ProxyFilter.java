package io.sharq.platform.inbound;

import java.util.Map;

public class ProxyFilter {

    final Map<String, String> attributes;

    public ProxyFilter(Map<String, String> attributes) {
        this.attributes = attributes;
    }

}
