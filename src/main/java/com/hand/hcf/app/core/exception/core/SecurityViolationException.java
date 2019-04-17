

package com.hand.hcf.app.core.exception.core;

import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

public class SecurityViolationException extends AccessDeniedException {

    public SecurityViolationException(Long userID, String right) {
        super("User[userID=" + userID + "] doesn't have Right[" + right + "]");
    }

    public SecurityViolationException(UUID userOID, UUID entityOID, Class entityClass) {
        super("User[userOID=" + userOID + "] doesn't have access to " + entityClass.getSimpleName() + " [" + entityOID + "]");
    }

    public SecurityViolationException(Long userId, UUID entityOID, Class entityClass) {
        super("User[userId=" + userId + "] doesn't have access to " + entityClass.getSimpleName() + " [" + entityOID + "]");
    }

    public SecurityViolationException(String message) {
        super(message);
    }

    public SecurityViolationException() {
        super("Identity is not consistent");
    }

}
