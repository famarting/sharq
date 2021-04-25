package io.sharq.platform.inbound;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.core.http.HttpClient;
import io.vertx.mutiny.ext.web.client.WebClient;

public class InboundProxy {

    private final Target target;
    private final WebClient client;

    public InboundProxy(Target target) {
        this.target = target;
        Vertx vertx = Vertx.vertx();
        HttpClient httpClient = vertx.createHttpClient(new HttpClientOptions());
        client = WebClient.wrap(httpClient);
    }

    public Uni<TargetResponse> send(byte[] data, Map<String, String> metadata) {

//        ce-specversion: 1.0
//        ce-type: com.example.someevent
//        ce-id: 1234-1234-1234
//        ce-source: /mycontext/subcontext
//        Content-Type: application/json; charset=utf-8

        Map<String, String> headers = metadata.entrySet()
            .stream()
            .map(e -> {
                if (!e.getKey().startsWith("ce-") ) {
                    return Map.entry("ce-" + e.getKey(), e.getValue());
                }
                return e;
            })
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        if (!headers.containsKey("ce-specversion")) {
            headers.put("ce-specversion", "1.0");
        }
        if (!headers.containsKey("ce-type")) {
            headers.put("ce-type", "sharq-platform-event");
        }
        if (!headers.containsKey("ce-id")) {
            headers.put("ce-id", UUID.randomUUID().toString());
        }
        if (!headers.containsKey("ce-source")) {
            headers.put("ce-source", "sharq-platform");
        }
        String contentType = "application/json";
        if (headers.containsKey("ce-datacontenttype")) {
            contentType = headers.get("ce-datacontenttype");
        }
        headers.put("Content-Type", contentType);

        var req = client.post(target.port, target.host, target.path);

        headers.forEach((k, v) -> req.putHeader(k, v));

        return req.sendBuffer(Buffer.buffer(data))
                .map(res -> {

                    //TODO extract this kind of things to a commong module
                    Map<String, String> responseMetadata = res.headers()
                            .entries()
                            .stream()
                            .filter(e -> e.getKey().startsWith("ce-"))
                            .map(e -> Map.entry(e.getKey(), String.join(",", e.getValue())))
                            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

                    Buffer buf = res.body();

                    return new TargetResponse(responseMetadata, buf == null ? null : buf.getBytes());
                });

    }

}
