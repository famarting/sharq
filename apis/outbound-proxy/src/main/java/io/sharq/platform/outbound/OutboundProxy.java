package io.sharq.platform.outbound;

import java.util.Map;

public interface OutboundProxy {
    
    ProxyResponse send(String componentName, byte[] data, Map<String, String> metadata);

}
