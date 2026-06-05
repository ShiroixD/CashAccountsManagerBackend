package org.dev.cash_accounts_manager_backend.exceptions;

import org.dev.cash_accounts_manager_backend.enums.RoleEnum;

/**
 * Custom exception telling that account role has not been found in repository
 *
 * @author Fabian Frontczak
 */
public class UserRoleNotExist extends RuntimeException {
    public UserRoleNotExist(RoleEnum roleEnum) {
        super("Role " + roleEnum + " doesn't exist");
    }
}
