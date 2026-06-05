package org.dev.cash_accounts_manager_backend.dtos;

/**
 * DTO model for account modification request
 *
 * @author Fabian Frontczak
 */
public record UpdateUserDto(
        String username,
        String password,
        String fullName
) { }
