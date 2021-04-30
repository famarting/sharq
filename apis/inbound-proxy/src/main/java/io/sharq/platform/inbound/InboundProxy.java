package io.sharq.platform.inbound;

import java.util.Map;
import java.util.stream.Collectors;

import io.sharq.platform.cloudevents.CloudEventsHttpClient;
import io.sharq.platform.cloudevents.Target;
import io.sharq.platform.cloudevents.TargetResponse;
import io.smallrye.mutiny.Uni;

public class InboundProxy {

    private final ProxyFilter filter;
    private final CloudEventsHttpClient client;

    public InboundProxy(Target target, ProxyFilter filter) {
        this.filter = filter;
        client = new CloudEventsHttpClient(target);
    }

    public Uni<TargetResponse> proxy(byte[] data, Map<String, String> metadata) {

        if (filter != null && filter.attributes != null && !filter.attributes.isEmpty()) {
            Map<String, String> attributes = metadata.entrySet()
                    .stream()
                    .map(e -> {
                        if (e.getKey().startsWith("ce-") ) {
                            return Map.entry(e.getKey().substring("ce-".length()), e.getValue());
                        }
                        return e;
                    })
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

            boolean filterMatches = filter.attributes.entrySet()
                .stream()
                .allMatch(entry -> attributes.containsKey(entry.getKey()) && attributes.get(entry.getKey()).equals(entry.getValue()));

            if (!filterMatches) {
                return Uni.createFrom().nullItem();
            }

        }

        return client.send(data, metadata);

    }

}
