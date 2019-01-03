package com.hand.hcf.app.base.util;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;

public class DecideAuthenticationServiceExceptionUtil {

    public static UserDetails decideAuthenticationServiceException(Exception repositoryProblem) {
        if (repositoryProblem.getMessage().equals("user.not.bind")) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage());
        } else if (repositoryProblem.getMessage().equals("email.is.empty")) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage());
        } else if (repositoryProblem.getMessage().equals("mobile.is.empty")) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage());
        } else if (repositoryProblem.getMessage().equals("user.is.locked")) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage());
        } else if (repositoryProblem.getMessage().equals("user.not.activated")) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage());
        } else if (repositoryProblem.getMessage().equals("get.user.detail.failed")) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage());
        } else if (repositoryProblem.getMessage().equals("code.is.invalid")) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage());
        } else if (repositoryProblem.getMessage().equals("user.login.expired")) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage());
        } else {
            throw new AuthenticationServiceException(
                repositoryProblem.getMessage(), repositoryProblem);
        }
    }
}
