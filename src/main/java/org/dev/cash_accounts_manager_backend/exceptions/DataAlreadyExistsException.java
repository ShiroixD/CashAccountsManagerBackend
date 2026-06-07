package org.dev.cash_accounts_manager_backend.exceptions;

/**
 * Custom exception telling that data being checked already exists
 *
 * @author Fabian Frontczak
 */
public class DataAlreadyExistsException extends RuntimeException {
    public DataAlreadyExistsException(String message) {
        super(message);
    }

    public DataAlreadyExistsException(String message, Throwable err) {
        super(message, err);
    }
}
