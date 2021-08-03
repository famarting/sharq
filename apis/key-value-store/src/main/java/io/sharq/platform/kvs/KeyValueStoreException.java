package io.sharq.platform.kvs;

import javax.ws.rs.core.Response.Status;

public class KeyValueStoreException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -7804676138504287031L;

    private Status status;
    private String errorMessage;

    public KeyValueStoreException(Status status, String errorMessage) {
        super(errorMessage);
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public KeyValueStoreException(Status status, String errorMessage, Throwable cause) {
        super(cause);
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public Status getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}