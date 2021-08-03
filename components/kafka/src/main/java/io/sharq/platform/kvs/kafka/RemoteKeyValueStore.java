package io.sharq.platform.kvs.kafka;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;

import org.apache.kafka.streams.state.HostInfo;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sharq.platform.kvs.client.quarkus.KeyValueStore;

@ApplicationScoped
public class RemoteKeyValueStore {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<HostInfo, KeyValueStore> clients = new ConcurrentHashMap<>();

    public InputStream getValue(HostInfo hostInfo, String storeName, String key) {
        KeyValueStore client = clients.computeIfAbsent(hostInfo, this::createClient);
        return client.getValue(storeName, key);
    }

    private KeyValueStore createClient(HostInfo hostInfo) {
        String url = "http://" + hostInfo.host() + ":" + hostInfo.port();
        logger.info("Creating client to remote store {}", url);
        return RestClientBuilder.newBuilder()
            .baseUri(URI.create(url))
            .build(KeyValueStore.class);
    }
}
