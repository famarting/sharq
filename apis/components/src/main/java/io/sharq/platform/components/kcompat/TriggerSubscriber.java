package io.sharq.platform.components.kcompat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TriggerSubscriber {

    private SinkRef ref;
    private String uri;

    public SinkRef getRef() {
        return ref;
    }

    public void setRef(SinkRef ref) {
        this.ref = ref;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
