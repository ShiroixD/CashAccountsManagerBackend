package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.dev.cash_accounts_manager_backend.enums.RoleEnum;

import java.util.Date;

/**
 * {@link org.dev.cash_accounts_manager_backend.models.Role} DTO model
 *
 * @author Fabian Frontczak
 */
public record RoleDto(
        @NotNull(message = "Id cannot be null")
        Integer id,

        RoleEnum code,

        @NotBlank(message = "Description cannot be blank")
        String description,

        @NotNull(message = "Created at date cannot be null")
        Date createdAt,

        @NotNull(message = "Updated at date cannot be null")
        Date updatedAt
) { }
