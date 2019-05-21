package com.ghostflow;

public class GhostFlowException extends RuntimeException {

    public GhostFlowException() {
        super();
    }

    public GhostFlowException(String message) {
        super(message);
    }

    public GhostFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public GhostFlowException(Throwable cause) {
        super(cause);
    }
}
