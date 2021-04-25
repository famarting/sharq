package io.sharq.platform.outbound;

import java.util.Map;

public class ProxyResponse {

    private Map<String, String> metadata;
    private byte[] data;

    public ProxyResponse() {
    }

    public ProxyResponse(Map<String, String> metadata, byte[] data) {
        this.metadata = metadata;
        this.data = data;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }

}
