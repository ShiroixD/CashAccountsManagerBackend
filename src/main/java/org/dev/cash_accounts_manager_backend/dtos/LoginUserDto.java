package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO model for account authentication request parameters like username and password
 *
 * @author Fabian Frontczak
 */
public record LoginUserDto(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) { }
