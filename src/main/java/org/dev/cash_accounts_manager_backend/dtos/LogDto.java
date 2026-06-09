package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.dev.cash_accounts_manager_backend.enums.ActionsEnum;

import java.util.Date;

/**
 * {@link org.dev.cash_accounts_manager_backend.models.Log} DTO model
 *
 * @author Fabian Frontczak
 */
public record LogDto(
        @NotNull(message = "Id cannot be null")
        Integer id,

        ActionsEnum name,

        @NotNull(message = "User cannot be null")
        UserDto user,

        @NotBlank(message = "Object cannot be blank")
        String objects,

        @NotBlank(message = "Description cannot be blank")
        String description,

        @NotNull(message = "Created at date cannot be null")
        Date createdAt
) { }
