package io.sharq.platform.components.kcompat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Trigger {

    private TriggerSpec spec;
    private TriggerStatus status;

    public TriggerSpec getSpec() {
        return spec;
    }
    public void setSpec(TriggerSpec spec) {
        this.spec = spec;
    }
    public TriggerStatus getStatus() {
        return status;
    }
    public void setStatus(TriggerStatus status) {
        this.status = status;
    }

}
