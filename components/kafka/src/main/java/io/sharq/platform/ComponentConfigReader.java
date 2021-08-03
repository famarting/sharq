package io.sharq.platform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

@ApplicationScoped
public class ComponentConfigReader {

    public List<ComponentConfig> readConfig(String configPrefix) {

        final Config config = ConfigProvider.getConfig();

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

        return componentsProperties.entrySet()
                .stream()
                .map(e -> new ComponentConfig(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

    }

}
