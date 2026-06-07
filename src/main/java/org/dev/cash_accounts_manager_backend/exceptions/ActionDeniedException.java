package org.dev.cash_accounts_manager_backend.exceptions;

/**
 * Custom exception telling that action is not permitted and further execution has been denied
 *
 * @author Fabian Frontczak
 */
public class ActionDeniedException extends RuntimeException {
    public ActionDeniedException(String message) {
        super(message);
    }

    public ActionDeniedException(String message, Throwable err) {
        super(message, err);
    }
}
