package io.sharq.platform.kvs.infinispan;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.infinispan.client.hotrod.DefaultTemplate;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sharq.platform.kvs.KeyValueStore;


@ApplicationScoped
public class InfinispanKeyValueStore implements KeyValueStore {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    ConcurrentHashMap<String, RemoteCache<String, Object>> caches = new ConcurrentHashMap<String, RemoteCache<String,Object>>(1);

    @Inject
    RemoteCacheManager cacheManager;

    private RemoteCache<String, Object> getCache(String storeName) {
        return caches.computeIfAbsent(storeName, k -> cacheManager.administration().getOrCreateCache(k, DefaultTemplate.DIST_ASYNC));
    }

    @Override
    public Response getValue(String key, String storeName) {
        Object value = getCache(storeName).get(key);
        if (value == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(value, MediaType.APPLICATION_JSON).build();
    }

    @Override
    public Response postValue(String key, String storeName, InputStream data) {
        try {
            String value = IOUtils.toString(data, StandardCharsets.UTF_8);
            getCache(storeName).put(key, value);
            return Response.ok().build();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Response delete(String key, String storeName) {
        getCache(storeName).remove(key);
        return Response.ok().build();
    }

}
