package io.sharq.platform.cloudevents;

public class Target {

    final String host;
    final int port;
    final String path;

    public Target(String path) {
        this(null, null, path);
    }

    public Target(int port, String path) {
        this(null, port, path);
    }

    public Target(String host, Integer port, String path) {
        if (host == null) {
            this.host = "localhost";
        } else {
            this.host = host;
        }
        if (port == null) {
            this.port = 8080;
        } else {
            this.port = port;
        }
        if (path == null) {
            this.path = "/";
        } else {
            this.path = path;
        }
    }

    @Override
    public String toString() {
        return "Target [host=" + host + ", port=" + port + ", path=" + path + "]";
    }

}
