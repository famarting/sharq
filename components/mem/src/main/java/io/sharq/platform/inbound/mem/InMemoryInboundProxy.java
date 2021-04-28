package io.sharq.platform.inbound.mem;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;
import io.sharq.platform.inbound.InboundProxy;
import io.sharq.platform.inbound.Target;
import io.sharq.platform.outbound.OutboundProxy;
import io.vertx.mutiny.core.Vertx;

@ApplicationScoped
public class InMemoryInboundProxy {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    Vertx vertx;

    @Inject
    Config config;

    @Inject
    OutboundProxy outboundProxy;

    public void onStart(@Observes StartupEvent ev) {

        String configPrefix = "sharq.inbound.";

        Map<String, Map<String, String>> componentsProperties = new HashMap<>();

        StreamSupport
                .stream(config.getPropertyNames().spliterator(), false)
                .map(String::toLowerCase)
                .map(prop -> prop.replaceAll("[^a-z0-9.]", "."))
                .filter(prop -> prop.startsWith(configPrefix))
                .distinct()
                .sorted()
                .forEach(prop -> {

                    String sub = prop.substring(configPrefix.length());
                    String componentName = sub.substring(0, sub.indexOf("."));

                    Map<String, String> componentProps = componentsProperties.computeIfAbsent(componentName, n -> new HashMap<>());

                    Optional<String> value = config.getOptionalValue(prop, String.class);
                    if (value.isPresent()) {

                        String componentPrefix = configPrefix + componentName + ".";

                        final String key = prop.substring(componentPrefix.length()).toLowerCase().replaceAll("[^a-z0-9.]", ".");

                        componentProps.put(key, value.get());

                    } else {
                        throw new IllegalStateException("This is not possible");
                    }

                });

        logger.info("Going to use this targets {}", componentsProperties);

        var pool = Executors.newCachedThreadPool();

        componentsProperties
            .forEach((name, config) -> {
                String component = config.get("component");
                String host = config.get("host");
                String port = config.get("port");
                String path = config.get("path");
                Target target = new Target(host, port == null ? null : Integer.valueOf(port), path);

                pool.execute(() -> {
                    String componentName = component == null ? name : component;
                    InboundProxy proxy = new InboundProxy(target);
                    vertx.eventBus().<byte[]>consumer(componentName)
                        .handler(m -> {

                            logger.info("Message received from vertx event bus");

                            Map<String, String> metadata = new HashMap<>();
                            m.headers().forEach(h -> metadata.put(h.getKey(), h.getValue()));
                            proxy.send(m.body(), metadata).subscribe()
                                .with(tr -> {
                                    //TODO send the response to the outbound proxy if there is response
                                    if (tr.getData() != null) {
                                        outboundProxy.send(componentName, tr.getData(), tr.getMetadata());
                                    }
                                });


                        });
                });

            });

    }
}
