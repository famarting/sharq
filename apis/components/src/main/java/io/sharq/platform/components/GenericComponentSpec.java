package io.sharq.platform.components;

import java.util.List;

public class GenericComponentSpec {

    private String type;

    private List<ConfigEntry> config;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ConfigEntry> getConfig() {
        return config;
    }

    public void setConfig(List<ConfigEntry> config) {
        this.config = config;
    }

}
