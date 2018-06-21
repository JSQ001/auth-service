/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * This exception is throw in case of a not activated user trying to authenticate.
 */
public class UserNotActivatedException extends AuthenticationException {

    public UserNotActivatedException(String message) {
        super(message);
    }

    public UserNotActivatedException(String message, Throwable t) {
        super(message, t);
    }
}
