package org.dev.cash_accounts_manager_backend.exceptions;

/**
 * Custom exception fields validation errors
 *
 * @author Fabian Frontczak
 */
public class ValidationError extends RuntimeException {
    public ValidationError(String message) {
        super(message);
    }

    public ValidationError(String message, Throwable err) {
        super(message, err);
    }
}
