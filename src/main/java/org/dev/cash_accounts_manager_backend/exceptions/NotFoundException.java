package org.dev.cash_accounts_manager_backend.exceptions;

/**
 * Custom exception telling that desired data has not been found
 *
 * @author Fabian Frontczak
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable err) {
        super(message, err);
    }
}
