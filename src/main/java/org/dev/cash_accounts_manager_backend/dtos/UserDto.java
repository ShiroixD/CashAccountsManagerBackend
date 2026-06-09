package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

/**
 * {@link org.dev.cash_accounts_manager_backend.models.User} DTO model
 *
 * @author Fabian Frontczak
 */
public record UserDto(
        @NotNull(message = "Id cannot be null")
        Integer id,

        @NotBlank(message = "Cannot be blank")
        String fullName,

        @NotBlank(message = "Cannot be blank")
        String username,

        @NotBlank(message = "Cannot be blank")
        String password,

        @NotNull(message = "Cannot be null")
        Date createdAt,

        @NotNull(message = "Cannot be null")
        Date updatedAt,

        RoleDto role,
        boolean disabled
) { }
