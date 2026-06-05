package org.dev.cash_accounts_manager_backend.dtos;

import org.dev.cash_accounts_manager_backend.enums.RoleEnum;

import jakarta.validation.constraints.NotNull;

/**
 * DTO model for account assigned role modification request
 *
 * @author Fabian Frontczak
 */
public record UserRoleUpdate(
        @NotNull(message = "Role is required")
        RoleEnum role
) { }
