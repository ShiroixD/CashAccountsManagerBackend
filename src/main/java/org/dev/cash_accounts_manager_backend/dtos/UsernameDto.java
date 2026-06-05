package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO model for request parameters containing username
 *
 * @author Fabian Frontczak
 */
public record UsernameDto(
        @NotBlank(message = "Username is required")
        String username
) { }
