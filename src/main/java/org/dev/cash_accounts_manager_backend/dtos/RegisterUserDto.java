package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO model for creation of new account request
 *
 * @author Fabian Frontczak
 */
public record RegisterUserDto(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required.")
        @Size(min = 2, max = 100, message = "The length of password must be between 2 and 100 characters")
        String password,

        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 100, message = "The length of full name must be between 2 and 100 characters")
        String fullName
) { }
