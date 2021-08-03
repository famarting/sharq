package io.sharq.platform;

import java.util.Map;

public class ComponentConfig {

    public final String name;
    public final Map<String, String> properties;

    public ComponentConfig(String name, Map<String, String> properties) {
        super();
        this.name = name;
        this.properties = properties;
    }

}
