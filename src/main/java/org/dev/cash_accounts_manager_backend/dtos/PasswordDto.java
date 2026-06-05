package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO model for change password request parameters
 *
 * @author Fabian Frontczak
 */
public record PasswordDto(
        @NotBlank(message = "Password is required")
        String password
) { }
