package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO model for account login successful response with generated token and expire time
 *
 * @author Fabian Frontczak
 */
public record LoginResponse(
        @NotBlank(message = "Token cannot be blank")
        String token,

        long expiresIn
) { }
