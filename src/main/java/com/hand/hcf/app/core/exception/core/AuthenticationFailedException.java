

package com.hand.hcf.core.exception.core;

public class AuthenticationFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AuthenticationFailedException(String username) {
        super("User[username=" + username + "] fails to authenticate");
    }

    public AuthenticationFailedException(Long userID, String username) {
        super("User[userID=" + userID + ", username=" + username + "] fails to authenticate");
    }

}
