package org.dev.cash_accounts_manager_backend.exceptions;

/**
 * Custom exception telling that user has not been found in repository
 *
 * @author Fabian Frontczak
 */
public class UserNotFound extends RuntimeException {
    public UserNotFound(String errorMessage) {
        super(errorMessage);
    }

    public UserNotFound(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
