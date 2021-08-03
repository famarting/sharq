package org.acme;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.junit.QuarkusTestProfile;

public class SharqQuarkusTestProfile implements QuarkusTestProfile {

    @Override
    public List<TestResourceEntry> testResources() {
        return Arrays.asList(new TestResourceEntry(SharqTestContainer.class));
    }


    public static class SharqTestContainer implements QuarkusTestResourceLifecycleManager {

        GenericContainer sharq;

        @Override
        public Map<String, String> start() {
            sharq = new GenericContainer(
                    DockerImageName.parse("quay.io/famargon/sharq-platform-component-mem:latest"))
                    .withExposedPorts(10001);

            sharq.start();

            int port = sharq.getMappedPort(10001);
            String host = sharq.getHost();
            String addr = "http://" + host + ":" + port;
            return Map.of("sharq.url", addr);
        }

        @Override
        public void stop() {
            sharq.stop();
        }

    }
}
