package org.dev.cash_accounts_manager_backend.exceptions;

/**
 * Custom exception telling that user has not been found in repository
 *
 * @author Fabian Frontczak
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public UserNotFoundException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
