package com.ghostflow.http.security;

import com.ghostflow.GhostFlowException;

public class GhostFlowAccessDeniedException extends GhostFlowException {
    public GhostFlowAccessDeniedException() {
        super("access denied");
    }
}
