package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.annotation.Nullable;

/**
 * DTO model for account modification request
 *
 * @author Fabian Frontczak
 */
public record UpdateUserDto(
        @Nullable
        String username,

        @Nullable
        String password,

        @Nullable
        String fullName
) { }
