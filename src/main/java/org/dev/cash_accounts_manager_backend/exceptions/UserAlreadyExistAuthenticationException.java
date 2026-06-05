package org.dev.cash_accounts_manager_backend.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * Custom exception telling that user with given username already exists during registration attempt
 *
 * @author Fabian Frontczak
 */
public class UserAlreadyExistAuthenticationException extends AuthenticationException {
    public UserAlreadyExistAuthenticationException(String errorMessage) {
        super(errorMessage, null);
    }

    public UserAlreadyExistAuthenticationException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
